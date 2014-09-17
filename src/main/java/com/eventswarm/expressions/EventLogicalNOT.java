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
import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;

/**
 * Logical NOT class for use with Expressions, with the expression being true when the most recent event
 * added *did not* result in the nested expression firing.
 *
 * This expression will pass events that *do not* cause the nested expression to fire to EventMatchAction
 * listeners.  Be aware that for complex expressions (i.e. where an multiple events are matched),
 * the negation of those expressions does not give you the current set of events held in the complex expression,
 * only the last event added.  We might consider adding this capability in the future.
 *
 * Note that removes are passed onward to the nested expression but will not cause this expression to fire or change
 * its state. There is potential for some semantic anomalies if remove changes the state of the nested expression
 * (i.e. false -> true or true -> false) (which is only possible for complex expressions or expressions
 * that monitor an abstraction over multiple events). We have no 'most recent event added' in this case.
 *
 * TODO: work out what to do when nested expressions can change state on remove
 *
 * @author andyb
 */
public class EventLogicalNOT 
        extends AbstractEventExpression implements EventMatchAction
{
    private EventExpression expr;
    private boolean exprMatched;

    /**
     * Create a negation of the provided expression.
     * 
     * The provided expression will be modified and should be exclusively
     * available to this negation (i.e. don't re-use elsewhere) and should not
     * have any listeners for its EventMatchAction.
     *
     * A null expression will never be evaluated and the LogicalNOT will always
     * be false as a result.
     * 
     * @param expr
     */
    public EventLogicalNOT(EventExpression expr) {
        super();
        setupExpr(expr);
    }

    /**
     * As per expression-only constructor, but setting the specified limit on
     * the number of matches held.
     *
     * @param expr
     */
    public EventLogicalNOT(EventExpression expr, int limit) {
        super(limit);
        setupExpr(expr);
    }

    /**
     * Hold a reference to the nested expression and listen for matches, giving ComplexExpression matches
     * precedence over EventExpression matches.
     *
     * @param expr
     */
    private void setupExpr(EventExpression expr) {
        this.expr = expr;
        this.exprMatched = true; // initial state is false (i.e. inverse of nested expression)
        if (null != expr) {
            expr.registerAction((EventMatchAction) this);
        }
    }

    /**
     * Hide the default constructor
     */
    private EventLogicalNOT() {
        //
    }

    /**
     * Add an event and catch the result from the nested expression.
     *
     * A null event will be passed onwards, so the result depends on the
     * expression being evaluated.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // pass the event onward to the negated expression
        if (this.expr != null) {
            this.exprMatched = false;
            this.expr.execute(trigger, event);
            if (this.isTrue()) {
                this.matches.execute(trigger, event);
                this.fire(event);
            }
        }
    }

    /**
     * Override to pass removes onward to the downstream expression
     *
     * Note that removes do not cause this expression to fire
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (this.expr != null) {
            expr.execute(trigger, event);
            super.execute(trigger, event);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    /**
     * If the nested expression fires, then we know the event made the expression true
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(EventMatchTrigger trigger, Event event) {
        this.exprMatched = true;
    }

    /**
     * Return the current state of the expression, i.e. the state after the last event was added
     *
     * @return
     */
    @Override
    public boolean isTrue() {
        return !this.exprMatched;
    }

    /**
     * Getter for the expression associated with this LogicalNOT
     * 
     * @return
     */
    public EventExpression getExpression() {
        return this.expr;
    }

    public String toString() {
        return ("NOT " + this.expr.toString());
    }
}
