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
import com.eventswarm.events.CombinationsPart;
import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.*;
import com.eventswarm.eventset.EventSet;
import java.util.concurrent.locks.*;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * An AND expression at least one event against each component expression
 *
 * Component cannot be fed events externally: the delivery of events to these expressions
 * must be controlled by the AND expression. Locking is used to ensure thread safety, so any use of threads
 * within component expressions will potentially cause deadlock or other concurrency issues.
 *
 * Since each component of the sequence can have an arbitrary number of matches, there can be a combinatorial explosion
 * of event combinations that match the sequence when a match occurs, and new matches will typically amplify the
 * already-large number of matching event combinations. Considering these, notifications will <em>only</em> be generated
 * for a new match (i.e. a new event is added to the set of events matching the last element of the expression).
 *
 * Listeners on the EventMatchTrigger will receive the event that caused a new match (i.e. the last event added).
 *
 * Listeners on the ComplexExpressionMatchTrigger will receive a ComplexExpressionMatchEvent that captures all new
 * combinations of events that match in a compact form.
 *
 * The size limits on component expressions effectively limit the number of possible combinations that
 * can be returned for a match, where the maximum is <code>limit^n-1</code> where <code>n</code> is the number of
 * elements in the expression and <code>limit</code> is the size limit of the component expressions.
 *
 * TODO: fix AddEventAction so that it works with nested expressions that generate activity events for matches (e.g. ComplexExpressions)
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ANDExpression extends AbstractEventExpression implements ComplexExpression, EventMatchAction {
    protected List<EventExpression> expressions;
    protected transient ArrayList<EventSet> eventSets;
    private Set<ComplexExpressionMatchAction> complexActions;
    protected ReadWriteLock lock = new ReentrantReadWriteLock();
    protected transient boolean hasMatched;
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(ANDExpression.class);

    /**
     * Initialise with a list of expressions
     *
     * The expressions should not be directly connected to any event sources, since the order of delivery of events
     * to each rule must be controlled to ensure correct matching of event sequences. Event sources should be
     * connected to the SequenceExpression instance instead.
     *
     * @param expressions
     */
    public ANDExpression(List<EventExpression> expressions) {
        super();
        this.complexActions = new HashSet<ComplexExpressionMatchAction>();
        this.expressions = expressions;
        this.eventSets = new ArrayList<EventSet>(expressions.size());
        for (EventExpression expr : expressions) {
            // For convenience, maintain direct pointers to the match sets of each expression
            eventSets.add(expr.getMatches());
            // Register to receive match actions from the component expressions
            expr.registerAction((EventMatchAction) this);
        }
    }

    public Collection<? extends Expression> getParts() {
        return expressions;
    }

    /**
     * Provide an explicit method to retrieve the parts as a list so the sequence is captured.
     *
     * @return
     */
    public List<EventExpression> getPartsAsList() {
        return expressions;
    }

    /**
     * Test if all elements of an expression are true
     *
     * @return True if all components are true
     */
    @Override
    public boolean isTrue() {
        boolean result;
        lock.readLock().lock();
        try {
            // true if none of the components is false
            result = true;
            Iterator<EventExpression> iter = expressions.iterator();
            EventExpression next;
            while (result && iter.hasNext()) {
                next = iter.next();
                result = next.isTrue();
                log.debug("AND expression component " + next.toString() + " is " + Boolean.toString(result));
            }
            log.debug("State of " + this.toString() + "is now " + Boolean.toString(result));
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    /**
     * This method receives events from upstream AddEventTrigger implementations
     * and creates a new complex event if we have matched.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // make sure the event is not already in the set of matches
        if (this.matched(trigger, event)) {
            // Create a ComplexExpressionMatchEvent that captures all of the matching combinations
            // including the newly added event and call actions requesting this ComplexExpressionMatchEvent
            ComplexExpressionMatchEvent cem =
                new JdoComplexExpressionMatchEvent(new JdoComplexExpressionPart(this), makeComplexExpressionPart(event));
            this.matches.execute(trigger, cem);
            this.fire(cem);
            super.fire(event);
        }
    }

    /**
     * Method to feed new events into the expression components
     *
     * To ensure correct matching, the event is delivered to each component expression in order.
     *
     * @param trigger
     * @param event
     */
    @Override
    public boolean matched(AddEventTrigger trigger, Event event) {
        lock.writeLock().lock();
        try {
            hasMatched = false;
            for (Expression expr : expressions) {
                log.debug("Checking for match with " + expr.toString());
                expr.execute(trigger, event);
            }
        } finally {
            lock.writeLock().unlock();
        }
        // this event caused a match if any component matched and the expression is true
        return (hasMatched && this.isTrue());
    }

    /**
     * Receive notifications of component rule matches
     *
     * This method just sets the hasMatched flag if we have a match resulting from the event.
     *
     * @param trigger
     * @param event
     */
    public void execute(EventMatchTrigger trigger, Event event) {
        log.debug("AND expression component " + ((EventExpression) trigger).toString() + " matched on " + event.toString());
        hasMatched = true;
    }


    /**
     * Remove an event from any match sets
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        lock.writeLock().lock();
        try {
            this.remove(trigger, event);
            super.execute(trigger, event);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Method to do the removal of events from this class
     */
    protected void remove(RemoveEventTrigger trigger, Event event) {
        for (Expression expr : this.expressions) {
            expr.execute(trigger, event);
        }
    }

    /**
     * This method returns a copy of the match sets for each expression.
     *
     * @return an (ordered) list of EventSets containing the current matches for the expression components
     */
    public List<EventSet> getMatchEvents() {
        List<EventSet> result = new ArrayList<EventSet>();
        lock.readLock().lock();
        try {
            for (EventExpression expr : this.expressions) {
                result.add(expr.getMatches());
            }
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            super.clear();
            for (Expression expr : expressions) {
                expr.clear();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void reset() {
        lock.writeLock().lock();
        try {
            this.clear();
            super.reset();
            this.complexActions.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void registerAction(ComplexExpressionMatchAction action) {
        complexActions.add(action);
    }

    public void unregisterAction(ComplexExpressionMatchAction action) {
        complexActions.remove(action);
    }

    /**
     * Notify all listeners that a complex match has occurred
     *
     * @param event
     */
    protected void fire(ComplexExpressionMatchEvent event) {
        for (ComplexExpressionMatchAction action : this.complexActions) {
            action.execute(this, event);
        }
    }

    /**
     * Create a match event using a CondensedCombinationsPart
     *
     * For AND expressions, the new combinations made from a newly added event
     * are the combinations consisting of that event and all possible combinations
     * of the other events in the other event sets. Since the same event can
     * appear in more than one match set, we need to check for its presence in
     * each match set.
     */
    protected CombinationsPart makeComplexExpressionPart(Event event) {
        SortedSet<Event> singleton = new TreeSet<Event>(); singleton.add(event);
        ArrayList<SortedSet<Event>> parts = new ArrayList<SortedSet<Event>>(eventSets.size());
        for (EventSet events : eventSets) {
            parts.add(events.getEventSet());
        }
        log.debug("Parts to be combined: " + parts.toString());
        Set <List<SortedSet<Event>>> sets = new HashSet<List<SortedSet<Event>>>();
        // For each EventSet that contains the last matched event
        for (int i=0; i < this.eventSets.size(); i++) {
            if (eventSets.get(i).contains(event)) {
                log.debug("Creating " + parts.toString());
                ArrayList<SortedSet<Event>> clone = new ArrayList<SortedSet<Event>>(parts);
                // replace the current set with a set containing only the new event
                clone.remove(i);
                clone.add(i, singleton);
                log.debug("Adding condensed combination to set: " + clone.toString());
                sets.add(clone);
            }
        }
        log.debug("Creating combinations using " + sets.toString());
        return new JdoCondensedCombinationsPart(sets);
    }
}
