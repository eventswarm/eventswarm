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
package com.eventswarm.powerset;

import com.eventswarm.*;
import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.expressions.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Class that evaluates a subset-specific expression for each event added to a powerset using replays rather than
 * registration until a subset reaches a threshold size.
 *
 * This class simplifies the registration of expressions against powersets and also provides the ability to optimise
 * expression creation. We find that for powersets where the number of events per subset is small and very dynamic,
 * the overhead of creating a new expression for each subset is very high. Thus this class allows us to set a threshold
 * value, below which new events are evaluated by explicitly replaying them through a recycled expression instance.
 *
 * Note that the Clear implementation on this class clears references to subsets that have been made "permanent", with
 * the assumption that the powerset to which this instance is attached has also been cleared. Without this behaviour,
 * clear is likely to result in memory leaks. If an instance is attached to multiple powersets, the clear method
 * won't break the behaviour, but might cause existing subsets to be re-evaluated unless all of the attached powersets
 * have been cleared.
 *
 * While the this implementation is thread safe for its own state, downstream clients registering against the match
 * triggers must either ensure that they are thread safe or set the serializedActions flag, since powersets are used
 * as the basis for parallelisation and match triggers can fire concurrently.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class PowersetExpression
        implements PowersetAddEventAction, PowersetRemoveEventAction, RemoveSetAction,
        EventMatchTrigger, ComplexExpressionMatchTrigger, Clear
{
    private ExpressionFactory factory;
    private int regThreshold;
    private HashMap<EventSet, Expression> registered;
    private Map<Expression, MatchHandler> matchHandlers;
    private Boolean isComplexExpr = null;
    private boolean serializedActions = false;

    private Set<EventMatchAction> matchActions = new HashSet<EventMatchAction>();
    private Set<ComplexExpressionMatchAction> complexActions  = new HashSet<ComplexExpressionMatchAction>();

    private static int POOL_SIZE = 5;
    private static Logger logger = Logger.getLogger(PowersetExpression.class);

    /**
     * Rather than registering an expression instance against every subset, this class recycles expressions and replays
     * all current events in the subset through the expression until the subset reaches a threshold size (expression
     * creation is somewhat more expensive that evaluation). Once the threshold is reached, an expression instance is
     * permanently registered against the subset. This constant defines the default threshold.
     */
    public static final int DEFAULT_REGISTER_THRESHOLD = 10;

    /**
     * Create a PowersetExpression using the supplied expression creator and the default registration threshold
     *
     * @param creator
     */
    public PowersetExpression(ExpressionCreator creator) {
        this(creator, DEFAULT_REGISTER_THRESHOLD);
    }

    /**
     * Create a PowersetExpression using the supplied expression creator and the supplied registration threshold
     *
     * @param creator
     */
    public PowersetExpression(ExpressionCreator creator, int regThreshold) {
        this.regThreshold = regThreshold;
        this.registered = new HashMap<EventSet,Expression>();
        this.factory = new Pool(this, creator, POOL_SIZE);
        this.matchHandlers = new HashMap<Expression,MatchHandler>();
        this.isComplexExpr = null;
    }

    /**
     * If the event is in a set that doesn't yet have a registered expression, grab an expression, replay,
     * and make it a permanent registration if we've reached the threshold.
     *
     * @param trigger Powerset that added an event
     * @param es EventSet to which the event was added (already containing the event)
     * @param event The event added
     */
    @Override
    public void execute(PowersetAddEventTrigger trigger, EventSet es, Event event) {
        if (isRegistered(es)) {
            // ignore, we can stay out of this
            logger.debug("Subset already registered, no add action required");
        } else {
            Expression expr = factory.get();
            Iterator<Event> iter = es.iterator();
            Event next;
            MatchHandler handler;
            while (iter.hasNext()) {
                next = iter.next();
                if (next == event) {
                    // if this matches, then our catcher will pass it on to registered expressions
                    handler = matchHandlers.get(expr);
                    handler.enable();
                    expr.execute((AddEventTrigger) es, next);
                    handler.disable();
                } else {
                    expr.execute((AddEventTrigger) es, next);
                }
            }

            // now set this up as a permanent if our size threshold has been exceeded
            if (es.size() > regThreshold) {
                makePermanent(expr, es);
            } else {
                factory.recycle(expr);
            }
        }
    }

    /**
     * Method to determine if the expression being created is a complex one.
     *
     * We assume our expression creator only creates one type of expression, so only check once.
     *
     * @return
     */
    private boolean isComplex() {
        if (isComplexExpr == null) {
            Expression expr = factory.get();
            this.isComplexExpr = (ComplexExpressionMatchTrigger.class.isInstance(expr));
            factory.recycle(expr);
        }
        return isComplexExpr;
    }

    /**
     * @return true if match actions are serialized to ensure thread safety of downstream components
     */
    public boolean isSerializedActions() {
        return serializedActions;
    }

    /**
     * Enable serialised actions, that is, match actions are called in a per-action synchronized block to ensure thread
     * safety
     */
    public void enableSerializedActions() {
        this.serializedActions = true;
    }

    /**
     * Disable serialised actions, that is, match actions might be called concurrently if the upstream powerset is
     * parallelised
     */
    public void disableSerializedActions() {
        this.serializedActions = false;
    }

    /**
     * If the event is in a set that doesn't yet have a registered expression, grab an expression, fill it up,
     * then remove the event to see if we get a match.
     *
     * Implementation is currently blocked due to performance and other concerns.
     *
     * TODO: unblock the implementation so we can support negation and true/false transitions
     *
     * @param trigger
     * @param es
     * @param event
     */
    @Override
    public void execute(PowersetRemoveEventTrigger trigger, EventSet es, Event event) {
        if (isRegistered(es)) {
            // ignore, we can stay out of this
            logger.debug("Subset already registered, no remove action required");
        } else {
            logger.debug("For now we're ignoring remove events because no expressions become true on them");
//            // replay all the events
//            Expression expr = factory.get();
//            // make sure the removed event is included
//            if (!es.contains(event)) {expr.execute ((AddEventTrigger) es, event);}
//            Iterator<Event> iter = es.iterator();
//            Event next;
//            while (iter.hasNext()) {
//                next = iter.next();
//                expr.execute((AddEventTrigger) es, next);
//            }
//
//
//            // remove the specified event and see if we get a match
//            catchMatches(expr);
//            expr.execute((RemoveEventTrigger) es, event);
//            uncatch(expr);
//
//            // now set this up as a permanent if our size threshold has been exceeded
//            if (es.size() > regThreshold) {
//                makePermanent(expr, es);
//            } else {
//                factory.recycle(expr);
//            }
        }
    }

    /**
     * When an eventset is pruned, we need to remove it from our internal structures if it has been registered
     *
     * @param trigger Identifies the source of the trigger
     * @param es The EventSet that has been created
     * @param key The key associated with the EventSet
     */
    @Override
    public void execute(RemoveSetTrigger trigger, EventSet es, Object key) {
        if (isRegistered(es)) {
            Expression expr = registered.get(es);
            registered.remove(es);
            factory.recycle(expr);
        }
    }

    public boolean isRegistered(EventSet es) {
        return this.registered.containsKey(es);
    }

    /**
     * Since our pool of expressions is recycled, we only need to clear registered (permanent) expressions
     */
    @Override
    public void clear() {
        for (Expression expr: this.registered.values()) {
            expr.clear();
        }
    }

    /**
     * Add an expression permanently, continuing to use our MatchHandler so that any new registrations against
     * this PowersetExpression are captured.
     *
     * @param expr
     * @param es
     */
    private void makePermanent(Expression expr, EventSet es) {
        es.registerAction((AddEventAction) expr);
        es.registerAction((RemoveEventAction) expr);
        registered.put(es, expr);
        matchHandlers.get(expr).enable();
    }

    @Override
    public void registerAction(ComplexExpressionMatchAction action) {
        complexActions.add(action);
    }

    @Override
    public void unregisterAction(ComplexExpressionMatchAction action) {
        complexActions.remove(action);
    }

    @Override
    public void registerAction(EventMatchAction action) {
        matchActions.add(action);
    }

    @Override
    public void unregisterAction(EventMatchAction action) {
        matchActions.remove(action);
    }

    /**
     * Inner class to implement a simple expression factory
     */
    private class Pool extends LinkedList<Expression> implements ExpressionFactory {
        private int max;
        private ExpressionCreator creator;
        private PowersetExpression owner;
        //private Logger logger = Logger.getLogger(Pool.class);

        public Pool(PowersetExpression owner, ExpressionCreator creator, int size) {
            super();
            this.max = size;
            this.creator = creator;
            this.owner = owner;
        }

        private void fill() {
            Expression expr;
            MatchHandler handler;
            for (int i = this.size(); i < max; i++) {
                expr = creator.newExpression(owner);
                this.add(expr);
                handler = new MatchHandler(expr);
                matchHandlers.put(expr, handler);
            }
        }

        public MatchHandler getHandler(Expression expr) {
            return matchHandlers.get(expr);
        }

        public synchronized Expression get() {
            if (this.size() == 0) {
                fill();
            }
            return this.pop();
        }

        public synchronized void recycle(Expression expr) {
            logger.debug("Recycling expression instance " + expr.toString());
            expr.clear();
            this.push(expr);
        }
    }

    /**
     * Inner class to handle expression matches with enable/disable to avoid matching intermediate events in replay
     */
    private class MatchHandler implements EventMatchAction, ComplexExpressionMatchAction
    {
        private boolean enabled;
        //private Logger logger = Logger.getLogger(MatchHandler.class);

        public MatchHandler(Expression expr) {
            super();
            if (isComplex()) ((ComplexExpressionMatchTrigger)expr).registerAction((ComplexExpressionMatchAction) this);
            expr.registerAction((EventMatchAction) this);
            this.enabled = false;
        }

        public void enable() {
            enabled = true;
        }

        public void disable() {
            enabled = false;
        }

        /**
         * Call all actions registered against the ComplexExpressionMatchTrigger of the PowersetExpression, serialising
         * actions if the serializedActions flag is set.
         *
         * @param trigger
         * @param event
         */
        @Override
        public void execute(ComplexExpressionMatchTrigger trigger, ComplexExpressionMatchEvent event) {
            if (enabled) {
                for (ComplexExpressionMatchAction action : complexActions) {
                    logger.debug("Reporting complex match: " + event.toString());
                    if (serializedActions) {
                        synchronized(action) {
                            action.execute(trigger, event);
                        }
                    } else {
                        action.execute(trigger, event);
                    }
                }
            }
        }

        /**
         * Call all actions registered against the EventMatchTrigger of the PowersetExpression, serialising
         * actions if the serializedActions flag is set.
         *
         * @param trigger
         * @param event
         */
        @Override
        public void execute(EventMatchTrigger trigger, Event event) {
            if (enabled) {
                for (EventMatchAction action : matchActions) {
                    logger.debug("Reporting event match: " + event.toString());
                    if (serializedActions) {
                        synchronized(action) {
                            action.execute(trigger, event);
                        }
                    } else {
                        action.execute(trigger, event);
                    }
                }
            }
        }
    }
}
