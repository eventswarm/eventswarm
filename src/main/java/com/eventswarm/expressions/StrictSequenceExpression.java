/**
 * Copyright 2020 Andrew Berry and other contributors
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
import com.eventswarm.eventset.EventSet;
import com.eventswarm.events.Activity;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * This extends a sequence expression to implement a strict sequence, that is, no intervening events
 *
 * Behaviour mostly the same as the SequenceExpression, except: 
 *   * expression(n) in a sequence is only enabled if it is the first expression OR 
 *     expression(n-1) has a match that immediately precedes the event or activity being 
 *     matched by expression(n) .
 *   * when constructing combinations of matches, non-contiguous combinations are discarded
 * 
 * The considerations for SequenceExpression also apply here.
 * 
 */
public class StrictSequenceExpression extends SequenceExpression {

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(StrictSequenceExpression.class);

    private NavigableSet<Event> eventSequence; // set of events we can see

    /**
     * Initialise with a list of expressions with the default maximum match
     *
     * @param expressions
     */
    public StrictSequenceExpression(List<EventExpression> expressions) {
        super(expressions);
        eventSequence = new TreeSet<Event>();
    }

    /**
     * Maintain a set of events that are "current" (i.e. not subject to upstream removes)
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        eventSequence.add(event); // add first so we can correctly evaluate sequenceExistsTo after matching
        super.execute(trigger, event);
    }

    /**
     * Return true if the expression at the specified index can receive the specified match event
     * 
     * This method overrides the parent to ensure that a match event in the preceding match set immediately
     * precedes (with no intervening events) the proposed match event. 
     */
    @Override
    protected boolean isEnabled(int index, Event event) {
        return super.isEnabled(index, event) && inSequence(index, event);
    }

    /**
     * Determine if there are one or more matches in the preceding expression that immediately precede the specified event
     * 
     * @param index index of expression that generated the match event
     * @param match match event
     * @return true if there is a strict sequence from one or more matches in the preceding expression to the match event
     */
    protected boolean inSequence(int index, Event match) {
        if (index == 0) {
            return true;
        }
        else {
            // check all matches for the preceding expression
            for (Event prev: matchSets.get(index-1)) {
                if (last(prev) == predecessor(first(match))) {
                    // return true if the last event of the match is the immediate predecessor of the first event in the proposed match
                    return true;
                }
            }
            // immediate predecessor not matched
            return false;
        }
    }

    @Override
    public void execute(EventMatchTrigger trigger, Event event) {
        int index = expressions.indexOf(trigger);
        log.debug("Checking index: " + Integer.toString(index));
        EventSet events = matchSets.get(index);
        if (isEnabled(index, event)) {
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
            log.debug("Current match:" + event.toString());
            log.debug("No immediately preceding match in previous expression");
            // expressions.get(index).execute((RemoveEventTrigger) null, event);
            matchSets.get(index).execute((RemoveEventTrigger) null, event);
        }
    }

    /**
     * Remove an event from any match sets and prune any component expression matches that are no longer in strict sequence as a result
     *
     * @param trigger upstream component that triggered the removal
     * @param event event to be removed
     */
    @Override
    public void remove(RemoveEventTrigger trigger, Event event) {
        EventSet prev = null, events;
        eventSequence.remove(event); // remove the event from our context
        for (int i = 0; i < expressions.size(); i++) {
            // first remove expression matches that contain the event
            expressions.get(i).execute(trigger, event);
            events = matchSets.get(i);
            if (prev != null && prev.isEmpty()) {
                // if a preceding list has been emptied, then empty this list
                events.clear();
            } else {
                // ensure that all remaininy matches are still in sequence
                for (Event match : events) {
                    if (!inSequence(i, match)) {
                        // prune any matches for which the invariant is no longer satisfied
                        events.execute(trigger, match);
                    }
                }
            } 
            prev = events;
        }
    }

    /**
     * Returns a subset of the supplied set of events that immediately precede the supplied event in our expression context.
     *
     * This overrides the parent, which checks `isBefore` rather than immediately preceding. 
     *
     * @param events
     * @param event
     * @return
     */
    protected Iterator<Event> predecessors(Iterator<Event> iter, Event event) {
        if (iter == null || !iter.hasNext()) {
            log.debug("No elements in preceding list, so no predecessors");
            return EMPTY_ITER;
        } else {
            ArrayList<Event> result = new ArrayList<Event>();
            while (iter.hasNext()) {
                Event next = iter.next();
                log.debug("Testing if " + next.toString() + " is an immediate predecessor");
                if (last(next) == predecessor(first(event))) {
                    log.debug("Adding event " + next.toString() + " to predecessors");
                    result.add(next);
                } else {
                    log.debug("Event " + next.toString() + " does not immediately precede");
                }
            }
            return result.iterator();
        }
    }

    /**
     * Get the immediate predecessor to an event or return null if no predecessor is present
     * 
     * Note that there are two cases for a null return:
     * 1. The supplied event is not in the sequence
     * 2. The supplied event is first in our sequence
     * 
     * @param event
     * @return the immediate predecessor to the supplied event in our current context or null if no predecessor
     */
    private Event predecessor(Event event) {
        return eventSequence.lower(event);
    }

    /**
     * Get the first atomic event in the supplied event object
     * 
     * @param event
     * @return event if already atomic, or event.first() if it is an activity 
     */
    private Event first(Event event) {
        return event.getClass().isInstance(Activity.class) ? ((Activity) event).first() : event; 
    }

    /**
     * Get the last atomic event in the supplied event object
     * 
     * @param event
     * @return event if already atomic, or event.last() if it is an activity 
     */
    private Event last(Event event) {
        return event.getClass().isInstance(Activity.class) ? ((Activity) event).last() : event; 
    }
}
