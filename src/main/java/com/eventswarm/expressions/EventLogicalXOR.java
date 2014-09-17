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
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import java.util.List;

/**
 * Simplified logical XOR class for use with EventExpression components.
 *
 * This class provides an easy-to-use implementation of logical XOR when the
 * expression components are EventExpressions associated with a single anonymous
 * label.  It cannot be used where multiple labels are required.
 *
 * To minimise overheads, this LogicalXOR removes events from component expressions
 * immediately. As such, subordinate components should not be used for any other
 * purpose (i.e. they are "owned" by the LogicalXOR instance).
 *
 * @author andyb
 */
public class EventLogicalXOR extends EventLogicalExpression
{

    public EventLogicalXOR(List<EventExpression> parts) {
        super(parts);
    }

    public EventLogicalXOR(List<EventExpression> parts, int limit) {
        super(parts, limit);
    }

    public EventLogicalXOR(EventExpression expr1, EventExpression expr2) {
        super(expr1, expr2);
    }

    protected EventLogicalXOR() {
        super();
    }

    @Override
    protected String getJoiner() {
        return "XOR";
    }

    /**
     * Add an event and evaluate the XOR of the contained expressions.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // pass the event onward to the joined expressions
        boolean matched = false;
        for (EventExpression expr : this.parts) {
            expr.execute(trigger, event);
            if (expr.hasMatched(event)) {
                // make sure we have at most one match
                if (matched == false) {
                    // first match, continue the loop but remove event from subordinate
                    matched = true;
                    //expr.execute((RemoveEventTrigger) null, event);
                } else {
                    // second match, clear the flag, remove event and short circuit
                    // by exiting loop
                    matched = false;
                    //expr.execute((RemoveEventTrigger) null, event);
                    break;
                }
            } else {
                // not matched, continue the loop
            }
        }

        // Store matched event
        if (matched) {
            this.matches.execute(trigger, event);
            this.fire(event);
        }
    }

}
