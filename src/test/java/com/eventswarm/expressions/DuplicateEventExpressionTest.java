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
package com.eventswarm.expressions;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import com.eventswarm.eventset.EventSet;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class DuplicateEventExpressionTest {
    Comparator singleComparator;
    ValueRetriever<String> retriever = new JsonEvent.StringRetriever("test1");
    DuplicateEventExpression instance;
    EventSet events;

    @Before
    public void setup() throws Exception {
        events = new EventSet();
        singleComparator = new ValueEqualComparator<String>(retriever);
    }

    @Test
    public void testConstruct() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        assertNotNull(instance);
        assertEquals(singleComparator, instance.getComparator());
        assertEquals(events, instance.getEvents());
    }

    @Test
    public void testFirstAdd() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        instance.execute((AddEventTrigger) null, makeEvent("a","b"));
        assertEquals(0, instance.matches.size());
    }

    @Test
    public void testSingleMatch() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        Event event = makeEvent("a","b");
        events.execute((AddEventTrigger) null, makeEvent("a","c"));
        instance.execute((AddEventTrigger) null, event);
        assertEquals(1, instance.matches.size());
        assertEquals(2, ((Activity) instance.matches.first()).getEvents().size());
        assertTrue(((Activity) instance.matches.first()).getEvents().contains(event));
    }

    @Test
    public void testDoubleMatch() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        Event event = makeEvent("a","b");
        events.execute((AddEventTrigger) null, makeEvent("a","c"));
        events.execute((AddEventTrigger) null, makeEvent("a","d"));
        instance.execute((AddEventTrigger) null, event);
        assertEquals(1, instance.matches.size());
        assertEquals(3, ((Activity) instance.matches.first()).getEvents().size());
        assertTrue(((Activity) instance.matches.first()).getEvents().contains(event));
    }

    @Test
    public void testNotMatched() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        Event event = makeEvent("a","b");
        events.execute((AddEventTrigger) null, makeEvent("b","c"));
        instance.execute((AddEventTrigger) null, event);
        assertEquals(0, instance.matches.size());
    }

    @Test
    public void testOneMatchInMultiple() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        Event event = makeEvent("a","b");
        events.execute((AddEventTrigger) null, makeEvent("b","c"));
        events.execute((AddEventTrigger) null, makeEvent("a","c"));
        instance.execute((AddEventTrigger) null, event);
        assertEquals(1, instance.matches.size());
        assertEquals(2, ((Activity) instance.matches.first()).getEvents().size());
        assertTrue(((Activity) instance.matches.first()).getEvents().contains(event));
    }

    @Test
    public void testIgnoreSelf() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        Event event = makeEvent("a","b");
        events.execute((AddEventTrigger) null, event);
        instance.execute((AddEventTrigger) null, event);
        assertEquals(0, instance.matches.size());
    }

    @Test
    public void testFirstDownstream() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        events.registerAction((AddEventAction) instance);
        Event event = makeEvent("a","b");
        events.execute((AddEventTrigger) null, event);
        assertEquals(0, instance.matches.size());
    }

    @Test
    public void testMatchDownstream() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        events.registerAction((AddEventAction) instance);
        Event event1 = makeEvent("a","b");
        Event event2 = makeEvent("a","b");
        events.execute((AddEventTrigger) null, event1);
        events.execute((AddEventTrigger) null, event2);
        assertEquals(1, instance.matches.size());
        assertEquals(2, ((Activity) instance.matches.first()).getEvents().size());
        assertTrue(((Activity) instance.matches.first()).getEvents().contains(event1));
        assertTrue(((Activity) instance.matches.first()).getEvents().contains(event2));
    }

    @Test
    public void testNonMatchDownstream() throws Exception {
        instance = new DuplicateEventExpression(singleComparator, events);
        events.registerAction((AddEventAction) instance);
        Event event1 = makeEvent("a","b");
        Event event2 = makeEvent("b","c");
        events.execute((AddEventTrigger) null, event1);
        events.execute((AddEventTrigger) null, event2);
        assertEquals(0, instance.matches.size());
    }

    Event makeEvent(String text1, String text2) {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(),
                new JSONObject("{'test1':'" + text1 + "', 'test2':'" + text2 + "'}"));
    }
}
