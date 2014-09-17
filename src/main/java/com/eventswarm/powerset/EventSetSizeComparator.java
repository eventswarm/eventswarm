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
import com.eventswarm.util.ComparisonResolver;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Implements an EventSet size comparator for a powerset, imposing a deterministic total order on eventsets of equal size.
 *
 * The boolean 'descending' flags determines the sort order. Default order is descending.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventSetSizeComparator<KeyType> implements Comparator<Map.Entry<KeyType,EventSet>> {
    private boolean descending;
    private ComparisonResolver resolver;
    private static Logger logger = Logger.getLogger(EventSetSizeComparator.class);

    public EventSetSizeComparator() {
        this(true);
    }

    public EventSetSizeComparator(boolean descending) {
        super();
        this.descending = descending;
        this.resolver = new ComparisonResolver();
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    /**
     * Order two EventSets from a PowerSet (in a Map.Entry object) in descending (default) or ascending order of size.
     *
     * Since keys are not necessarily comparable, we use the EventSet hashcode for a secondary comparison when size
     * is the same, and a resolver that generates unique IDs for object instances if that also fails to distinguish.
     *
     * If the descending flag on this object instance is false, the ordering is reversed.
     *
     * @param events1
     * @param events2
     * @return
     */
    @Override
    public int compare(Map.Entry<KeyType,EventSet> events1, Map.Entry<KeyType,EventSet> events2) {
        int result;
        if (events1 == events2) {
            result = 0;
        } else {
            result = compareIntegers(events1.getValue().size(), events2.getValue().size());
            if (result == 0) {
                result = compareIntegers(events1.getValue().hashCode(), events2.getValue().hashCode());
                if (result == 0) {
                    logger.info("Resolving clash in hashcodes");
                    result = compareIntegers(resolver.getInt(events1), resolver.getInt(events2));
                }
            }
        }
        return descending ? -result : result;
    }

    private int compareIntegers(int first, int second) {
        if (first < second) return -1;
        else if (second < first) return 1;
        else return 0;
    }
}
