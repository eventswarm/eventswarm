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
package com.eventswarm.channels;

import com.eventswarm.events.Event;
import org.apache.log4j.Logger;
import sun.nio.cs.StandardCharsets;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Interface for implementations that convert an event to a byte array and/or String for serialization purposes.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface Serializer {
    /**
     * Convert an event to a byte array suitable for storage or transmission
     *
     * Content should be delineated, that is, it should be possible for a parser to distinguish event boundaries
     * if multiple events are written to a stream.
     *
     * @param event
     * @return
     * @throws SerializeException
     */
    public byte[] toBytes(Event event) throws SerializeException;

    /**
     * Convert an event to a String suitable for storage or transmission
     *
     * Content should be delineated, that is, it should be possible for a parser to distinguish event boundaries
     * if multiple events are written to a stream.
     *
     * @param event
     * @return
     * @throws SerializeException
     */
    public String toString(Event event) throws SerializeException;

    public static String DEFAULT_CHARSET = "UTF-8";

    public static class SerializeException extends Exception {
        public SerializeException() {
            super();
        }

        public SerializeException(String s) {
            super(s);
        }

        public SerializeException(String s, Throwable throwable) {
            super(s, throwable);
        }

        public SerializeException(Throwable throwable) {
            super(throwable);
        }
    }

    /**
     * Simple serializer implementation that just emits the string representation of an event
     * extracted by the toString method as either a byte array or string.
     *
     * This implementation will only work if an event can be safely reconstructed from the string representation.
     * There are some key issues to watch:
     *
     * - the event constructor needs to be able to determine the event id from this content: if not, then
     *   the reconstructed event will get a different ID and won't be recognised as the same event.
     *
     * - if the header sequence number is not included, the ordering of two events with the same timestamp will be
     *   non-deterministic (sequence numbers are used to define an arbitrary total order over events with the same
     *   timestamp).
     */
    public static class StringToBytesSerializer implements Serializer {
        private static Logger logger = Logger.getLogger(StringToBytesSerializer.class);
        @Override
        public byte[] toBytes(Event event) {
            try {
                return event.toString().getBytes(DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException exc) {
                // this should never happen, log and return an empty array
                logger.fatal("What the? UTF-8 not supported?", exc);
                return new byte[0];
            }
        }

        @Override
        public String toString(Event event) throws SerializeException {
            return event.toString();
        }
    }
}
