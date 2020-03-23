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

import com.eventswarm.channels.Deserializer;
import com.eventswarm.channels.Serializer;
import com.eventswarm.events.Event;
import com.eventswarm.events.Header;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class OrgJsonDeserializerTest {
    @Test
    public void testDeserializeBytes() throws Exception {
        // should perhaps remove dependency on serializer
        Serializer serializer = new OrgJsonSerializer();
        Deserializer instance = new OrgJsonDeserializer();
        OrgJsonEvent event = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        byte[] bytes = serializer.toBytes(event);
        System.out.write(bytes); System.out.println();
        Event result = (OrgJsonEvent) instance.fromBytes(bytes);
        assertNotNull(result);
        assertEquals(event, result);
        assertEquals(1, event.getInt("a"));
        assertEquals(event.getHeader().getTimestamp(), result.getHeader().getTimestamp());
        assertEquals(event.getHeader().getEventId(), result.getHeader().getEventId());
        assertEquals(event.getHeader().getSequenceNumber(), result.getHeader().getSequenceNumber());
        assertEquals(event.getHeader().getSource(), result.getHeader().getSource());
    }

    @Test
    public void testDeserializeString() throws Exception {
        // should perhaps remove dependency on serializer
        Serializer serializer = new OrgJsonSerializer();
        Deserializer instance = new OrgJsonDeserializer();
        OrgJsonEvent event = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        String string = serializer.toString(event);
        System.out.println(string);
        Event result = (OrgJsonEvent) instance.fromString(string);
        assertNotNull(result);
        assertEquals(event, result);
        assertEquals(1, event.getInt("a"));
        assertEquals(event.getHeader().getTimestamp(), result.getHeader().getTimestamp());
        assertEquals(event.getHeader().getEventId(), result.getHeader().getEventId());
        assertEquals(event.getHeader().getSequenceNumber(), result.getHeader().getSequenceNumber());
        assertEquals(event.getHeader().getSource(), result.getHeader().getSource());
    }

    @Test
    public void testDeserializeJsonObject() throws Exception {
        // should perhaps remove dependency on serializer
        Serializer serializer = new OrgJsonSerializer();
        OrgJsonDeserializer instance = new OrgJsonDeserializer();
        OrgJsonEvent event = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        JSONObject json = new JSONObject(serializer.toString(event));
        System.out.println(json.toString());
        Event result = (OrgJsonEvent) instance.fromJsonObject(json);
        assertNotNull(result);
        assertEquals(event, result);
        assertEquals(1, event.getInt("a"));
        assertEquals(event.getHeader().getTimestamp(), result.getHeader().getTimestamp());
        assertEquals(event.getHeader().getEventId(), result.getHeader().getEventId());
        assertEquals(event.getHeader().getSequenceNumber(), result.getHeader().getSequenceNumber());
        assertEquals(event.getHeader().getSource(), result.getHeader().getSource());
    }

    @Test
    public void testDeserializeBySource() throws Exception {
        String mySource = "blah";
        Map<String,Class<? extends Event>> map = new HashMap<String,Class<? extends Event>>();
        map.put(mySource, MyOrgJsonEvent.class);
        Serializer serializer = new OrgJsonSerializer();
        Deserializer instance = new OrgJsonDeserializer(map);
        Header header = new JdoHeader(new Date(), new JdoSource(mySource));
        OrgJsonEvent event = new OrgJsonEvent(header, new JSONObject("{'a':1}"));
        String string = serializer.toString(event);
        System.out.println(string);
        MyOrgJsonEvent result = (MyOrgJsonEvent) instance.fromString(string);
        assertTrue(MyOrgJsonEvent.class.isInstance(result));
        assertNotNull(result);
        assertEquals(event, result);
        assertEquals(1, event.getInt("a"));
        assertEquals(event.getHeader().getTimestamp(), result.getHeader().getTimestamp());
        assertEquals(event.getHeader().getEventId(), result.getHeader().getEventId());
        assertEquals(event.getHeader().getSequenceNumber(), result.getHeader().getSequenceNumber());
        assertEquals(event.getHeader().getSource(), result.getHeader().getSource());
    }

    @Test
    public void testDeserializeDefault() throws Exception {
        String mySource = "blah";
        Map<String,Class<? extends Event>> map = new HashMap<String,Class<? extends Event>>();
        map.put(mySource, MyOrgJsonEvent.class);
        Serializer serializer = new OrgJsonSerializer();
        Deserializer instance = new OrgJsonDeserializer(map);
        OrgJsonEvent event = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        String string = serializer.toString(event);
        System.out.println(string);
        OrgJsonEvent result = (OrgJsonEvent) instance.fromString(string);
        assertNotNull(result);
        assertTrue(OrgJsonEvent.class.isInstance(result));
        assertFalse(MyOrgJsonEvent.class.isInstance(result));
        assertEquals(event, result);
        assertEquals(1, event.getInt("a"));
        assertEquals(event.getHeader().getTimestamp(), result.getHeader().getTimestamp());
        assertEquals(event.getHeader().getEventId(), result.getHeader().getEventId());
        assertEquals(event.getHeader().getSequenceNumber(), result.getHeader().getSequenceNumber());
        assertEquals(event.getHeader().getSource(), result.getHeader().getSource());
    }

    @Test
    public void testDeserializeFactory() throws Exception {
        OrgJsonEventFactory factory = new OrgJsonEventFactory() {
            public Event create(Header header, JSONObject json) throws Deserializer.DeserializeException {
                return new MyOrgJsonEvent(header, json);
            }
        };
        Deserializer instance = new OrgJsonDeserializer(factory);
        Serializer serializer = new OrgJsonSerializer();
        OrgJsonEvent event = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        String string = serializer.toString(event);
        System.out.println(string);
        MyOrgJsonEvent result = (MyOrgJsonEvent) instance.fromString(string);
        assertTrue(MyOrgJsonEvent.class.isInstance(result));
        assertNotNull(result);
        assertEquals(event, result);
    }

    public static class MyOrgJsonEvent extends OrgJsonEvent {
        // just declare a constructor
        public MyOrgJsonEvent(Header header, JSONObject json) {
            super(header, json);
        }
    }
}
