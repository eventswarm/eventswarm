/**
 * Copyright 2007-2014 Ensift Pty Ltd as trustee for the Avaz Trust and other contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.eventswarm.expressions;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

/**
 * Class that wraps a pipeline of components (single input, single output) so that they look like an expression,
 * with all input events that appear in the output considered to be matches for the expression.
 *
 * We seem to run into this a bit too often: the need to have an expression, but in reality, it is constructed from
 * filters, powersets and component expressions, all leading to a set of matching events. Note that this class only
 * matches events that pass all the way through the pipeline. If new events are created by the pipeline, these
 * might be added to the set of matches but will not "fire" the expression match trigger.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventPipelineExpression extends AbstractEventExpression {
    private AddEventAction input;
    private AddEventTrigger output;
    private boolean triggered = false;

    private static Logger logger = Logger.getLogger(EventPipelineExpression.class);

    public EventPipelineExpression(AddEventAction input, AddEventTrigger output) {
        this.input = input;
        this.output = output;
        setup();
    }

    public EventPipelineExpression(AddEventAction input, AddEventTrigger output, int limit) {
        super(limit);
        this.input = input;
        this.output = output;
        setup();
    }

    /**
     * Configure the output of the pipeline to add events to the set of matches
     */
    private void setup() {
        output.registerAction(this);
        triggered = false;
    }

    /**
     * Send inbound events to input and look for them appearing in the output, firing a match if they make it through
     *
     * TODO: verify that our triggering test is resilient
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        if (!triggered) {
            // coming from the outside, so pass the event into the pipeline and remember we're in here
            logger.debug("New event pushed into pipeline: " + event.toString());
            triggered = true;
            input.execute(this, event);
            triggered = false;
        } else {
            // catching a match from the pipeline, so fire
            logger.debug("Pipeline has generated a match: " + event.toString());
            this.matches.execute(trigger, event);
            fire(event);
        }
    }

    /**
     * Send removed events to input if remove is supported and remove them explicitly from the match set.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (RemoveEventAction.class.isInstance(input)) {
            ((RemoveEventAction) input).execute(trigger, event);
        }
        super.execute(trigger, event);
    }
}
