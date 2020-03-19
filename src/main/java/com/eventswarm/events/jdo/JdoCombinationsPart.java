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
 * Persistable implementation of the CombinationsPart based on an enumerated set of Combination objects
 *
 * This implementation does not require that the length of all Combination objects is the same. This might cause
 * problems if ordering is used to associate a combination element with an expression (e.g. a
 * ComplexExpressionMatchEvent), so in these cases, users should ensure that all Combinations have the same length.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCombinationsPart extends JdoEventPart implements CombinationsPart  {

    private Set<Combination> combinations;

    /**
     * Create an empty Combinations EventPart, only required for persistence purposes
     */
    private JdoCombinationsPart() {
        super();
    }

    /**
     * Create a combinations EventPart containing the supplied combinations
     *
     * @param combinations
     */
    public JdoCombinationsPart(Set<Combination> combinations) {
        super();
        this.setCombinations(combinations);
    }

    /**
     * Private setter for use by persistence infrastructure only
     *
     * @param combinations
     */
    private void setCombinations(Set<Combination> combinations) {
        if (combinations == null) {
            this.combinations = new HashSet<Combination>();
        } else {
            this.combinations = combinations;
        }
    }

    /**
     * Return a set containing all combinations held by this object.
     *
     * @return a set containing all Combination objects held by this object
     */
    public Set<Combination> getCombinations() {
        return this.combinations;
    }

    /**
     * Return a count of the number of Combinations represented by this object.
     *
     * This method is somewhat less expensive than getCombinations, but if a set of Combination objects is required,
     * then it is preferable to build that set and then use the size() method on the returned set.
     *
     * @return number of combinations represented
     */
    public int count() {
        return this.combinations.size();
    }

    /**
     * Returns a set containing all events in all sets contained in the Combinations object.
     *
     * This method creates a new set containing the events. Callers are encouraged to cache the result since
     * Combinations instances are likely to be immutable. Null values will be removed if present.
     *
     * @return Set of all events referenced by this object
     */
    public SortedSet<Event> getEvents() {
        SortedSet<Event> result = new TreeSet<Event>();
        for(Combination comb: this.combinations) {
            result.addAll(comb);
        }
        return result;
    }
}
