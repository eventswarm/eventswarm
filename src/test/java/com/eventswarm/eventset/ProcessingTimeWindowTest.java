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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eventswarm.eventset;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.eventswarm.events.jdo.TestEvents;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.util.IntervalUnit;

/**
 *
 * @author zoki
 */
public class ProcessingTimeWindowTest
        implements AddEventTrigger, RemoveEventTrigger, com.eventswarm.RemoveEventAction {

    public static Event event1,  event2,  event3;
    private static final long ALLOWEDDELAY = 10;
    private Object sync1 = new Object();
    private Object SyncDelay = new Object();
    private long add1,  remove1;
    private Event removed;

    public ProcessingTimeWindowTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

        event1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmpty);
        event2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmpty);
        event3 = new JdoEvent(TestEvents.headerA3, TestEvents.partsEmpty);
    }

    @After
    public void tearDown() {
    }

    /** Tests for constructors
     * 
     */
    @Test
    public void testForConstructorDefault() {
        System.out.println("Testing Default Constructor");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(5);
        System.out.println("Window size is " + Long.toString(instance.windowSize) + " milliseconds");
        assertTrue(instance.queueSize() == 0);
        assertTrue(instance.consistent());     
    }

    
   @Test
    public void testForConstructorsTimeUnitsExplicit() {
 
        System.out.println("Testing Explicit TimeUnits Constructor");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(IntervalUnit.MINUTES, 5);
        System.out.println("Window size is " + Long.toString(instance.windowSize) + " milliseconds");
        assertTrue(instance.queueSize() == 0);
        assertTrue(instance.consistent());     
    }
    
    
    /**
     * Test of AddEventTrigger method, of class ProcessingTimeWindow.
     */
    @Test
    public void testExecute_AddZeroEventTrigger_Event() {
        System.out.println("AddEventTrigger_event execute - No events added");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(5);
        assertTrue(instance.queueSize() == 0);
        assertTrue(instance.consistent());
    }

    @Test
    public void testExecute_AddOneEventTrigger_Event() {
        System.out.println("AddEventTrigger_event execute - one event");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(5);
        instance.execute((AddEventTrigger) this, event1);
        assertTrue(instance.queueSize() == 1);
        assertTrue(instance.consistent());
        assertTrue(instance.queueContains(event1));
    }

    @Test
    public void testExecute_AddTwoEventTrigger_Event() {
        System.out.println("AddEventTrigger_event execute - two events");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(5);
        instance.execute((AddEventTrigger) this, event1);
        instance.execute((AddEventTrigger) this, event2);
        assertTrue(event1.equals(instance.peek().getEvent()));
        assertTrue(instance.queueSize() == 2);
        assertTrue(instance.consistent());
        assertTrue(instance.queueContains(event1));
        assertTrue(instance.queueContains(event2));
    }

    @Test
    public void testExecute_AddTwoSameEventTrigger_Event() {
        System.out.println("AddEventTrigger_event execute - two SAME events");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(5);
        instance.execute((AddEventTrigger) this, event1);
        instance.execute((AddEventTrigger) this, event1);
        assertTrue(event1.equals(instance.peek().getEvent()));
        assertTrue(instance.consistent());
        assertTrue(instance.queueSize() == 1);
        assertTrue(instance.queueContains(event1));
    }

    /**
     * Test of execute method, of class ProcessingTimeWindow.
     */
    @Test
    public void testExecute_RemoveEventTrigger_Event() {
        System.out.println("RemoveEventTrigger_execute");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(5);
        instance.execute((AddEventTrigger) this, event1);
        // Removes should be ignored
        instance.execute((RemoveEventTrigger) this, event1);
        instance.execute((RemoveEventTrigger) this, event2);
        assertTrue(instance.queueSize() == 1);
        assertTrue(instance.consistent());
        assertTrue(instance.queueContains(event1));
    }

    @Test
    public void testExecute_RemoveEventTrigger_Event_AfterWindowExpiry() {
        System.out.println("RemoveEventTrigger_execute with Wait");
        ProcessingTimeWindow instance = new ProcessingTimeWindow(2);
        instance.registerAction(this);
        this.add1 = System.currentTimeMillis();
        instance.execute((AddEventTrigger) this, event1);
        try {
            synchronized (sync1) {
                this.sync1.wait(10000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessingTimeWindowTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(" Queue size is " + instance.queueSize());
        long interval = remove1 - add1;
        System.out.println("Time in window was " + Long.toString(interval) + " milliseconds");
        assertTrue(interval >= 2000 && interval <= (2000 + ALLOWEDDELAY));
        assertTrue(instance.consistent());
        assertTrue(instance.queueSize() == 0);
        assertTrue(event1 == this.removed);
    }

    @Test
    public void testExecute_AddTwoEventsWaitThenAddThirdEvent() {
        System.out.println("Add TwoEventsWaitThenAddThirdEvent ");
        // Create 5 sec ProcessingTimeWindow
        ProcessingTimeWindow instance = new ProcessingTimeWindow(5);
        // Add two events then wait for say 2 seconds
        instance.execute((AddEventTrigger) this, event1);
        instance.execute((AddEventTrigger) this, event2);
        assertTrue(instance.queueSize() == 2);
        try {
            synchronized (SyncDelay) {
                this.SyncDelay.wait(2000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessingTimeWindowTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Check that event1 and event2 are in the queue after 2 seconds
        assertTrue(instance.queueSize() == 2);
        // Add the third event to the queue  - which will be removed after new interval of 5 sec, 
        // which is effectivly after 7 seconds from the initial event1 being entered
        instance.execute((AddEventTrigger) this, event3);
        assertTrue(instance.queueSize() == 3);
        try {
            synchronized (SyncDelay) {
                this.SyncDelay.wait(3500);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessingTimeWindowTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Check that this DelayedQueue removes only the first two wvents 
        // (because we waited for 5.5 seconds which is gretar then window size (5 sec)
        assertTrue(instance.queueSize() == 1);
        assertFalse(instance.queueContains(event1));
        assertFalse(instance.queueContains(event2));
        assertTrue(instance.queueContains(event3));

        try {
            synchronized (SyncDelay) {
                this.SyncDelay.wait(2500);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ProcessingTimeWindowTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        // wait for another 2.5 seconds (giving us now 6 seconds from the time when event 3 was added, 
        // to ensure that the DelayedQueue removes this third event after its 5 sec interval expired
        assertTrue(instance.queueSize() == 0);
        assertFalse(instance.queueContains(event1));
        assertFalse(instance.queueContains(event2));
        assertFalse(instance.queueContains(event3));
    }

    public void registerAction(AddEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterAction(AddEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(RemoveEventTrigger trigger, Event event) {
        this.remove1 = System.currentTimeMillis();
        this.removed = event;
        synchronized (this.sync1) {
            this.sync1.notify();
        }
    }

    public void registerAction(RemoveEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterAction(RemoveEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}