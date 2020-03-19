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
 * Expression implementing > comparison between a value retrieved from a new event and a value held in an abstraction
 * or other predefined source of values.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ValueLessMatcher implements Matcher {
    private Value<Number> value;
    private ValueRetriever<Number> retriever;

    public ValueLessMatcher(Value<Number> value, ValueRetriever<Number> retriever) {
        this.value = value;
        this.retriever = retriever;
    }

    /**
     * Returns true if the value in the event is less than the value supplied in the constructor
     *
     * Note that the numeric values are converted to doubles for the comparison, thus may have be affected by rounding
     */
    public boolean matches(Event event) {
        return (retriever.getValue(event).doubleValue() < value.getValue().doubleValue());
    }
}
