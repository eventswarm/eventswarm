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

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.eventset.ClockedDiscreteTimeWindow;
import com.eventswarm.schedules.TickTrigger;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SizeBelowExpressionTest implements EventMatchAction {

    Source EVENT_SOURCE = new JdoSource("ClockedDiscreteTimeWindowTest");
    ArrayList<Event> events;
    ClockedDiscreteTimeWindow window;
    Map<String, EventPart> parts = null;

    @Before
    public void setup() {
        events = new ArrayList<Event>();
    }

    @Test
    public void testAddBelowThreshold() throws Exception {
        window = new ClockedDiscreteTimeWindow(1, 0, 0);
        SizeBelowExpression instance = new SizeBelowExpression(2, window);
        instance.registerAction(this);
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(), EVENT_SOURCE), parts));
        assertEquals(1, window.size());
        assertEquals(0, events.size());
        assertTrue(instance.isTrue());
    }

    @Test
    public void testAddAtThreshold() throws Exception {
        window = new ClockedDiscreteTimeWindow(1, 0, 0);
        SizeBelowExpression instance = new SizeBelowExpression(1, window);
        instance.registerAction(this);
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(), EVENT_SOURCE), parts));
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(), EVENT_SOURCE), parts));
        assertEquals(2, window.size());
        assertEquals(0, events.size());
        assertFalse(instance.isTrue());
    }


    @Test
    public void testRemoveBelowThreshold() throws Exception {
        window = new ClockedDiscreteTimeWindow(1, 0, 0);
        SizeBelowExpression instance = new SizeBelowExpression(2, window);
        instance.registerAction(this);
        Date now = new Date();
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(now, EVENT_SOURCE), parts));
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(now.getTime()+100), EVENT_SOURCE), parts));
        window.execute((TickTrigger) null, new Date(now.getTime()+1001));
        assertEquals(1, window.size());
        assertEquals(1, events.size());
        assertTrue(instance.isTrue());
    }

    @Test
    public void testSetThreshold() throws Exception {
        window = new ClockedDiscreteTimeWindow(1, 0, 0);
        SizeBelowExpression instance = new SizeBelowExpression(1, window);
        instance.registerAction(this);
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(), EVENT_SOURCE), parts));
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(), EVENT_SOURCE), parts));
        instance.setThreshold(2);
        assertEquals(2, window.size());
        assertEquals(0, events.size());
        assertFalse(instance.isTrue());
    }

    @Test
    public void testRemoveAfterSetThreshold() throws Exception {
        window = new ClockedDiscreteTimeWindow(1, 0, 0);
        SizeBelowExpression instance = new SizeBelowExpression(1, window);
        instance.registerAction(this);
        Date now = new Date();
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(now, EVENT_SOURCE), parts));
        window.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(now.getTime()+100), EVENT_SOURCE), parts));
        instance.setThreshold(2);
        window.execute((TickTrigger) null, new Date(now.getTime()+1001));
        assertEquals(1, window.size());
        assertEquals(1, events.size());
        assertTrue(instance.isTrue());
    }

    @Override
    public void execute(EventMatchTrigger trigger, Event event) {
        events.add(event);
    }
}
