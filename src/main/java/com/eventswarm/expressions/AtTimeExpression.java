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
import com.eventswarm.schedules.Schedule;
import com.eventswarm.schedules.ScheduleAction;
import com.eventswarm.schedules.ScheduleTrigger;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Expression wrapper class that fires if the expression is true when a schedule trigger is received
 * (i.e. at specific points in time)
 *
 * The state of this expression (i.e. isTrue) reflects the state of the wrapped expression at the last
 * clock tick.
 *
 * Created with IntelliJ IDEA
 * User: andyb
 */
public class AtTimeExpression extends AbstractEventExpression implements ScheduleAction, EventMatchAction {
    private Expression expr;
    private Event lastMatch = null;
    private boolean state = false;
    private Date lastTick;

    private static Logger logger = Logger.getLogger(AtTimeExpression.class);

    /**
     * Wrap the provided expression so it only fires on clock ticks, if true
     *
     * @param expr
     */
    public AtTimeExpression(Expression expr) {
        super();
        this.expr = expr;
        expr.registerAction(this);
    }

    /**
     * Wrap the provided expression so it only fires on clock ticks, if true, with the specified limit on the
     * number of matches held both in this class and the subordinate.
     *
     * @param expr
     */
    public AtTimeExpression(int limit, Expression expr) {
        super(limit);
        this.expr = expr;
        if (MatchLimit.class.isInstance(expr)) {
            ((MatchLimit) expr).setLimit(limit);
        }
        expr.registerAction(this);
    }

    /**
     * When receiving an event, pass it on to the subordinate expression
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
       this.expr.execute(trigger, event);
    }

    /**
     * Pass removes to subordinate but do not remove from local matches
     *
     * Note that local matches are not removed because otherwise we would have inconsistent
     * semantics for ComplexEvents.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        expr.execute(trigger, event);
    }

    /**
     * Capture the last match event from the wrapped expression
     *
     * @param trigger
     * @param event
     */
    public void execute(EventMatchTrigger trigger, Event event) {
        lastMatch = event;
    }

    /**
     * Fire this expression using the last match event if the subordinate expression is currently true
     *
     * @param trigger
     * @param time
     */
    public void execute(ScheduleTrigger trigger, Schedule schedule, Date time) {
        lastTick = time;
        if (expr.isTrue()) {
            if (lastMatch == null) {
                logger.warn("Expression is true but no matches have been recorded");
            }
            this.matches.execute((AddEventTrigger) this, lastMatch);
            state = true;
            super.fire(lastMatch);
        } else {
            state = false;
        }
    }

    @Override
    public boolean isTrue() {
        return state;
    }

    public Event getLastMatch() {
        return lastMatch;
    }

    public void setLastMatch(Event lastMatch) {
        this.lastMatch = lastMatch;
    }

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    public Date getLastTick() {
        return lastTick;
    }

    private void setLastTick(Date lastTick) {
        this.lastTick = lastTick;
    }
}

