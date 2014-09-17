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

/**
 * Wrapper class for expressions that should only be evaluated once.
 *
 * This class allows exactly one match for an expression, after which no further
 * events are evaluated until the matching event is removed by an upstream 
 * window or other removal action. It has a reset operation to re-enable
 * matches, if desired.
 *
 * @author andyb
 */
public class EventOnceOnly extends AbstractEventExpression
{
    private EventExpression expr;
    private boolean matched = false;

    /**
     * Create a once-only wrapper around the supplied expression
     * 
     * @param expr
     */
    public EventOnceOnly(EventExpression expr) {
        super();
        this.expr = expr;
    }

    /**
     * Hide the default constructor
     */
    private EventOnceOnly() {
        //
    }

    /**
     * Add an event and evaluate the contained expression, but ignore all events
     * after the first match until a reset is executed.
     *
     * A null event will be passed onwards, so the result depends on the
     * expression being evaluated.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // pass the event onward if we have no previous match
        if (!this.matched) {
            this.expr.execute(trigger, event);
            if (this.expr.hasMatched(event)) {
                // we have a match
                this.matched = true;
                this.matches.add(event);
                this.fire(event);
                // Remove event from the subordinate expression for efficiency
                this.expr.execute((RemoveEventTrigger) null, event);
            } 
        }
    }

    /**
     * Remove should reset the matched flag if the matching event is being removed
     * 
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (this.matches.contains(event)) {
            this.matched = false;
        }
        super.execute(trigger, event);
    }

    /**
     * Getter for the expression associated with this LogicalNOT
     * 
     * @return
     */
    public EventExpression getExpression() {
        return this.expr;
    }

    /**
     * Clear the match state
     */
    public void clear() {
        this.matched = false;
        this.expr.clear();
        super.clear();
    }
    
    /**
     * Reset the expression, including all registered actions
     * 
     */
    public void reset() {
        this.clear();
        super.reset();
    }

    public String toString() {
        return ("ONE MATCH OF " + this.expr.toString());
    }
}
