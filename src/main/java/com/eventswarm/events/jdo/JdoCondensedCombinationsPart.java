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
package com.eventswarm.events.jdo;

import com.eventswarm.Combination;
import com.eventswarm.events.CombinationsPart;
import com.eventswarm.events.Event;

import java.util.*;

/**
 * Persistable implementation of the CombinationsPart based on a condensed set of sets from which an enumeration
 * of Combination objects can be created.
 *
 * This implementation does not require that the length of all lists is the same. As such, combinations returned by
 * getCombinations might contain Combination objects of differing lengths. This might cause problems if ordering is
 * used to associate a combination element with an expression (e.g. a ComplexExpressionMatchEvent), so in these cases, users
 * should ensure that all lists have the same length.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCondensedCombinationsPart extends JdoEventPart implements CombinationsPart  {

    private Set<List<SortedSet<Event>>> source;
    private transient Set<Combination> combinations = null;

    /**
     * Create an empty Combinations object, only required for persistence purposes
     */
    private JdoCondensedCombinationsPart() {
        super();
    }

    /**
     * Convenience constructor to create a Combinations object with a single list, since we expect this to be the
     * most common case.
     *
     * @param list List of sets of events from which to create combinations
     */
    public JdoCondensedCombinationsPart(List<SortedSet<Event>> list) {
        super();
        newSet();
        if (list != null) this.source.add(list);
    }

    /**
     * Create a Combinations object.
     *
     * @param source Set of lists of sets of events from which to create combinations
     */
    public JdoCondensedCombinationsPart(Set<List<SortedSet<Event>>> source) {
        super();
        if (source != null) this.setSource(source);
        else newSet();
    }

    private void newSet() {
        this.source = new HashSet<List<SortedSet<Event>>>();
    }

    private void setSource(Set<List<SortedSet<Event>>> source) {
        this.source = source;
    }

    public Set<List<SortedSet<Event>>> getSource() {
        return this.source;
    }

    /**
     * Return a set containing all combinations represented by this object.
     *
     * This method can be expensive for a large number of combinations so the
     * result is cached because the set of combinations is immutable in this
     * implementation.
     *
     * Subclasses allowing for mutable combinations <strong>must</strong> override
     * this method to prevent such caching.
     *
     * @return a set containing all Combination objects represented by this object
     */
    @Override
    public Set<Combination> getCombinations() {
        if (this.combinations == null) {
            this.combinations = new HashSet<Combination>();
            for (List<SortedSet<Event>> list : this.source) {
                this.combinations.addAll(buildCombinations(list));
            }
        }
        return this.combinations;
    }

    /**
     * Return a count of the number of Combinations represented by this object.
     *
     * This method actually builds the set of combinations, so is no less expensive
     * than retrieving the set using getCombinations().
     *
     * @return number of combinations represented
     */
    @Override
    public int count() {
        return this.getCombinations().size();
    }

    /**
     * Returns a set containing all events in all sets contained in the Combinations object.
     *
     * This method creates a new set containing the events. Callers are encouraged to cache the result since
     * Combinations instances are likely to be immutable. Null values will be removed if present.
     *
     * @return Set of all events referenced by this object
     */
    @Override
    public SortedSet<Event> getEvents() {
        SortedSet<Event> result = new TreeSet<Event>();
        for(List<SortedSet<Event>> list: this.source) {
            if (list != null) {
                for (SortedSet<Event> events : list) {
                    if (events != null) {
                        for (Event event : events ) {
                            if (event != null) result.add(event);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Build the set of combinations that arise from the supplied list
     *
     * This method uses recursion to simplify and clarify the implementation.
     *
     * TODO: unroll the recursion for efficiency, particularly to avoid creating so many 'tail' lists
     *
     * @param list list of sets of events representing a set of combinations
     * @return
     */
    private Set<Combination> buildCombinations(List<SortedSet<Event>> list) {
        Set<Combination> result = new HashSet<Combination>();
        if (list != null) {
            if (list.isEmpty()) {
                // add a single empty combination
                result.add(new JdoCombination());
            } else {
                // construct combination from each event with each tail combination
                Set<Combination> tails = buildCombinations(list.subList(1,list.size()));
                // empty sets are equivalent to a set containing a null (i.e. the combination has a null for that element)
                if (list.get(0).isEmpty()) {
                    addCombinations(result, null, tails);
                } else {
                    for (Event event: list.get(0)) {
                        addCombinations(result, event, tails);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Add the combinations created by joining the specified event with each of the specified tails
     *
     * @param result
     * @param event
     * @param tails
     */
    private void addCombinations(Set<Combination> result, Event event, Set<Combination> tails) {
        for (Combination tail: tails) {
            result.add(new JdoCombination(event, tail));
        }
    }
}
