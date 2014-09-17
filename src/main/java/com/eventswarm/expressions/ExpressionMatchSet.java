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

import com.eventswarm.*;
import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 * Wrapper around an EventSet that receives events via
 * EventMatchTriggers and ComplexExpressionMatchTriggers and makes those
 * events available to downstream components (e.g. alerters)
 * 
 * Implemented as a wrapper so that users can choose how they might buffer 
 * expression matches (e.g. last N, time window). The wrapper can also
 * register a remove action with an upstream event source so that event removals
 * can be propagated.
 *
 * Note that the usefulness of this class is decreased by the introduction of
 * a size limit on the default expression class and the addition of add/remove
 * event triggers.
 *
 * @author andyb
 */
public class ExpressionMatchSet
        implements AddEventTrigger, RemoveEventTrigger, RemoveEventAction,
                   EventMatchAction, ComplexExpressionMatchAction, Iterable<Event>, Clear
{
    private EventSet buffer;
    private int count;
    private static Logger logger = Logger.getLogger(ExpressionMatchSet.class);

    public ExpressionMatchSet(EventSet buffer) {
        this.buffer = buffer;
    }

    public void execute(ComplexExpressionMatchTrigger trigger, ComplexExpressionMatchEvent event) {
        count++;
        logger.debug("Have match " + event.toString());
        this.buffer.execute((AddEventTrigger) this, event);
    }

    public void execute(EventMatchTrigger trigger, Event event) {
        count++;
        logger.debug("Have match " + event.toString());
        this.buffer.execute((AddEventTrigger) this, event);
    }
    
    public void registerAction(AddEventAction action) {
        this.buffer.registerAction(action);
    }

    public void unregisterAction(AddEventAction action) {
        this.buffer.unregisterAction(action);
    }

    public void registerAction(RemoveEventAction action) {
        this.buffer.registerAction(action);
    }

    public void unregisterAction(RemoveEventAction action) {
        this.buffer.unregisterAction(action);
    }

    public void execute(RemoveEventTrigger trigger, Event event) {
        this.buffer.execute(trigger, event);
    }

    public Iterator<Event> iterator() {
        return this.buffer.iterator();
    }

    @Override
    public void clear() {
        this.buffer.clear();
        this.count = 0;
    }

    /**
     * Return match count
     * 
     * @return
     */
    public int getCount() {
        return this.count;
    }
}
