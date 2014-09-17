/**
Copyright 2007-2014 Ensift Pty Ltd as trustee for the Avaz Trust and other contributors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.abstractions.MutableCalculationAbstraction;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

/**
 * Expression class that monitors the value of a calculation abstraction as events are added, firing when the
 * abstraction value exceeds a specified value.
 *
 * This expression performs the comparison <strong>after</strong> updating the abstraction, ensuring that the event
 * that caused the abstraction to exceed the threshold is reported downstream. To ensure correctness, the
 * abstraction should not be updated by any other source.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class AbstractionThresholdExpression extends AbstractEventExpression implements EventExpression {

    public static int MINIMUM_COUNT = 10;

    private MutableCalculationAbstraction abstraction;
    private Value<Number> value;

    private static Logger logger = Logger.getLogger(AbstractionThresholdExpression.class);

    /**
     * Create an abstraction threshold expression that calculates statistics on values retrieved by the retriever,
     * firing when the abstraction has a value greater than the specified value.
     *
     * @param abstraction
     * @param value
     */
    public AbstractionThresholdExpression(MutableCalculationAbstraction abstraction, Value<Number> value) {
        super();
        this.abstraction = abstraction;
        this.value = value;
    }

    /**
     * Create an abstraction threshold expression as above, but with the specified limit on the number of matches held.
     *
     * The default limit is specified in the parent AbstractEventExpression class.
     *
     * @see com.eventswarm.expressions.AbstractEventExpression
     *
     * @param abstraction
     * @param value
     * @param limit
     */
    public AbstractionThresholdExpression(MutableCalculationAbstraction abstraction, Value<Number> value, int limit) {
        super(limit);
        this.abstraction = abstraction;
        this.value = value;
    }

    /**
     * Override the parent method so we can add the event to our abstraction before the match comparison occurs.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        abstraction.execute(trigger, event);
        super.execute(trigger, event);
    }

    /**
     * Override the parent method so we can remove the event from our abstraction
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        abstraction.execute(trigger, event);
        super.execute(trigger, event);
    }

    /**
     * Return true if the abstraction value is greater than the supplied comparison value
     *
     * @param trigger
     * @param event
     * @return
     */
    @Override
    protected boolean matched(AddEventTrigger trigger, Event event) {
        return(abstraction.getValue().doubleValue() > value.getValue().doubleValue());
    }


    /**
     * Make this instance ready for recycling by clearing the abstraction and any matches
     */
    @Override
    public void clear() {
        this.abstraction.clear();
        super.clear();
    }
}
