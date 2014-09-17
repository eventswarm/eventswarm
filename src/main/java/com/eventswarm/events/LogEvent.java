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
import com.eventswarm.powerset.EventKey;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface LogEvent extends Event, Text {

    public enum Level {
        // log severity level, in order of increasing severity
        trace, debug, info, warn, error, fatal;

        public boolean isAbove(Level level) {return this.ordinal() > level.ordinal();}
        public boolean isBelow(Level level) {return this.ordinal() < level.ordinal();}
    }

    /**
     * Return the logging level of this log event
     *
     * Implementations using underlying log events whose levels do not match the enumeration above need to
     * normalise and/or map into these levels.
     *
     * @return
     */
    public Level getLevel();

    /**
     * Get a short rendered message associated with the log event
     *
     * If the event is the result of an exception, only the message from the exception should be returned (not the
     * stack trace). The getText method should return the full text, including stack trace.
     *
     * @see com.eventswarm.events.Text
     */
    public String getShortMessage();

    /**
     * Return the stack trace associated with a log event, if present
     *
     * This method returns an array to assist in rendering. If source events do not contain an array, the stack trace
     * should be split at linefeeds.
     *
     * @see com.eventswarm.events.Text
     *
     * @return an array of stack trace strings or null no stack trace is present
     */
    public String[] getStackTrace();

    /**
     * Return a classifier for this log event, that is, a string indicating the source or location of the event
     *
     * For example, if using Log4J, this is the logger name
     *
     * @return
     */
    public String getClassifier();

    /**
     * Return a string distinguishing the runtime context of this event, for example, a thread id
     *
     * This string is used to distinguish separate threads of execution or sessions. In Log4J, this is the NDC (nested
     * diagnostic context).
     */
    public String getContext();

    /**
     * Static retriever class for classifiers
     */
    public static class GetClassifier implements ValueRetriever<String> {
        public static final GetClassifier INSTANCE = new GetClassifier();
        public static final EventKey<String> EVENT_KEY = new EventKey.EventKeyRetriever<String>(INSTANCE);

        @Override
        public String getValue(Event event) {
            if (LogEvent.class.isInstance(event)) {
                return ((LogEvent) event).getClassifier();
            } else {
                return null;
            }
        }
    }

    /**
     * Static retriever class for levels
     */
    public static class GetLevel implements ValueRetriever<Level> {
        public static final GetLevel INSTANCE = new GetLevel();
        public static final EventKey<Level> EVENT_KEY = new EventKey.EventKeyRetriever<Level>(INSTANCE);

        @Override
        public Level getValue(Event event) {
            if (LogEvent.class.isInstance(event)) {
                return ((LogEvent) event).getLevel();
            } else {
                return null;
            }
        }
    }
}
