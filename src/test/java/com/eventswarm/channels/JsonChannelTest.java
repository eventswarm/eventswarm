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
import com.eventswarm.events.jdo.*;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JsonChannelTest implements FromJson {
    public static String SOURCE = "JsonChannelTest";
    public static String TYPE_ID = "JsonChannelTest";

    @Test
    public void testNextSingle() throws Exception {
        InputStream stream = new StringBufferInputStream("{a:1, b:2}");
        JsonChannel channel = new JsonChannel(stream);
        Event event = channel.next();
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(1, ((JsonEvent<?>) event).getInt("a"));
        assertEquals(2, ((JsonEvent<?>) event).getInt("b"));
        JSONObject part =  ((JdoPartWrapper<JSONObject>)event.getPart(JsonEvent.JSON_PART_NAME)).getWrapped();
        assertEquals(2, part.length());
    }

    @Test
    public void testNextMultiple() throws Exception {
        InputStream stream = new StringBufferInputStream("{a:1, b:2}{a:2, b:1}");
        JsonChannel channel = new JsonChannel(stream);
        channel.next();
        Event event = channel.next();
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(2, ((JsonEvent<?>) event).getInt("a"));
        assertEquals(1, ((JsonEvent<?>) event).getInt("b"));
        JSONObject part = ((JdoPartWrapper<JSONObject>)event.getPart(JsonEvent.JSON_PART_NAME)).getWrapped();
        assertEquals(2, part.length());
    }


    @Test
    public void testNextEmpty() throws Exception {
        InputStream stream = new StringBufferInputStream("");
        JsonChannel channel = new JsonChannel(stream);
        Event event = channel.next();
        assertNull(event);
        assertTrue(channel.isStopped());
    }

    @Test
    public void setTokener() throws Exception {
        InputStream stream = new StringBufferInputStream("{a:1, b:2}");
        JsonChannel channel = new JsonChannel(stream);
        channel.next();
        channel.setTokener(new StringBufferInputStream("{a:2, b:1}"));
        Event event = channel.next();
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(2, ((JsonEvent<?>) event).getInt("a"));
        assertEquals(1, ((JsonEvent<?>) event).getInt("b"));
        JSONObject part = ((JdoPartWrapper<JSONObject>)event.getPart(JsonEvent.JSON_PART_NAME)).getWrapped();
        assertEquals(2, part.length());
    }

    @Test
    public void testRegisterConstructor() throws Exception {
        InputStream stream = new StringBufferInputStream("{typeId: 'JsonChannelTest', a:1, b:2}");
        JsonChannel channel = new JsonChannel(stream);
        channel.registerConstructor(TYPE_ID, this);
        Event event = channel.next();
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals("JsonChannelTest", event.getHeader().getSource().getSourceId());
    }

    @Test
    public void testUnregisterConstructor() throws Exception {
        InputStream stream = new StringBufferInputStream("{typeId: 'JsonChannelTest', a:1, b:2}{typeId: 'JsonChannelTest', a:2, b:1}");
        JsonChannel channel = new JsonChannel(stream);
        channel.registerConstructor(TYPE_ID, this);
        Event event1 = channel.next();
        assertEquals("JsonChannelTest", event1.getHeader().getSource().getSourceId());
        channel.unregisterConstructor(TYPE_ID, this);
        Event event2 = channel.next();
        assertTrue(JsonEvent.class.isInstance(event2));
        assertEquals(2, ((JsonEvent<?>) event2).getInt("a"));
        assertEquals(1, ((JsonEvent<?>) event2).getInt("b"));
        assertFalse("JsonChannelTest".equals(event2.getHeader().getSource().getSourceId()));
    }

    public Event fromJson(JSONObject json) {
        return new OrgJsonEvent(new JdoHeader(new Date(), new JdoSource("JsonChannelTest")), json);
    }
}
