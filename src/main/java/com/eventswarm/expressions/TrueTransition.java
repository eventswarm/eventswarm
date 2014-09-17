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
 * from false to true.
 *
 * This expression wrapper is useful for expressions over calculated values and
 * other abstractions that often remain true for extended periods, but the
 * transition to true is the most important event.
 *
 * Note that this expression will only be 'true' immediately after a transition.
 * A subsequent event will make this expression false and it will not become
 * true again until a subsequent transition occurs.
 *
 * @see FalseTransition
 *
 * @author andyb
 */
public class TrueTransition extends AbstractEventExpression implements EventMatchAction
{
    private EventExpression expr;
    private boolean wasFalse;        // last state of the nested expression
    private boolean initial = false; // initial value for last state of nested expression
    private boolean isTrue;          // our state
    private Event matchEvent;
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(TrueTransition.class);

    /**
     * Create a wrapper around the supplied expression, with a default initial
     * state of false (i.e. first event that matches nested expression will
     * fire a transition).
     * 
     * @param expr
     */
    public TrueTransition(EventExpression expr) {
        super();
        setup(expr, false);
    }

    /**
     * Create a wrapper around the supplied expression with the supplied
     * initial statue
     *
     * @param expr
     * @param initial
     */
    public TrueTransition(EventExpression expr, boolean initial) {
        super();
        setup(expr, initial);
    }

    private void setup(EventExpression expr, boolean initial) {
        this.expr = expr;
        this.expr.registerAction((EventMatchAction) this); // register so we can capture match events
        this.initial = initial;
        this.wasFalse = !initial;
        this.isTrue = false;
    }

    /**
     * Hide the default constructor
     */
    private TrueTransition() {
        //
    }

    /**
     * Compare the expression state before and after the event is added
     *
     * A null event will be passed onwards, so the result depends on the
     * expression being evaluated.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // expression matches if we transition from false to true
        this.expr.execute(trigger, event);
        boolean isTrue = this.expr.isTrue();
        log.debug("State " + Boolean.toString(!wasFalse) + ", new state " + Boolean.toString(isTrue));
        if (wasFalse && isTrue) {
            log.debug("Have transitioned");
            // we have transitioned, so match
            this.matches.execute(trigger, matchEvent);
            this.fire(matchEvent);
            this.isTrue = true;
        } else {
            // clear our expression match flag since this is not a transition
            this.isTrue = false;
        }
        wasFalse = !isTrue;
    }

    /**
     * Catch matches from the wrapped expression so that we can fire the right match event
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(EventMatchTrigger trigger, Event event) {
        matchEvent = event;
    }

    /**
     * Pass on upstream removes so that downstream expressions that span multiple events work correctly
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        boolean wasTrue = this.expr.isTrue();
        this.expr.execute(trigger, event);
        // if we have transitioned to false because of the event removal, record it and clear isTrue so we can transition again
        if (wasTrue && !this.expr.isTrue()) {
            log.debug("Removal has transitioned expression to false");
            wasFalse = true;
            isTrue = false;
        }
        super.execute(trigger, event);
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
     * Set the wasFalse flag to its initial state
     */
    public void clear() {
        this.wasFalse = !initial;
        this.isTrue = false;
        super.clear();
    }

    /**
     * Clear and call super's reset method
     */
    public void reset() {
        this.clear();
        super.reset();
    }

    public String toString() {
        return ("True transition of " + this.expr.toString());
    }
}
