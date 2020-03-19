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
package com.eventswarm.abstractions;

import com.eventswarm.events.Event;

import java.util.Date;

/**
 * Interface for retrieving a value from an Event
 *
 * The intent of this interface is to provide a way to attach abstractions (e.g. statistical calculations) to
 * values extracted from an event without knowing the structure of the event. An Event would typically have a
 * method to retrieve an instance of ValueRetriever that wraps the appropriate value retrieval method in this standard
 * interface. Often this can be done with an anonymous class.
 *
 * The method should return null if the event type is not recognised.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface ValueRetriever<T extends Object> {
    public T getValue(Event event);

    /**
     * Value retriever instance to return the string name of the event source
     */
    public static ValueRetriever<String>  sourceRetriever = new ValueRetriever<String>() {
        public String getValue(Event event) {
            return event.getHeader().getSource().getId();
        }
    };

    /**
     * Value retriever instance to return the event timestamp
     */
    public static ValueRetriever<Date>  timestampRetriever = new ValueRetriever<Date>() {
        public Date getValue(Event event) {
            return event.getHeader().getTimestamp();
        }
    };
}
