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
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.eventset.OutOfOrderAction;
import com.eventswarm.eventset.OutOfOrderTrigger;
import com.eventswarm.util.IntervalUnit;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.eventswarm.events.jdo.TestEvents;
import java.util.Date;

/**
 *
 * @author andyb
 */
public class DiscreteTimeWindowTest implements AddEventTrigger, RemoveEventAction, OutOfOrderAction {

    // keep a FIFO queue of events that have been removed
    private Queue<Event> queue = new LinkedList<Event>();    // keep a FIFO queue of events that are out of order
    private Queue<Event> outOfOrderQueue = new LinkedList<Event>();    // define some timestamps for our tests
    static long WINDOWSIZE = 2;
    static long SIZEINMILLIS = WINDOWSIZE * IntervalUnit.MILLISPERSECOND;
    static long first = System.currentTimeMillis();
    static long second1 = first + 1L;
    static long second2 = first + 1L;
    static long keepFirst = first + SIZEINMILLIS;
    static long removeFirst = first + SIZEINMILLIS + 1L;
    static long removeSecond = second1 + SIZEINMILLIS + 1L;    // create events using timestamps
    static JdoSource source = new JdoSource("BLAH");
    static private Map<String,EventPart> empty = null;
    static Event firstEvent = new JdoEvent(new JdoHeader(new Date(first), 1, source), empty);
    static Event second1Event = new JdoEvent(new JdoHeader(new Date(second1), 1, source), empty);
    static Event second2Event = new JdoEvent(new JdoHeader(new Date(second2), 2, source), empty);
    static Event keepFirstEvent = new JdoEvent(new JdoHeader(new Date(keepFirst), 1, source), empty);
    static Event removeFirstEvent = new JdoEvent(new JdoHeader(new Date(removeFirst), 1, source), empty);
    static Event removeSecondEvents = new JdoEvent(new JdoHeader(new Date(removeSecond), 1, source), empty);

    public DiscreteTimeWindowTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        // make sure our queue is empty
        this.queue.clear();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of execute method, of class DiscreteTimeWindow.
     */
    @Test
    public void testExecute_empty() {
        System.out.println("execute with first event");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.execute(this, firstEvent);
        // event should be the only event in the EventSet
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.size() == 1);
    }

    @Test
    public void testExecute_addToOne() {
        System.out.println("execute with first and second event");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.execute(this, firstEvent);
        instance.execute(this, second1Event);
        // Should be two events, in the right order
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.contains(second1Event));
        assertTrue(instance.eventSet.first() == firstEvent);
        assertTrue(instance.size() == 2);
    }

    @Test
    public void testExecute_addSame() {
        System.out.println("execute with same event twice");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.execute(this, firstEvent);
        instance.execute(this, firstEvent);
        // Should be just the one event
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.size() == 1);
    }

    @Test
    public void testExecute_addOutOfOrder() {
        System.out.println("execute with out-of-order events");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.execute(this, second1Event);
        instance.execute(this, firstEvent);
        // Should both be there in correct order
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.contains(second1Event));
        assertTrue(instance.eventSet.first() == firstEvent);
        assertTrue(instance.size() == 2);
    }

    @Test
    public void testExecute_addOutOfOrderActionFiring() {
        System.out.println("execute with out-of-order triger firing OutOfOrder actions");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((OutOfOrderAction) this);
        instance.execute(this, second1Event);
        instance.execute(this, firstEvent);
        assertTrue(this.outOfOrderQueue.size() == 1);
        // Should both be there in correct order
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.contains(second1Event));
        assertTrue(instance.eventSet.first() == firstEvent);
        assertTrue(instance.size() == 2);
    }

    @Test
    public void testExecute_addToOneAtLimit() {
        System.out.println("Add event at limit of window");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.execute(this, firstEvent);
        instance.execute(this, keepFirstEvent);
        // Should still have both events
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.contains(keepFirstEvent));
        assertTrue(instance.size() == 2);
    }

    @Test
    public void testExecute_addToOneOverLimit() {
        System.out.println("Add event past limit of window");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((RemoveEventAction) this);
        instance.execute(this, firstEvent);
        instance.execute(this, removeFirstEvent);
        // Old event should have been removed
        assertFalse(instance.contains(firstEvent));
        assertTrue(instance.contains(removeFirstEvent));
        assertTrue(instance.size() == 1);
        assertTrue(this.queue.peek() == firstEvent);
        assertTrue(this.queue.size() == 1);
    }

    @Test
    public void testExecute_addToTwoOverLimitOne() {
        System.out.println("Add event past limit of window for first event but not second");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((RemoveEventAction) this);
        instance.execute(this, firstEvent);
        instance.execute(this, second1Event);
        instance.execute(this, removeFirstEvent);
        // Old event should have been removed
        assertFalse(instance.contains(firstEvent));
        assertTrue(instance.contains(second1Event));
        assertTrue(instance.contains(removeFirstEvent));
        assertTrue(instance.size() == 2);
        assertTrue(this.queue.peek() == firstEvent);
        assertTrue(this.queue.size() == 1);
    }

    @Test
    public void testExecute_addToTwoOverLimitTwo() {
        System.out.println("Add event past limit of window for first and second events");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((RemoveEventAction) this);
        instance.execute(this, firstEvent);
        instance.execute(this, second1Event);
        instance.execute(this, removeSecondEvents);
        // Old event should have been removed
        assertFalse(instance.contains(firstEvent));
        assertFalse(instance.contains(second1Event));
        assertTrue(instance.contains(removeSecondEvents));
        assertTrue(instance.size() == 1);
        assertTrue(this.queue.peek() == firstEvent);
        assertTrue(this.queue.contains(second1Event));
        assertTrue(this.queue.size() == 2);
    }

    @Test
    public void testExecute_addToTwoOverLimitTwice() {
        System.out.println("Add event past limit of window for first event, then separately for second event");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((RemoveEventAction) this);
        instance.execute(this, firstEvent);
        instance.execute(this, second1Event);
        instance.execute(this, removeFirstEvent);
        instance.execute(this, removeSecondEvents);
        // Oldest two events should have been removed
        assertFalse(instance.contains(firstEvent));
        assertFalse(instance.contains(second1Event));
        assertTrue(instance.contains(removeFirstEvent));
        assertTrue(instance.contains(removeSecondEvents));
        assertTrue(instance.size() == 2);
        assertTrue(this.queue.peek() == firstEvent);
        assertTrue(this.queue.contains(second1Event));
        assertTrue(this.queue.size() == 2);
    }

    @Test
    public void testExecute_addWithEqualTimestamps() {
        System.out.println("Add two events with equal timestamps");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((RemoveEventAction) this);
        instance.execute(this, second2Event);
        instance.execute(this, second1Event);
        // Both events should be there
        assertTrue(instance.contains(second1Event));
        assertTrue(instance.contains(second2Event));
        assertTrue(instance.size() == 2);
        assertTrue(instance.eventSet.first() == second1Event);
    }

    @Test
    public void testExecute_addAndRemoveWithEqualTimestamps() {
        System.out.println("Add two events with equal timestamps then remove them");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((RemoveEventAction) this);
        instance.execute(this, second2Event);
        instance.execute(this, second1Event);
        instance.execute(this, removeSecondEvents);
        // Both events should be there
        assertFalse(instance.contains(second1Event));
        assertFalse(instance.contains(second2Event));
        assertTrue(instance.contains(removeSecondEvents));
        assertTrue(instance.size() == 1);
        assertTrue(this.queue.size() == 2);
        assertTrue(this.queue.peek() == second1Event);
    }


    @Test
    public void testExecute_addBeforeWindowStart() {
        System.out.println("Add event that is before the window start");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.registerAction((RemoveEventAction) this);
        instance.registerAction((OutOfOrderAction) this);
        instance.execute(this, removeSecondEvents);
        instance.execute(this, second1Event);
        // The older event should not be there, and should be in the out of order queue
        assertFalse(instance.contains(second1Event));
        assertTrue(instance.contains(removeSecondEvents));
        assertTrue(instance.size() == 1);
        assertTrue(this.outOfOrderQueue.size() == 1);
        assertTrue(this.outOfOrderQueue.peek() == second1Event);
    }
    
    
    
    @Test
    public void testIsFilling() {
        System.out.println("Check Initialisation stage of the Window");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        // Initialisation lasts until the first event is removed so this adds
        // few events to load up the window
        instance.execute(this, second1Event);
        instance.execute(this, firstEvent);
        assertTrue(instance.isFilling());
        // Should still have both events
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.size() == 2);
        instance.execute(this, keepFirstEvent); 
        assertTrue(instance.isFilling());
        instance.execute(this, removeFirstEvent);
        // when the first event is removed the window is in sliding (i.e. active)
        assertFalse(instance.isFilling());
        // contunue sliding from now on
        instance.execute(this, firstEvent);
        assertFalse(instance.isFilling());
    }
    
    @Test
    public void test_getEnd() {
        System.out.println("test getEnd method");
        DiscreteTimeWindow instance = new DiscreteTimeWindow(WINDOWSIZE);
        instance.execute(this, firstEvent);
        assertTrue(keepFirstEvent.getHeader().getTimestamp().getTime() == (instance.getEnd()));
        assertTrue(instance.contains(firstEvent));
        assertTrue(instance.size() == 1);
    }

    @Test
    public void testAddDupeIdSameTimestamp() {
        Date ts = new Date();
        Event event1 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "http://myfeed.com#article?h=JYLo0gBdUjxlVvNGXjWYsnEwRXU="), TestEvents.partsSingleMap);
        Event event2 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "2"), TestEvents.partsSingleMap);
        Event event3 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "http://myfeed.com#article?h=JYLo0gBdUjxlVvNGXjWYsnEwRXU="), TestEvents.partsSingleMap);
        DiscreteTimeWindow events = new DiscreteTimeWindow(WINDOWSIZE);
        events.execute((AddEventTrigger) null, event1);
        events.execute((AddEventTrigger) null, event2);
        events.execute((AddEventTrigger) null, event3);
        assertEquals(2, events.size());
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));
        assertTrue(events.contains(event3));
        assertNotSame(event3, events.first());
    }

    /**
     * Note that this test will generate log warnings because we should never have this situation (dupe id, different timestamp)
     */
    @Test
    public void testAddDupeIdDiffTimestamp() {
        Date ts = new Date();
        Event event1 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "1"), TestEvents.partsSingleMap);
        Event event2 = new JdoEvent(new JdoHeader(new Date(ts.getTime()+1), new JdoSource("EventSetTest"), "1"), TestEvents.partsSingleMap);
        DiscreteTimeWindow events = new DiscreteTimeWindow(WINDOWSIZE);
        events.execute((AddEventTrigger) null, event1);
        events.execute((AddEventTrigger) null, event2);
        assertEquals(2, events.size());
        assertTrue("EventSet should contain event1", events.contains(event1));
        assertTrue("EventSet should also contain event2", events.contains(event2));
    }

    public void registerAction(AddEventAction action) {
        // implementation not required for testing
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterAction(AddEventAction action) {
        // implementation not required for testing
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void execute(RemoveEventTrigger trigger, Event event) {
        // we need to record the event that has been removed in a manner that
        // can be checked
        this.queue.add(event);
    }

    public void execute(OutOfOrderTrigger trigger, Event event) {
        System.out.println("Out of order events detected");
        this.outOfOrderQueue.add(event);
    }

    public void registerAction(OutOfOrderAction action) {
        // implementation not required for testing
        throw new UnsupportedOperationException("Not supported yet.");
    }
}