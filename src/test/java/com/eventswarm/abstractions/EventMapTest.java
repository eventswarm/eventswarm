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
package com.eventswarm.abstractions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventMapTest {
    ValueRetriever<String> keyRetriever = new OrgJsonEvent.StringRetriever("id");
    Map<String,Object> jsonMap = new HashMap<String,Object>();

    public void testConstruct() throws Exception {
        EventMap<String> instance = new EventMap<String>(keyRetriever);
        assertNotNull(instance);
        assertNotNull(instance.getMap());
    }

    @Test
    public void testAddSingle() throws Exception {
        EventMap<String> instance = new EventMap<String>(keyRetriever);
        Event event = createEvent("id", "id1");
        instance.execute((AddEventTrigger) null, event);
        assertEquals(1, instance.getMap().size());
        assertEquals(event, instance.getMap().get("id1"));
    }

    @Test
    public void testAddSecond() throws Exception {
        EventMap<String> instance = new EventMap<String>(keyRetriever);
        Event event1 = createEvent("id", "id1");
        Event event2 = createEvent("id", "id2");
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(2, instance.getMap().size());
        assertEquals(event1, instance.getMap().get("id1"));
        assertEquals(event2, instance.getMap().get("id2"));
    }

    @Test
    public void testReplace() throws Exception {
        EventMap<String> instance = new EventMap<String>(keyRetriever);
        Event event1 = createEvent("id", "id1");
        Event event2 = createEvent("id", "id1");
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(1, instance.getMap().size());
        assertEquals(event2, instance.getMap().get("id1"));
    }

    @Test
    public void testRemoveSingle() throws Exception {
        EventMap<String> instance = new EventMap<String>(keyRetriever);
        Event event = createEvent("id", "id1");
        instance.execute((AddEventTrigger) null, event);
        instance.execute((RemoveEventTrigger) null, event);
        assertEquals(0, instance.getMap().size());
    }

    @Test
    public void testRemoveOneFromTwo() throws Exception {
        EventMap<String> instance = new EventMap<String>(keyRetriever);
        Event event1 = createEvent("id", "id1");
        Event event2 = createEvent("id", "id2");
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(2, instance.getMap().size());
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(1, instance.getMap().size());
        assertNull(instance.getMap().get("id1"));
    }

    @Test
    public void testRemoveNotFound() throws Exception {
        EventMap<String> instance = new EventMap<String>(keyRetriever);
        Event event1 = createEvent("id", "id1");
        Event event2 = createEvent("id", "id1");
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(1, instance.getMap().size());
        assertEquals(event2, instance.getMap().get("id1"));
    }

    public Event createEvent(String key, String value) {
        jsonMap.clear();
        jsonMap.put(key,value);
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject(jsonMap));
    }

}
