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
package com.eventswarm.events.serialization;

import com.eventswarm.channels.Serializer;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * OrgJsonEvent serializer implementation that uses the field names defined in the JSONEvent interface to
 * add a header object and serialize to a JSON string or bytes.
 */
public class OrgJsonSerializer implements Serializer {

    private static Logger logger = Logger.getLogger(OrgJsonSerializer.class);

    public byte[] toBytes(Event event) throws SerializeException {
        try {
            if (OrgJsonEvent.class.isInstance(event)) {
                return toString(event).getBytes(OrgJsonEvent.CHARSET);
            } else {
                throw new SerializeException("Attempt to serialize non-JSON event as JSON");
            }
        } catch (UnsupportedEncodingException exc) {
            // this should never happen, log and return an empty array
            logger.fatal("What the? UTF-8 not supported?", exc);
            throw new SerializeException(exc);
        }
    }

    /**
     * Return a string representation of a serialized JSON event
     *
     * @param event
     * @return Serialized string representation of an event
     * @throws com.eventswarm.channels.Serializer.SerializeException
     *
     */
    public String toString(Event event) throws SerializeException {
        if (OrgJsonEvent.class.isInstance(event)) {
            return "{" + makeHeader(event) + "," + ((OrgJsonEvent) event).getJsonString().trim().substring(1);
        } else {
            throw new SerializeException("Attempt to serialize non-JSON event as JSON");
        }
    }

    /**
     * General purpose method to create a JSON field containing the header of an event.
     * <p/>
     * Returned in a form suitable for direct inclusion in a higher-level structure, e.g.
     * "EventSwarm":{"eventId":"joigwoihweoiwge", "timestamp":1231341313, "sequenceNumber": 0, "source":"twitter.com"}
     *
     * @param event
     * @return
     */
    public String makeHeader(Event event) {
        JSONObject fields = new JSONObject();
        fields.put(OrgJsonEvent.JSON_EVENT_ID, event.getHeader().getEventId());
        fields.put(OrgJsonEvent.JSON_TIMESTAMP, event.getHeader().getTimestamp().getTime());
        fields.put(OrgJsonEvent.JSON_SEQUENCE_NO, event.getHeader().getSequenceNumber());
        fields.put(OrgJsonEvent.JSON_SOURCE_ID, event.getHeader().getSource().getSourceId());
        return "\"" + OrgJsonEvent.JSON_HEADER_OBJECT + "\":" + fields.toString();
    }
}