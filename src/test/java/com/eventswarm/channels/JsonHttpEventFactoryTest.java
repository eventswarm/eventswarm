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
import com.eventswarm.events.HttpEventPart;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHttpEventPart;
import com.eventswarm.events.jdo.JdoPartWrapper;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JsonHttpEventFactoryTest {
    private static String TEST_SOURCE = "JsonEventFactoryTest";
    private InetSocketAddress address;
    Map<String,List<String>> headers = new HashMap<String,List<String>>();

    @Before
    public void setup() throws Exception {
        String[] date = {"Fri, 16 Aug 2013 00:30:00 GMT"};
        headers.put(JdoHttpEventPart.DATE_HEADER, Arrays.asList(date));
        address = new InetSocketAddress("localhost", 3333);
    }

    @Test
    public void fromJsonHttp() throws Exception {
        JsonHttpEventFactory instance = new JsonHttpEventFactory();
        Event event = instance.fromJsonHttp(new JSONObject("{a: 1, b:2}"), makeHttp());
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(1, ((JsonEvent<?>) event).getInt("a"));
        assertEquals(2, ((JsonEvent<?>) event).getInt("b"));
        JSONObject part = ((JdoPartWrapper<JSONObject>)event.getPart(JsonEvent.JSON_PART_NAME)).getWrapped();
        assertEquals(2, part.length());
    }

    @Test
    public void fromJsonHttpNoSource() throws Exception {
        JsonHttpEventFactory instance = new JsonHttpEventFactory();
        Event event = instance.fromJsonHttp(new JSONObject("{a: 1, b:2}"), makeHttp());
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals(address.getHostName(), event.getHeader().getSource().getSourceId());
    }

    @Test
    public void fromJsonWithFromNoSource() throws Exception {
        String[] from = {"lala@lala.org"};
        headers.put("From", Arrays.asList(from));
        JsonHttpEventFactory instance = new JsonHttpEventFactory();
        Event event = instance.fromJsonHttp(new JSONObject("{a: 1, b:2}"), makeHttp());
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals("lala@lala.org", event.getHeader().getSource().getSourceId());
    }

    @Test
    public void fromJsonWithSource() throws Exception {
        JsonHttpEventFactory instance = new JsonHttpEventFactory();
        Event event = instance.fromJsonHttp(new JSONObject("{source: 'SomeOtherSource', a: 1, b:2}"), makeHttp());
        assertTrue(JsonEvent.class.isInstance(event));
        assertEquals("SomeOtherSource", event.getHeader().getSource().getSourceId());
    }

    @Test
    public void sourceReuseDefault() throws Exception {
        JsonHttpEventFactory instance = new JsonHttpEventFactory();
        HttpEventPart http = makeHttp();
        Event event1 = instance.fromJsonHttp(new JSONObject("{a: 1, b:2}"), http);
        Event event2 = instance.fromJsonHttp(new JSONObject("{a: 1, b:2}"), http);
        assertTrue(event1.getHeader().getSource() == event2.getHeader().getSource());
    }

    @Test
    public void sourceReuseOther() throws Exception {
        JsonHttpEventFactory instance = new JsonHttpEventFactory();
        HttpEventPart http = makeHttp();
        Event event1 = instance.fromJsonHttp(new JSONObject("{source: 'SomeOtherSource', a: 1, b:2}"), http);
        Event event2 = instance.fromJsonHttp(new JSONObject("{source: 'SomeOtherSource', a: 2, b:1}"), http);
        assertTrue(event1.getHeader().getSource() == event2.getHeader().getSource());
    }

    private HttpEventPart makeHttp() throws Exception {
        return new JdoHttpEventPart(headers, address, "POST", new URI("http://localhost/blah"));
    }
}
