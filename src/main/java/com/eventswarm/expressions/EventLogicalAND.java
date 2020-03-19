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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import java.util.List;

/**
 * Simplified logical AND class for use with EventExpression components.
 *
 * This class provides an easy-to-use implementation of logical AND when the
 * expression components are EventExpressions associated with a single anonymous
 * label.  It cannot be used where multiple labels are required.
 *
 * To minimise overheads, this LogicalAND removes events from component expressions
 * immediately. As such, subordinate components should not be used for any other
 * purpose (i.e. they are "owned" by the LogicalAND instance).
 *
 * @author andyb
 */
public class EventLogicalAND extends EventLogicalExpression
{

    public EventLogicalAND(List<EventExpression> parts) {
        super(parts);
    }

    public EventLogicalAND(List<EventExpression> parts, int limit) {
        super(parts, limit);
    }

    public EventLogicalAND(EventExpression expr1, EventExpression expr2) {
        super(expr1, expr2);
    }

    protected EventLogicalAND() {
        super();
    }

    @Override
    protected String getJoiner() {
        return "AND";
    }


    /**
     * Add an event and evaluate the AND of the contained expressions.
     *
     * This expression always evaluates true if there are no expressions in the
     * set of expression components.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // pass the event onward to the joined expressions, short circuiting as
        // soon as we find one that doesn't match
        boolean matched = true;
        for (EventExpression expr : this.parts) {
            expr.execute(trigger, event);
            if (!expr.hasMatched(event)) {
                // if not matched, fail on this event and exit the loop to
                // short circuit the evaluation
                matched = false;
                break;
            }
        }

        if (matched) {
            // store matched event and call registered actions
            this.matches.execute(trigger, event);
            this.fire(event);
        }
    }

}
