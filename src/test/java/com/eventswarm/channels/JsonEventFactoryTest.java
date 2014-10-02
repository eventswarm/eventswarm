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
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoPartWrapper;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.events.jdo.OrgJsonPart;
import org.json.JSONObject;
import org.junit.Test;

import java.net.InetAddress;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JsonEventFactoryTest {
    private static String TEST_SOURCE = "JsonEventFactoryTest";

    @Test
    public void fromJson() throws Exception {
        JsonEventFactory instance = new JsonEventFactory();
        Event event = instance.fromJson(new JSONObject("{a: 1, b:2}"));
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(1, ((JsonEvent) event).getInt("a"));
        assertEquals(2, ((JsonEvent) event).getInt("b"));
        JSONObject part = ((JdoPartWrapper<JSONObject>)event.getPart(JsonEvent.JSON_PART_NAME)).getWrapped();
        assertEquals(2, part.length());
    }

    @Test
    public void fromJsonNoSource() throws Exception {
        JsonEventFactory instance = new JsonEventFactory();
        Event event = instance.fromJson(new JSONObject("{a: 1, b:2}"));
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(JdoSource.getLocalSource().getSourceId(), event.getHeader().getSource().getSourceId());
    }

    @Test
    public void fromJsonFactorySource() throws Exception {
        JsonEventFactory instance = new JsonEventFactory(TEST_SOURCE);
        Event event = instance.fromJson(new JSONObject("{a: 1, b:2}"));
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(TEST_SOURCE, event.getHeader().getSource().getSourceId());
    }

    @Test
    public void fromJsonWithSource() throws Exception {
        JsonEventFactory instance = new JsonEventFactory();
        Event event = instance.fromJson(new JSONObject("{source: 'SomeOtherSource', a: 1, b:2}"));
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals("SomeOtherSource", event.getHeader().getSource().getSourceId());
    }

    @Test
    public void sourceReuseDefault() throws Exception {
        JsonEventFactory instance = new JsonEventFactory();
        Event event1 = instance.fromJson(new JSONObject("{a: 1, b:2}"));
        Event event2 = instance.fromJson(new JSONObject("{a: 1, b:2}"));
        assertTrue(event1.getHeader().getSource() == event2.getHeader().getSource());
    }

    @Test
    public void sourceReuseOther() throws Exception {
        JsonEventFactory instance = new JsonEventFactory();
        Event event1 = instance.fromJson(new JSONObject("{source: 'SomeOtherSource', a: 1, b:2}"));
        Event event2 = instance.fromJson(new JSONObject("{source: 'SomeOtherSource', a: 2, b:1}"));
        assertTrue(event1.getHeader().getSource() == event2.getHeader().getSource());
    }
}
