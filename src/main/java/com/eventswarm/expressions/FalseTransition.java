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

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

/**
 * Wrapper class for expressions that only fires when the expression transitions
 * from true to false.
 *
 * This expression wrapper is useful for expressions over calculated values and
 * other abstractions that often remain true for extended periods and the
 * transition to false an important event.
 *
 * Note that this expression will only be 'true' immediately after a transition.
 * A subsequent event will make this expression false and it will not become
 * true again until another transition occurs.
 *
 * @see TrueTransition
 * 
 * @author andyb
 */
public class FalseTransition extends AbstractEventExpression
{
    private EventExpression expr;
    private boolean wasTrue;         // was the last state of the nested expression true?
    private boolean initial = false; // initial value of last state of nested expression
    private boolean isTrue;          // our expression state
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(FalseTransition.class);

    /**
     * Create a wrapper around the supplied expression, with a default initial
     * state of false (i.e. the expression must transition to true then false
     * again before it fires).
     * 
     * @param expr
     */
    public FalseTransition(EventExpression expr) {
        super();
        this.expr = expr;
        this.wasTrue = initial;
        this.isTrue = false;
    }

    /**
     * Create a wrapper around the supplied expression with the supplied
     * initial statue
     *
     * @param expr
     * @param initial
     */
    public FalseTransition(EventExpression expr, boolean initial) {
        super();
        this.expr = expr;
        this.initial = initial;
        this.wasTrue = initial;
        this.isTrue = false;
    }


    /**
     * Hide the default constructor
     */
    private FalseTransition() {
        //
    }

    /**
     * Add compare the expression state before and after the event is added
     *
     * A null event will be passed onwards, so the result depends on the
     * expression being evaluated.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // expression matches if we transition from true to false
        this.expr.execute(trigger, event);
        boolean exprTrue = this.expr.isTrue();
        log.debug("FalseTransition: state " + Boolean.toString(wasTrue) + ", new state " + Boolean.toString(exprTrue));
        if (this.wasTrue && !exprTrue) {
            log.debug("FalseTransition: have transitioned");
            // we have transitioned, so match
            this.matches.execute(trigger, event);
            this.fire(event);
            this.isTrue = true;
        } else {
            // always clear our match state when there is no transition
            this.isTrue = false;
        }
        // remove the event from the expression for efficiency
        this.expr.execute((RemoveEventTrigger) null, event);
        this.wasTrue = exprTrue;
    }

    /**
     * This expression should only be considered true when a transition has just
     * occurred.
     *
     * As soon as a new event occurs after a transition, this will return false
     * 
     * @return
     */
    @Override
    public boolean isTrue() {
        return this.isTrue;
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
     * Set the wasTrue flag to its initial state and set matched state to false
     */
    public void clear() {
        this.wasTrue = initial;
        this.isTrue = false;
        super.clear();
    }

    public String toString() {
        return ("True transition of " + this.expr.toString());
    }
}
