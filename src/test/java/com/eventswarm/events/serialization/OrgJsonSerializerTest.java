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
import com.eventswarm.events.Header;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class OrgJsonSerializerTest {
    @Test
    public void testDefaultSerializerBytes() throws Exception {
        Serializer instance = new OrgJsonSerializer();
        Header jdoheader = JdoHeader.getLocalHeader();
        OrgJsonEvent event = new OrgJsonEvent(jdoheader, new JSONObject("{'a':1}"));
        byte[] result = instance.toBytes(event);
        System.out.write(result);
        JSONObject parsed = new JSONObject(new JSONTokener(new ByteArrayInputStream(result)));
        assertEquals(1, parsed.getInt("a"));
        JSONObject header = parsed.getJSONObject(JsonEvent.JSON_HEADER_OBJECT);
        assertNotNull(header);
        assertTrue(header.has(JsonEvent.JSON_EVENT_ID));
        assertTrue(header.has(JsonEvent.JSON_SOURCE_ID));
        assertTrue(header.has(JsonEvent.JSON_TIMESTAMP));
        assertEquals(jdoheader.getSequenceNumber(), header.getInt(JsonEvent.JSON_SEQUENCE_NO));
    }

    @Test
    public void testDefaultSerializerString() throws Exception {
        Serializer instance = new OrgJsonSerializer();
        Header jdoheader = JdoHeader.getLocalHeader();
        OrgJsonEvent event = new OrgJsonEvent(jdoheader, new JSONObject("{'a':1}"));
        String result = instance.toString(event);
        System.out.println(result);
        JSONObject parsed = new JSONObject(result);
        assertEquals(1, parsed.getInt("a"));
        JSONObject header = parsed.getJSONObject(JsonEvent.JSON_HEADER_OBJECT);
        assertNotNull(header);
        assertTrue(header.has(JsonEvent.JSON_EVENT_ID));
        assertTrue(header.has(JsonEvent.JSON_SOURCE_ID));
        assertTrue(header.has(JsonEvent.JSON_TIMESTAMP));
        assertEquals(jdoheader.getSequenceNumber(), header.getInt(JsonEvent.JSON_SEQUENCE_NO));
    }

}
