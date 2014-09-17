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
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoActivity;
import com.eventswarm.eventset.AtMostNWindow;
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class implements a wrapper around an EventExpression that counts the number of matches and becomes true
 * when the number of matches hits or exceeds a specified count.
 *
 * Full results are reported through a EventMatchTrigger that carries an Activity event with references to the set
 * of all current matches. Note that this is not a ComplexExpressionMatch event because there is only a single
 * expression being matched.
 *
 * This class does not remove Activity events from the set of matches when the RemoveEventAction is called, thus the
 * limit on the number of matches held is important for memory management.
 *
 * Note that since this expression does not match on a single event, it does not work when nested within the current
 * ANDExpression and SequenceExpression implementations. These will eventually be fixed.
 *
 * @see com.eventswarm.events.Activity
 * @see ANDExpression
 *
 * @author andyb
 */
public class MatchCountExpression extends AbstractEventExpression implements EventMatchAction
{
    private EventExpression expr;
    private EventSet exprMatches;
    private transient AddEventTrigger addTrigger = null;
    private int threshold;
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(MatchCountExpression.class);

    /**
     * Default limit on the number of events that will be included in a match. This will also limit the number of
     * events held within this object (i.e. constrain memory usage). This limit is implemented using an 'AtMostNWindow'
     * for the matched events. If the threshold is set higher than the default, then the limit will be increased to
     * match the threshold.
     */
    public static int EVENT_LIMIT = 10;

    /**
     * Create a wrapper around the supplied expression and watch for the match count to equal or exceed the threshold.
     *
     * @param expr
     */
    public MatchCountExpression(EventExpression expr, int threshold) {
        super();
        setAttrs(expr, threshold, EVENT_LIMIT);
    }

    /**
     * Same as 2-parameter constructor, but with a modified limit on the number of activity events (matches) held
     * (see AbstractEventExpression for default) and modified limit on the number of subordinate events included in
     * matches (see the EVENT_LIMIT constant).
     *
     * @see AbstractEventExpression
     *
     * @param expr
     */
    public MatchCountExpression(EventExpression expr, int threshold, int matchLimit, int eventLimit) {
        super(matchLimit);
        setAttrs(expr, threshold, eventLimit);
    }

    /**
     * Helper for constructor to avoid code duplication
     *
     * @param expr
     * @param threshold
     */
    private void setAttrs(EventExpression expr, int threshold, int eventLimit) {
        eventLimit = threshold > eventLimit ? threshold : eventLimit;
        this.exprMatches = new AtMostNWindow(eventLimit);
        this.threshold = threshold;
        this.expr = expr;
        this.expr.registerAction(this); // catch matches for the wrapped expression
    }

    /**
     * Hide the default constructor
     */
    private MatchCountExpression() {
        //
    }

    /**
     * Pass the event onwards and check the threshold, firing the EventMatchTrigger if the threshold is hit
     *
     * A null event will be passed onwards, so the result depends on the expression being evaluated.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // remember the upstream add trigger and pass onto our nested expression
        this.addTrigger = trigger;
        this.expr.execute(trigger, event); // we catch the match in our execute(EventMatchTrigger ...) implementation
    }

    /**
     * Catch an EventMatchTrigger emitted by the wrapped expression and update our match set
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(EventMatchTrigger trigger, Event event) {
        this.exprMatches.execute(addTrigger, event);
        if (this.isTrue()) {
            log.debug("Count expression is now true: we have " + Integer.toString(exprMatches.size()) + " matches");
            // create an activity event and notify downstream
            Activity activity = new JdoActivity((SortedSet<Event>) this.exprMatches.getEventSet());
            this.matches.execute(addTrigger, activity);
            super.fire(activity);
        }
    }

    /**
     * Remove the event from the local match set and pass on removes to wrapped expression (although
     * the add action should remove them).
     *
     * Note that activity events (matches) created by this object when the threshold is matched are not removed when
     * component events are removed: this class relies on the match limit to constrain memory usage for the set of
     * activities created by this expression.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        this.expr.execute(trigger, event);
        this.exprMatches.execute((RemoveEventTrigger) this, event);
    }

    /**
     * This expression is true when the size of the match set for the contained expression hits or exceeds the threshold.
     *
     * @return
     */
    @Override
    public boolean isTrue() {
        return this.exprMatches.size() >= this.threshold;
    }

    /**
     * Returns true if the last event added made the expression true
     *
     * This method is slightly misleading, since the expression is only true when multiple events have been matched
     *
     * @param event
     * @return
     */
    @Override
    @Deprecated
    public boolean hasMatched(Event event) {
        return (this.isTrue() && this.exprMatches.contains(event));
    }

    /**
     * Clear match set, pass on clear to wrapped expression, and call super clear method
     */
    public void clear() {
        this.expr.clear();
        this.exprMatches.clear();
        super.clear();
    }

    /**
     * Clear and call both super and wrapped expression reset methods
     */
    public void reset() {
        this.expr.reset();
        this.clear();
        super.reset();
    }

    public String toString() {
        return ("True when " + this.expr.toString() + " has >= " + Integer.toString(threshold) + " matches");
    }

    /* getters and setters, setters private: only for persistence */
    public EventExpression getExpr() {
        return expr;
    }

    private void setExpr(EventExpression expr) {
        this.expr = expr;
    }

    public int getThreshold() {
        return threshold;
    }

    private void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
