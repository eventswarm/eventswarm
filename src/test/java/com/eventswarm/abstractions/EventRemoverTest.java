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
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import com.eventswarm.eventset.EventSet;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventRemoverTest {
    ValueRetriever<String> keyRetriever = new OrgJsonEvent.StringRetriever("id");
    EventSet events = new EventSet();
    Map<String,Event> eventMap = new HashMap<String,Event>();
    Map<String,Object> jsonMap = new HashMap<String,Object>();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testRemoveEmptySet() throws Exception {
        EventRemover<String> remover = new EventRemover<String>(eventMap, keyRetriever);
        remover.registerAction(events);
        remover.execute((AddEventTrigger) null, createEvent("id", "id1"));
        assertEquals(0, events.size());
    }

    @Test
    public void testRemoveHit() throws Exception {
        EventRemover<String> remover = new EventRemover<String>(eventMap, keyRetriever);
        remover.registerAction(events);
        Event eventAdd = createEvent("id", "id1");
        eventMap.put("id1", eventAdd);
        events.execute((AddEventTrigger) null, eventAdd);
        assertEquals(1, events.size());
        Event eventRemove = createEvent("id", "id1");
        remover.execute((AddEventTrigger) null, eventRemove);
        assertEquals(0, events.size());
    }

    @Test
    public void testRemoveMiss() throws Exception {
        EventRemover<String> remover = new EventRemover<String>(eventMap, keyRetriever);
        remover.registerAction(events);
        Event eventAdd = createEvent("id", "id1");
        eventMap.put("id1", eventAdd);
        events.execute((AddEventTrigger) null, eventAdd);
        assertEquals(1, events.size());
        Event eventRemove = createEvent("id", "id2");
        remover.execute((AddEventTrigger) null, eventRemove);
        assertEquals(1, events.size());
    }

    @Test
    public void testRemoveNoKey() throws Exception {
        EventRemover<String> remover = new EventRemover<String>(eventMap, keyRetriever);
        remover.registerAction(events);
        Event eventAdd = createEvent("id", "id1");
        eventMap.put("id1", eventAdd);
        events.execute((AddEventTrigger) null, eventAdd);
        assertEquals(1, events.size());
        Event eventRemove = createEvent("noid", "id1");
        remover.execute((AddEventTrigger) null, eventRemove);
        assertEquals(1, events.size());
    }

    @Test
    public void testRemoveMultiple() throws Exception {
        EventRemover<String> remover = new EventRemover<String>(eventMap, keyRetriever);
        remover.registerAction(events);
        Event eventAdd = createEvent("id", "id1");
        eventMap.put("id1", eventAdd);
        events.execute((AddEventTrigger) null, eventAdd);
        EventSet events2 = new EventSet();
        remover.registerAction(events2);
        events2.execute((AddEventTrigger) null, eventAdd);
        assertEquals(1, events.size());
        assertEquals(1, events2.size());
        Event eventRemove = createEvent("id", "id1");
        remover.execute((AddEventTrigger) null, eventRemove);
        assertEquals(0, events.size());
        assertEquals(0, events2.size());
    }

    public Event createEvent(String key, String value) {
        jsonMap.clear();
        jsonMap.put(key,value);
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject(jsonMap));
    }
}
