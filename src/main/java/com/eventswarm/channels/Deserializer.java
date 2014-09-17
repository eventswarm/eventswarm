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

import java.io.UnsupportedEncodingException;

/**
 * Interface for implementations that convert a byte array to an event for deserialization purposes
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface Deserializer {
    public Event fromBytes(byte[] bytes) throws DeserializeException;

    public Event fromString(String string) throws DeserializeException;

    public static String DEFAULT_CHARSET = "UTF-8";

    public static class DeserializeException extends Exception {
        public DeserializeException() {
            super();
        }

        public DeserializeException(String s) {
            super(s);
        }

        public DeserializeException(String s, Throwable throwable) {
            super(s, throwable);
        }

        public DeserializeException(Throwable throwable) {
            super(throwable);
        }
    }
}
