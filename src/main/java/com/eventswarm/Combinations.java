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
package com.eventswarm;


import com.eventswarm.events.Event;
import java.util.Set;
import java.util.SortedSet;

/**
 * A Combinations represents a set of Combinations expressed as one or more sequences of sets, where any
 * sequence of events taken from the component sets is a valid combination.
 *
 * For example, a Combinations object containing {[{a,b},{c},{d,e}]} represents the combinations:
 * @pre [a,c,d], [a,c,e], [b,c,d], [b,c,e]. Multiple sequences of sets are supported because there are situations
 * where a single sequence of sets cannot deterministically capture all of the Combinations.
 *
 * Sets of events may contain a null to indicate that a null event is valid for that entry in the combination.
 *
 * An empty set of events is considered equivalent to a set containing a single null element. Implementations are
 * permitted to insert a null event into such empty sets. To minimise issues arising from such insertions, it is
 * recommended that creators use sets containing a single null element rather than empty sets.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface Combinations {
    /**
     * Return the set of Combination objects represented by this object.
     *
     * An empty set of events shall be considered equivalent to a set containing a single, null element, thus
     * returned combinations will contain a null for the corresponding element in a combination.
     *
     * @return
     */
    public Set<Combination> getCombinations();

    /**
     * Return the number of Combinations represented by this object.
     *
     * For each list in the set, the number of represented combinations is the product of the sizes of its
     * component sets, that is, L<sub>1</sub>.size * L<sub>2</sub>.size ...
     *
     * The total number of combinations is the sum of combinations for each list.
     *
     * @return
     */
    public int count();

    /****
     * Return the union of all sets of events
     */
    public SortedSet<Event> getEvents();
}
