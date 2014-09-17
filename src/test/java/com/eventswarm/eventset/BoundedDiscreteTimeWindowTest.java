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
package com.eventswarm.eventset;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Header;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class BoundedDiscreteTimeWindowTest {
    private static Source SOURCE = new JdoSource("BoundedDiscreteTimeWindowTest");

    @Test
    public void addToEmptyBeforeLimit() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 1);
        Event event = makeEvent(0);
        instance.execute((AddEventTrigger) null, event);
        assertEquals(1, instance.size());
        assertEquals(event, instance.first());
    }

    @Test
    public void addAtLimit() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 1);
        Event event1 = makeEvent(1);
        Event event2 = makeEvent(0);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(1, instance.size());
        assertEquals(event2, instance.first());
    }

    @Test
    public void addDuplicateAtLimit() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 2);
        Event event1 = makeEvent(1);
        Event event2 = makeEvent(0);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(2, instance.size());
        assertEquals(event1, instance.first());
        assertEquals(event2, instance.last());
    }


    @Test
    public void addAtLimitLess1() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 2);
        Event event1 = makeEvent(1);
        Event event2 = makeEvent(0);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(2, instance.size());
        assertEquals(event1, instance.first());
    }

    @Test
    public void addOutsideWindow() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 2);
        Event event1 = makeEvent(1001);
        Event event2 = makeEvent(0);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(1, instance.size());
        assertEquals(event2, instance.first());
    }

    @Test
    public void addAtLimitAndOutsideWindow() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 1);
        Event event1 = makeEvent(1001);
        Event event2 = makeEvent(0);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(1, instance.size());
        assertEquals(event2, instance.first());
    }

    @Test
    public void addAtLimitAndBeforeFirst() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 1);
        Event event1 = makeEvent(0);
        Event event2 = makeEvent(1001);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(1, instance.size());
        assertEquals(event1, instance.first());
    }

    @Test
    public void repeatedAddAtLimitAndBeforeFirst() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 1);
        Event event1 = makeEvent(0);
        Event event2 = makeEvent(3001);
        Event event3 = makeEvent(1001);
        Event event4 = makeEvent(2001);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        instance.execute((AddEventTrigger) null, event4);
        assertEquals(1, instance.size());
        assertEquals(event1, instance.first());
    }

    @Test
    public void addOverLimit() throws Exception {
        BoundedDiscreteTimeWindow instance = new BoundedDiscreteTimeWindow(1, 1);
        Event event1 = makeEvent(2);
        Event event2 = makeEvent(1);
        Event event3 = makeEvent(0);
        instance.addEvent(event1);
        instance.addEvent(event2);
        instance.execute((AddEventTrigger) null, event3);
        assertEquals(1, instance.size());
        assertEquals(event3, instance.first());
    }

    private Event makeEvent(long before) {
        Map<String,EventPart> map = null;
        long ts = (new Date()).getTime() - before;
        Header header = new JdoHeader(new Date(ts), SOURCE);
        return new JdoEvent(header, map);
    }
}
