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
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.schedules.TickTrigger;
import org.junit.Test;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ClockedDiscreteTimeWindowTest implements RemoveEventAction {
    Source EVENT_SOURCE = new JdoSource("ClockedDiscreteTimeWindowTest");
    ArrayList<Event> events;
    Map<String, EventPart> parts = null;

    @Before
    public void setup() {
        events = new ArrayList<Event>();
    }

    @Test
    public void addEvent() throws Exception {
        ClockedDiscreteTimeWindow instance = new ClockedDiscreteTimeWindow(2, 0, 1000);
        instance.registerAction(this);
        instance.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(), EVENT_SOURCE), parts));
        assertEquals(1, instance.size());
        assertEquals(0, events.size());
    }

    @Test
    public void forceRemoveWithExternalTick() throws Exception {
        ClockedDiscreteTimeWindow instance = new ClockedDiscreteTimeWindow(2, 0, 0);
        instance.registerAction(this);
        Date now = new Date();
        instance.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(now, EVENT_SOURCE), parts));
        instance.execute((TickTrigger) null, new Date(now.getTime() + 2001));
        assertEquals(0, instance.size());
        assertEquals(1, events.size());
    }


    @Test
    public void waitForRemovalInternalTick() throws Exception {
        ClockedDiscreteTimeWindow instance = new ClockedDiscreteTimeWindow(1, 0, 100);
        instance.registerAction(this);
        instance.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(new Date(), EVENT_SOURCE), parts));
        try {
            Thread.sleep(1101);
        } catch(InterruptedException ex) {
            System.out.println("I'm so drowsy, who woke me?");
        }
        assertEquals(0, instance.size());
        assertEquals(1, events.size());
    }

    @Test
    public void respectsLatencyAllowance() throws Exception {
        ClockedDiscreteTimeWindow instance = new ClockedDiscreteTimeWindow(2, 100, 0);
        instance.registerAction(this);
        Date now = new Date();
        instance.execute((AddEventTrigger) null, new JdoEvent(new JdoHeader(now, EVENT_SOURCE), parts));
        instance.execute((TickTrigger) null, new Date(now.getTime() + 2001));
        assertEquals(1, instance.size());
        assertEquals(0, events.size());
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        events.add(event);
    }
}
