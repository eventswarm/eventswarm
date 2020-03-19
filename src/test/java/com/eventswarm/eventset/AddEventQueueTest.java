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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import com.eventswarm.events.jdo.TestEvents;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class AddEventQueueTest {
    AddEventAction action;
    List<Event> adds;
    AddEventQueue instance;

    @Before
    public void setup() throws Exception {
        instance = new AddEventQueue();
        adds = new ArrayList<Event>();
        action = new AddEventAction() {
            public void execute(AddEventTrigger trigger, Event event) {
                adds.add(event);
            }
        };
        instance.registerAction(action);
    }

    @After
    public void teardown() throws Exception {
        instance.stop();
        instance.getExecutor().awaitTermination(1L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testConstruct() throws Exception {
        assertNotNull(instance);
        assertNotNull(instance.queue);
        assertNotNull(instance.getExecutor());
    }

    @Test
    public void testAdd() throws Exception {
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(10);
        assertEquals(0, instance.queue.size());
        assertEquals(1, adds.size());
        assertEquals(TestEvents.jdoEvent, adds.get(0));
    }

    @Test
    public void testAdd2() throws Exception {
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEventAfterDiffSrcAfterSeq);
        Thread.sleep(10);
        assertEquals(0, instance.queue.size());
        assertEquals(2, adds.size());
        assertEquals(TestEvents.jdoEvent, adds.get(0));
        assertEquals(TestEvents.jdoEventAfterDiffSrcAfterSeq, adds.get(1));
    }

    @Test
    public void testAddSame() throws Exception {
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(10);
        assertEquals(0, instance.queue.size());
        assertEquals(1, adds.size());
        assertEquals(TestEvents.jdoEvent, adds.get(0));
    }

    @Test
    public void testMultipleActions() throws Exception {
        AddEventAction action2 = new AddEventAction() {
            public void execute(AddEventTrigger trigger, Event event) {
                adds.add(event);
            }
        };
        instance.registerAction(action2);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(10);
        assertEquals(0, instance.queue.size());
        assertEquals(2, adds.size());
        assertEquals(TestEvents.jdoEvent, adds.get(0));
        assertEquals(TestEvents.jdoEvent, adds.get(1));
    }

    @Test
    public void testAddThreaded2() throws Exception {
        new Thread(new RunnableAdder(instance, 1)).start();
        new Thread(new RunnableAdder(instance, 1)).start();
        Thread.sleep(1000);
        assertEquals(0, instance.queue.size());
        assertEquals(2, adds.size());
    }

    @Test
    public void testAddThreadedMany() throws Exception {
        new Thread(new RunnableAdder(instance, 100)).start();
        new Thread(new RunnableAdder(instance, 100)).start();
        Thread.sleep(1000);
        assertEquals(0, instance.queue.size());
        assertEquals(200, adds.size());
    }

    @Test
    public void testStopOwn() throws Exception {
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(10);
        instance.stop();
        assertEquals(0, instance.queue.size());
        assertEquals(1, adds.size());
        assertTrue(instance.isStopped());
        assertTrue(instance.getExecutor().isShutdown());
        assertTrue(instance.getExecutor().awaitTermination(10, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testStopOther() throws Exception {
        instance.setExecutor(Executors.newSingleThreadExecutor());
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(10);
        instance.stop();
        assertEquals(0, instance.queue.size());
        assertEquals(1, adds.size());
        assertFalse(instance.getExecutor().isShutdown());
        assertFalse(instance.getExecutor().isTerminated());
    }


    private class RunnableAdder implements Runnable {
        private Event event;
        private AddEventQueue queue;
        int count;

        private RunnableAdder(AddEventQueue queue, int count) {
            this.queue = queue;
            this.count = count;
        }

        private Event makeEvent(int seqNr) {
            return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'seqnr':" + Integer.toString(seqNr) + "}"));
        }

        public void run() {
            for (int i=0; i < count; i++) {
                queue.add(makeEvent(i));
            }
        }
    }
}
