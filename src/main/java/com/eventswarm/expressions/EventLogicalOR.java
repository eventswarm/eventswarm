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
 * Simplified logical OR class for use with EventExpression components.
 *
 * This class provides an easy-to-use implementation of logical OR when the
 * expression components are EventExpressions associated with a single anonymous
 * label.  It cannot be used where multiple labels are required.
 *
 * To minimise overheads, this LogicalOR removes events from component expressions
 * immediately. As such, subordinate components should not be used for any other
 * purpose (i.e. they are "owned" by the LogicalOR instance).
 *
 * @author andyb
 */
public class EventLogicalOR extends EventLogicalExpression
{

    public EventLogicalOR(List<EventExpression> parts) {
        super(parts);
    }

    public EventLogicalOR(List<EventExpression> parts, int limit) {
        super(parts, limit);
    }

    public EventLogicalOR(EventExpression expr1, EventExpression expr2) {
        super(expr1, expr2);
    }


    protected EventLogicalOR() {
        super();
    }

    // define our joining string for toString
    static {
        joiner = "OR";
    }

    @Override
    protected String getJoiner() {
        return "OR";
    }

    /**
     * Add an event and evaluate the OR of the contained expressions.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // pass the event onward to the component expressions, short circuiting
        // as soon as we find one that matches
        boolean matched = false;
        for (EventExpression expr : this.parts) {
            expr.execute(trigger, event);
            if (expr.hasMatched(event)) {
                // matched, remove the event from component and exit the loop
                matched = true;
                //expr.execute((RemoveEventTrigger) null, event);
                break;
            } else {
                // not matched, continue searching
            }
        }
        
        // store matched event
        if (matched) {
            this.matches.execute(trigger, event);
            this.fire(event);
        }
    }

    /**
     * Propagate removes, just in case
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        // pass the remove onward to the component expressions
        for (EventExpression expr : this.parts) {
            expr.execute(trigger, event);
            // TODO: check for matches caused by remove (e.g. negated expressions)
        }
    }
}
