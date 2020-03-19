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
package com.eventswarm.events;

import com.eventswarm.abstractions.ValueRetriever;

/**
 * Interface implemented by events that contain an event throughput rate in events per second
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface RateEvent extends Event {

    /**
     * Return the recorded rate in events per second
     *
     * @return
     */
    public Double getRate();

    /**
     * Provide a constant retriever that can get the rate as a Double
     */
    public static final ValueRetriever<Double> RATE_RETRIEVER = new ValueRetriever<Double>() {
        public Double getValue(Event event) {
            if (RateEvent.class.isInstance(event)) {
                return ((RateEvent) event).getRate();
            } else {
                return null;
            }
        }
    };

    /**
     * Provide a constant retriever that can get the rate as a Number
     */
    public static final ValueRetriever<Number> RATE_NUMBER_RETRIEVER = new ValueRetriever<Number>() {
        public Double getValue(Event event) {
            if (RateEvent.class.isInstance(event)) {
                return ((RateEvent) event).getRate();
            } else {
                return null;
            }
        }
    };
}
