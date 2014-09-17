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

import com.eventswarm.eventset.EventSet;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Creates a sorted set of entries over a powerset, allowing a caller to iterate over the Powerset entries in a defined
 * order (e.g. size).
 *
 * Default ordering is by descending size (i.e. largest first). An alternate comparator for sorting can be provided if desired.
 * Ordering is defined at the time of sorted set creation and changes in the underlying eventsets after creation are not captured.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SortedEventSets<KeyType> {
    Powerset<KeyType> pset;
    Comparator<Map.Entry<KeyType,EventSet>> comparator;

    public SortedEventSets(Powerset<KeyType> pset) {
        this(pset, new EventSetSizeComparator<KeyType>());
    }

    public SortedEventSets(Powerset<KeyType> pset, Comparator<Map.Entry<KeyType,EventSet>> comparator) {
        this.pset = pset;
        this.comparator = comparator;
    }

    /**
     * Get a sorted set containing the current entries in the powerset ordered by the comparator.
     *
     * Note that this will return a snapshot of the powerset entries at the time, but the EventSets referenced
     * could be updated after this result is returned (e.g. entries added or removed).
     *
     * @return
     */
    public SortedSet<Map.Entry<KeyType,EventSet>> getEventSets() {
        SortedSet result = new TreeSet<Map.Entry<KeyType,EventSet>>(comparator);
        result.addAll(pset.entrySet());
        return result;
    }
}
