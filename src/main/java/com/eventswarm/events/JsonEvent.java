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
import org.apache.log4j.Logger;

/**
 * Event interface for Json events, just allowing us to retrieve each attribute of the Json by key.
 *
 * Simple paths using the PATH_SEPARATOR between name elements must be supported
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface JsonEvent<Impl> extends Event {

    /** separator between name elements in a path */
    public static String PATH_SEPARATOR="/";

    /** default part name for the JsonPart of a JsonEvent */
    public static String JSON_PART_NAME="JSON";

    /** EventSwarm header fields should be found in a top level object with this name */
    public static String JSON_HEADER_OBJECT = "EventSwarm";

    /** Eventswarm header event id */
    public static String JSON_EVENT_ID = "eventId";
    /** Eventswarm header timestamp */
    public static String JSON_TIMESTAMP = "timestamp";
    /** Eventswarm header source name */
    public static String JSON_SOURCE_ID = "source";
    /** EventSwarm header sequence number */
    public static String JSON_SEQUENCE_NO = "sequenceNumber";


    public static Logger logger = Logger.getLogger(JsonEvent.class);

    /**
     * Get the named object or attribute from the Json object encapsulated by the event
     *
     * @param path
     * @return
     */
    public Impl getJsonObject(String path);

    public int getInt(String path);

    public boolean getBoolean(String path);

    public double getDouble(String path);

    public long getLong(String path);

    public String getString(String path);

    public boolean has(String path);

    public String getJsonString();

    public Object get(String path);

    /**
     * Retriever class for retrieving integer attribute values
     */
    public static class IntegerRetriever implements ValueRetriever<Integer> {
        private String path;

        public IntegerRetriever(String path) {
            this.path = path;
        }

        @Override
        public Integer getValue(Event event) {
            try {
                return (JsonEvent.class.isInstance(event) ? ((JsonEvent) event).getInt(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving long attribute values
     */
    public static class LongRetriever implements ValueRetriever<Long> {
        private String path;

        public LongRetriever(String path) {
            this.path = path;
        }

        @Override
        public Long getValue(Event event) {
            try {
                return (JsonEvent.class.isInstance(event) ? ((JsonEvent) event).getLong(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving String attribute values
     */
    public static class StringRetriever implements ValueRetriever<String> {
        private String path;

        public StringRetriever(String path) {
            this.path = path;
        }

        @Override
        public String getValue(Event event) {
            try {
                return (JsonEvent.class.isInstance(event) ? ((JsonEvent) event).getString(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving String attribute values folded to lower case for case-insensitive comparisons
     */
    public static class DowncaseStringRetriever implements ValueRetriever<String> {
        private String path;

        public DowncaseStringRetriever(String path) {
            this.path = path;
        }

        @Override
        public String getValue(Event event) {
            try {
                return (JsonEvent.class.isInstance(event) ? ((JsonEvent) event).getString(path).toLowerCase() : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving Double attribute values
     */
    public static class DoubleRetriever implements ValueRetriever<Double> {
        private String path;

        public DoubleRetriever(String path) {
            this.path = path;
        }

        @Override
        public Double getValue(Event event) {
            try {
                return (JsonEvent.class.isInstance(event) ? ((JsonEvent) event).getDouble(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving boolean attribute values
     */
    public static class BooleanRetriever implements ValueRetriever<Boolean> {
        private String path;

        public BooleanRetriever(String path) {
            this.path = path;
        }

        @Override
        public Boolean getValue(Event event) {
            try {
                return (JsonEvent.class.isInstance(event) ? ((JsonEvent) event).getBoolean(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving the string representation of an object at the specified path
     */
    public static class ObjectAsStringRetriever implements ValueRetriever<String> {
        private String path;

        public ObjectAsStringRetriever(String path) {
            this.path = path;
        }

        @Override
        public String getValue(Event event) {
            try {
                return (JsonEvent.class.isInstance(event) ? ((JsonEvent) event).get(path).toString() : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving value at path " + path, exc);
                return null;
            }
        }
    }


}
