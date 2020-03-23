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
package com.eventswarm.util;

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import com.eventswarm.expressions.Value;

/**
 * Numeric ValueRetriever that returns the difference between a numeric ValueRetriever and a Value instance
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class DifferenceRetriever implements ValueRetriever<Number> {
    private Value<Number> value;
    private ValueRetriever<Number> retriever;
    private boolean absolute = true;

    /**
     * Create a DifferenceRetriever that returns the difference between a supplied Value and a Value retrieved
     * from an event.
     *
     * @param value Value to calculate difference from
     * @param retriever Event from which to retrieve
     * @param absolute if true, return the absolute value only
     */
    public DifferenceRetriever(Value<Number> value, ValueRetriever<Number> retriever, boolean absolute) {
        this.value = value;
        this.retriever = retriever;
        this.absolute = absolute;
    }

    /**
     * Return the difference between the instance value and the value retrieved from the supplied event, applying abs
     * absolute is true.
     *
     * @param event
     * @return
     */
    public Number getValue(Event event) {
        Number value = this.value.getValue();
        Number eventValue = this.retriever.getValue(event);
        if (value != null && eventValue != null) {
            double difference = value.doubleValue() - eventValue.doubleValue();
            if (absolute && difference < 0) difference *= -1.0;
            return difference;
        } else {
            return null;
        }
    }
}
