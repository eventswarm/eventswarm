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

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;

import java.util.List;

/**
 * Comparator that compares a list of values retrieved from two events, returning true if all corresponding values match.
 *
 * This comparator uses short circuit evaluation, and will return false immediately when non-matching values are found.
 * Thus the comparator is most efficient when the value least likely to match is first.
 * Note that an empty set of value retrievers will always return true.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ValueSetEqualComparator<T> implements Comparator {
    List<ValueRetriever<T>> retrievers;

    public ValueSetEqualComparator(List<ValueRetriever<T>> retrievers) {
        this.retrievers = retrievers;
    }

    @Override
    public boolean matches(Event event1, Event event2) {
        for (ValueRetriever<T> retriever: retrievers) {
            if (!retriever.getValue(event1).equals(retriever.getValue(event2))) {
                return false;
            }
        }
        return true;
    }
}
