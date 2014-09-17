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

import java.util.Map;

/**
 * Event class for events created from a CSV stream.
 *
 * A CSVEvent contains a header and a map of field names to field values
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface CSVEvent extends Event {

    /**
     * Name of part containing map of CSV fields
     */
    public static final String MAPPART = "MAP";

    /**
     * Return the CSV field value having the specified field name
     *
     * @param field
     * @return
     */
    public String get(String field);

    /**
     * Return the full Map of CSV fields in the event
     *
     * @return
     */
    public Map<String, String> getCsvMap();


    /**
     * Static ValueRetriever class to retrieve the string value of any key
     */
    public static class CSVRetriever implements ValueRetriever<String> {
        private String key;

        /**
         * Create a value retriever to retrieve 'key' values.
         *
         * @param key
         */
        public CSVRetriever(String key) {
            this.key = key;
        }

        @Override
        public String getValue(Event event) {
            return CSVEvent.class.isInstance(event) ? ((CSVEvent)event).get(key) : null;
        }
    }

    /**
     * Static ValueRetriever class to return a Long value parsed from the string at a nominated key.
     *
     * For ease of use with statistics and other abstractions, this is a Number retriever rather than a Long retriever.
     */
    public static class CSVLongRetriever implements ValueRetriever<Number> {
        private String key;

        /**
         * Create a value retriever to retrieve 'key' values.
         *
         * @param key
         */
        public CSVLongRetriever(String key) {
            this.key = key;
        }

        @Override
        public Number getValue(Event event) {
            return CSVEvent.class.isInstance(event) ? (Long.parseLong(((CSVEvent)event).get(key))) : null;
        }
    }

    /**
     * Static ValueRetriever class to return a Double value parsed from the string at a nominated key.
     *
     * For ease of use with statistics and other abstractions, this is a Number retriever rather than a Double retriever.
     */
    public static class CSVDoubleRetriever implements ValueRetriever<Number> {
        private String key;

        /**
         * Create a value retriever to retrieve 'key' values.
         *
         * @param key
         */
        public CSVDoubleRetriever(String key) {
            this.key = key;
        }

        @Override
        public Number getValue(Event event) {
            return CSVEvent.class.isInstance(event) ? (Double.parseDouble(((CSVEvent)event).get(key))) : null;
        }
    }
}
