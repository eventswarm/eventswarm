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

/**
 * Comparator that compares the result of retrieving a value from two events and returns the negation of the comparison
 *
 * i.e. return retrieve(event1) != retrieve(event2)
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ValueNotEqualComparator<T> implements Comparator {
    ValueRetriever<T> retriever;

    public ValueNotEqualComparator(ValueRetriever<T> retriever) {
        this.retriever = retriever;
    }

    public boolean matches(Event event1, Event event2) {
        return !retriever.getValue(event1).equals(retriever.getValue(event2));
    }
}
