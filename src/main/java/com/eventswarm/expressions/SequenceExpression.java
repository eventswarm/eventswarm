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
import com.eventswarm.Combination;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.CombinationsPart;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.*;
import com.eventswarm.eventset.EventSet;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * A sequence expression matches a sequence event expressions in order.
 *
 * Expressions in the sequence cannot be fed events externally: the delivery of events to these expressions
 * must be controlled by the sequence expression. Locking is used to ensure thread safety, so any use of threads
 * within component expressions will potentially cause deadlock or other concurrency issues.
 *
 * The set of matches for each expression is maintained with the following invariants:
 *
 * {@code
 * 1. A match set cannot contain any events that are older than the first event in the preceding match set
 * 2. A match set cannot contain any events if the preceding set is empty.
 * }
 *
 * This ensures that when the last set in the list of match sets contains an event, then the expression has matched.
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
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SequenceExpression extends ANDExpression {

    private static Iterator<Event> EMPTY_ITER = new ArrayList<Event>(0).iterator();
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(SequenceExpression.class);

    /**
     * Initialise with a list of expressions
     *
     * The expressions should not be directly connected to any event sources, since the order of delivery of events
     * to each rule must be controlled to ensure correct matching of event sequences. Event sources should be
     * connected to the SequenceExpression instance instead.
     *
     * @param expressions
     */
    public SequenceExpression(List<EventExpression> expressions) {
        super(expressions);
    }

    /**
     * Test if this sequence has been matched
     *
     * Otherwise, the expression will be true if all components of the sequence have at least one matching event.
     * Note that a sequence with no expressions is always true.
     *
     * Considering our invariant, we can return true if the last match set is not empty.
     *
     * WARNING: the superclass method will be called <strong>if</strong> your
     * variable is an instance of a parent class, courtesy of the funky
     * Java method dispatching (nearest rather than most specific). So, always
     * use interface types for your variables!
     *
     * @return True if all components of the sequence have at least one matching event
     */
    @Override
    public boolean isTrue() {
        boolean result;
        lock.readLock().lock();
        try {
            // true if the last match set is not empty we have no expressions to match
            result = (expressions.size() == 0 || (eventSets.get(eventSets.size()-1)).size() > 0);
        } finally {
            lock.readLock().unlock();
        }
        return result;
    }

    /**
     * Return true if the matchSet identified by the supplied index is enabled and can receive match events
     *
     * A matchSet is enabled if the preceding matchSet is not empty (i.e. events can match an expression
     * if the preceding expression has matches, ensuring the sequence of matches).
     *
     * @param index
     * @return true if match set is enabled
     */
    private boolean isEnabled(int index) {
        return (index != -1 && (index == 0 || !eventSets.get(index-1).isEmpty()));
    }

    /**
     * Receive notifications of component rule matches
     *
     * This method will add the supplied event to the set of matches for a rule if matches are enabled for that rule
     * (i.e. preceding match set is not empty) and the event is strictly after the first event of the preceding set.
     *
     * This method is intended for internal purposes but needs to be public for interface matching. Other
     * components can register to be notified about event matches against particular expression components, but this
     * is not recommended.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(EventMatchTrigger trigger, Event event) {
        int index = expressions.indexOf(trigger);
        EventSet events = expressions.get(index).getMatches();
        if (isEnabled(index) && (index == 0 ||  eventSets.get(index-1).first().isBefore(event))) {
            events.execute((AddEventTrigger) trigger, event);
            log.debug("Index: " + Integer.toString(index) + ", Events: " + events.toString() + ", Trigger: " + event.toString());
            // fire triggers if the event has been added to the last set, indicating new matching combinations exist
            log.debug("Checking if index(" + Integer.toString(index) + ") is the last, that is, " + Integer.toString(expressions.size()-1));
            if (index == (expressions.size()-1)) {
                log.debug("Sequence expression satisfied");
                hasMatched = true;
            }
        } else {
            // remove the match, since we're not using it and it might upset stuff like EventOnceOnly
            expressions.get(index).execute((RemoveEventTrigger) null, event);
        }
    }

    /**
     * Remove an event from any match sets
     *
     * Must ensure our invariant, which is that a match set cannot contain any events that are older than the
     * first event in the preceding match set, and that a match set cannot contain any events if the preceding
     * set is empty. These ensure that when we have at least one event in all sets, we have a sequential match.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void remove(RemoveEventTrigger trigger, Event event) {
        EventSet prev = null;
        EventSet events;
        boolean removed = false;
        for (EventExpression expr : expressions) {
            events = expr.getMatches();
            // we can exit if the list is empty (due to our invariant, all subsequent lists will also be empty)
            if (events.isEmpty()) break;
            if (prev != null && prev.isEmpty()) {
                // if a preceding list has been emptied, then empty this list
                events.clear();
            } else if (removed) {
                // if any events have been removed, get earliest event in preceding list and trim older events
                Event first = prev.first();
                Iterator<Event> iter = events.iterator();
                while (iter.hasNext()) {
                    Event next = iter.next();
                    if (next.isBefore(first) || next == first) {
                        events.execute(trigger, next);
                        removed = true;
                    } else break;
                }
            } else {
                // Otherwise, just remove the event from the set
                events.execute((RemoveEventTrigger) this, event);
                removed = true;
            }
            prev = events;
        }
    }

    /**
     * Create a match event
     */
    @Override
    protected CombinationsPart makeComplexExpressionPart(Event event) {
        return new JdoCombinationsPart(buildCombinations(eventSets.subList(0, eventSets.size()-1), event));
    }


    /**
     * Method to build the list of match combination sets arising from an event
     *
     * The method constructs all combinations from the supplied list of match events that are strictly sequenced
     * (i.e. E_1 < E_2 ... < tail ). If the tail event is null, each event in the last match set is used as tail
     * (i.e. it returns all sequential combinations from the match sets).
     *
     * This method is implemented recursively for simplicity.
     *
     * TODO: try to take advantage of the fact that we can re-use the combinations on subsequent roots
     * TODO: if efficiency constraints dictate, unroll the recursion
     *
     * @param list current match list (or head of match list if recursing)
     * @return
     */
    private Set<Combination> buildCombinations(List<EventSet> list, Event tail) {
        log.debug("List: " + list + ", Tail: " + tail);
        int size = list.size();
        Iterator<Event> roots;
        Set<Combination> result = new HashSet<Combination>();
        if (list.isEmpty()) {
            // when we reach the end of the list, create a combination using the tail event
            if (tail != null) {
                log.debug("Reached end of list, starting combination with " + tail.toString());
                Combination comb = new JdoCombination();
                comb.add(tail);
                result.add(comb);
            }
        } else {
            if (tail == null) {
                // if we have no tail, then all last events are roots
                roots = list.get(size-1).iterator();
            } else {
                // if we have a tail, then we can only use events that are older (before) than the tail
                roots = predecessors(list.get(size-1).iterator(), tail);
            }
            // iterate through roots, generating the set of combinations that precede the root
            log.debug("Roots: " + roots);
            while(roots.hasNext()){
                for (Combination comb : buildCombinations(list.subList(0,size-1), roots.next())) {
                    result.add(new JdoCombination(comb, tail));
                }
            }
        }
       return result;
    }

    /**
     * Returns a subset of the supplied set of events that strictly precede the supplied event using the isBefore()
     * relationship of events.
     *
     * Note that isBefore understands concurrent events, that is, events with the same timestamp or events that are
     * equal are considered concurrent. Implemented using the headSet method of the supplied set, so events are not
     * copied. An empty set is returned if no events in the set are before the supplied event or the supplied set is
     * empty or null.
     *
     * @param events
     * @param event
     * @return
     */
    private Iterator<Event> predecessors(Iterator<Event> iter, Event event) {
        if (iter == null || !iter.hasNext()) {
            log.debug("No elements in preceding list, so no predecessors");
            return EMPTY_ITER;
        } else {
            ArrayList<Event> result = new ArrayList<Event>();
            // return only events that are strictly before the supplied event
            while (iter.hasNext()) {
                Event next = iter.next();
                log.debug("Testing if " + next.toString() + " is before");
                if (next.isBefore(event)) {
                    log.debug("Adding event " + next.toString() + " to predecessors");
                    result.add(next);
                } else {
                    log.debug("Event " + next.toString() + " is after");
                    break;
                }
            }
            return result.iterator();
        }
    }
}
