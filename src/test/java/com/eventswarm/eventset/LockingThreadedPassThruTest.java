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
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class LockingThreadedPassThruTest {
    private int counter;
    private ArrayList<Event> adds = new ArrayList<Event>();
    private ArrayList<Event> otherAdds = new ArrayList<Event>();
    private ArrayList<Event> removes = new ArrayList<Event>();
    private AddEventAction add1, add2;
    private RemoveEventAction remove1, remove2;
    private boolean interfered;

    private static int COUNT=1000000;

    @Before
    public void setup() throws Exception {
        counter = 0;
        interfered = false;
        add1 = new AddEventAction() {
            @Override
            public void execute(AddEventTrigger trigger, Event event) {
                adds.add(event);
                count(COUNT);
            }
        };
        add2 = new AddEventAction() {
            @Override
            public void execute(AddEventTrigger trigger, Event event) {
                adds.add(event);
                count(COUNT);
            }
        };
        remove1 = new RemoveEventAction() {
            @Override
            public void execute(RemoveEventTrigger trigger, Event event) {
                removes.add(event);
                count(1000000);
            }
        };
        remove2 = new RemoveEventAction() {
            @Override
            public void execute(RemoveEventTrigger trigger, Event event) {
                removes.add(event);
                count(1000000);
            }
        };
    }

    @Test
    public void testSimpleAdd() throws Exception {
        LockingThreadedPassThru instance = new LockingThreadedPassThru(1);
        instance.registerAction(add1);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(100);
        assertEquals(1, adds.size());
        assertFalse(interfered);
    }

    @Test
    public void testAddTwoSingleAction() throws Exception {
        LockingThreadedPassThru instance = new LockingThreadedPassThru(2);
        instance.registerAction(add1);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEventAfterDiffSrcAfterSeq);
        Thread.sleep(100);
        assertEquals(2, adds.size());
        assertEquals(TestEvents.jdoEventBeforeDiffSrcAfterSeq, adds.get(0));
        assertFalse(interfered);
    }

    @Test
    public void testInterferingAdds() throws Exception {
        LockingThreadedPassThru instance = new LockingThreadedPassThru(2);
        instance.registerAction(add1);
        instance.registerAction(add2);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(100);
        assertEquals(2, adds.size());
        assertTrue(interfered);
    }

    @Test
    public void testRemove() throws Exception {
        LockingThreadedPassThru instance = new LockingThreadedPassThru(1);
        instance.registerAction(remove1);
        instance.execute((RemoveEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(100);
        assertEquals(1, removes.size());
    }

    @Test
    public void testRemoveTwoSingleAction() throws Exception {
        LockingThreadedPassThru instance = new LockingThreadedPassThru(2);
        instance.registerAction(remove1);
        instance.execute((RemoveEventTrigger) null, TestEvents.jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((RemoveEventTrigger) null, TestEvents.jdoEventAfterDiffSrcAfterSeq);
        Thread.sleep(100);
        assertEquals(2, removes.size());
        assertEquals(TestEvents.jdoEventBeforeDiffSrcAfterSeq, removes.get(0));
        assertFalse(interfered);
    }

    @Test
    public void testInterferingRemoves() throws Exception {
        LockingThreadedPassThru instance = new LockingThreadedPassThru(2);
        instance.registerAction(remove1);
        instance.registerAction(remove2);
        instance.execute((RemoveEventTrigger) null, TestEvents.jdoEvent);
        Thread.sleep(100);
        assertEquals(2, removes.size());
        assertTrue(interfered);
    }

    @Test
    public void testStop() throws Exception {

    }

    @Test
    public void testRestart() throws Exception {

    }

    /**
     * Method to sit in a tight loop incrementing a number and verifying that threads are not interfering
     * with each other
     *
     * @param iterations
     */
    private void count(int iterations) {
        int start = this.counter;
        for (int i=0; i < iterations; i++) {
            this.counter++;
        }
        System.out.println("Counter started at " + Integer.toString(start) + " and finished at " + Integer.toString(this.counter));
        if (this.counter - start != iterations) {
            interfered = true;
        }
    }
}
