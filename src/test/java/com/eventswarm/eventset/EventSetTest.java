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
 * EventSetTest.java
 * JUnit based test
 *
 * Created on May 11, 2007, 2:41 PM
 */

package com.eventswarm.eventset;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.abstractions.DuplicateAbstractionException;
import com.eventswarm.abstractions.Abstraction;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import junit.framework.*;
import com.eventswarm.events.*;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.TestEvents;
import java.util.*;
import org.junit.Before;

/**
 *
 * @author andyb
 */
public class EventSetTest extends TestCase {
    
    EventSet events;
    Event event1, event2, event3;
    StubAbstraction abstraction;
    StubIncrementalAbstraction incrAbstraction;
    
    public EventSetTest(String testName) {
        super(testName);
    }

    @Before
    protected void setUp() throws Exception {
        this.events = new EventSet();
        event1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmpty);
        event2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmpty);
        event3 = new JdoEvent(TestEvents.headerA3, TestEvents.partsEmpty);
        StubAbstraction.classShareAble = true;
        StubIncrementalAbstraction.classShareAble = true;
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of add method, of class com.eventswarm.eventset.EventSet.
     */
    public void testFirstAdd() {
        System.out.println("add: add event to empty set");
        
        events.add(event1);
        
        assertTrue(events.size() == 1);
        assertTrue(events.contains(event1));
    }

    /**
     * Test of add method, of class com.eventswarm.eventset.EventSet.
     */
    public void testSecondAdd() {
        System.out.println("add: add second event to set containing one");
        
        events.add(event1);
        events.add(event2);
        
        assertTrue(events.size() == 2);
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));
    }



    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetNullAbstraction() throws Exception {
        System.out.println("getAbstraction: static abstraction, empty set");
        
        Throwable exc = null;
        try {
            Abstraction abs = events.getAbstraction(null);
        } catch (NullPointerException np) {
            exc = np;
        }
        
        assertSame(NullPointerException.class, exc.getClass());
    }

    
    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetStaticAbstractionEmpty() throws Exception {
        System.out.println("getAbstraction: static abstraction, empty set");
        
        Abstraction abs = events.getAbstraction(StubAbstraction.class);
        
        assertTrue(((StubAbstraction) abs).events.size() == 0);
        assertTrue(abs.isCurrent());
    }


    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetStaticAbstractionSingle() throws Exception {
        System.out.println("getAbstraction: static abstraction, single event in set");
        
        Event event = new JdoEvent(TestEvents.header, TestEvents.partsEmpty);
        events.add(event);
        Abstraction abs = events.getAbstraction(StubAbstraction.class);
        
        // Abstraction should contain a single event 
        assertTrue(((StubAbstraction) abs).events.indexOf(event) == 0);
        assertTrue(abs.isCurrent());
    }

    
    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetStaticAbstractionMultiple() throws Exception {
        System.out.println("getAbstraction: static abstraction, multiple events in set");
        
        Event event1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmpty);
        Event event2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmpty);
        events.add(event2);
        events.add(event1);
        Abstraction abs = events.getAbstraction(StubAbstraction.class);
        
        // Abstraction should contain events in an order consistent with chronological order
        assertTrue(((StubAbstraction) abs).events.indexOf(event1) == 0);
        assertTrue(((StubAbstraction) abs).events.indexOf(event2) == 1);
        assertTrue(abs.isCurrent());
    }

    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetStaticAbstractionAdd() throws Exception {
        System.out.println("getAbstraction: static abstraction, add event after registration");
        
        Abstraction abs = events.getAbstraction(StubAbstraction.class);
        Event event = new JdoEvent(TestEvents.header, TestEvents.partsEmpty);
        events.add(event);
        
        // Static abstractions should not be updated by adding an event and should be marked not current
        assertTrue(((StubAbstraction) abs).events.size() == 0);
        assertFalse(abs.isCurrent());
    }


    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetStaticAbstractionGetSecond() throws Exception {
        System.out.println("getAbstraction: static abstraction, add event after get, get again");
        
        Abstraction abs1 = events.getAbstraction(StubAbstraction.class);
        Event event = new JdoEvent(TestEvents.header, TestEvents.partsEmpty);
        events.add(event);
        Abstraction abs2 = events.getAbstraction(StubAbstraction.class);
        
        // Same abstraction should be returned for both calls
        assertSame(abs1, abs2);
        // Abstraction should be current 
        assertTrue(abs2.isCurrent());
        // Single event should be in abstraction
        assertTrue(((StubAbstraction) abs2).events.indexOf(event) == 0);
    }



    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetIncrAbstractionEmpty() throws Exception {
        System.out.println("getAbstraction: incremental abstraction, empty set");
        
        Abstraction abs = events.getAbstraction(StubIncrementalAbstraction.class);
        
        assertTrue(((StubIncrementalAbstraction) abs).events.size() == 0);
        assertTrue(abs.isCurrent());
    }


    
    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetIncrAbstractionSingle() throws Exception {
        System.out.println("getAbstraction: incremental abstraction, single event in set");
        
        Event event = new JdoEvent(TestEvents.header, TestEvents.partsEmpty);
        events.add(event);
        Abstraction abs = events.getAbstraction(StubIncrementalAbstraction.class);
        
        // Abstraction should contain a single event 
        assertTrue(((StubIncrementalAbstraction) abs).events.indexOf(event) == 0);
        assertTrue(abs.isCurrent());
    }

    
    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetIncrAbstractionMultiple() throws Exception {
        System.out.println("getAbstraction: incremental abstraction, multiple events in set");
        
        Event event1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmpty);
        Event event2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmpty);
        events.add(event2);
        events.add(event1);
        Abstraction abs = events.getAbstraction(StubIncrementalAbstraction.class);
        
        // Abstraction should contain events in an order consistent with chronological order
        assertTrue(((StubIncrementalAbstraction) abs).events.indexOf(event1) == 0);
        assertTrue(((StubIncrementalAbstraction) abs).events.indexOf(event2) == 1);
        assertTrue(abs.isCurrent());
    }


    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetIncrAbstractionGetSecond() throws Exception {
        System.out.println("getAbstraction: incremental abstraction, add event after get, get again");
        
        Abstraction abs1 = events.getAbstraction(StubIncrementalAbstraction.class);
        Event event = new JdoEvent(TestEvents.header, TestEvents.partsEmpty);
        events.add(event);
        Abstraction abs2 = events.getAbstraction(StubIncrementalAbstraction.class);
        
        // Same abstraction should be returned for both calls
        assertSame(abs1, abs2);
        // Abstraction should be current 
        assertTrue(abs2.isCurrent());
        // Single event should be in abstraction
        assertTrue(((StubIncrementalAbstraction) abs2).events.indexOf(event) == 0);
    }


    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetIncrAbstractionAdd() throws Exception {
        System.out.println("getAbstraction: incremental abstraction, add event after get");
        
        Abstraction abs = events.getAbstraction(StubIncrementalAbstraction.class);
        Event event = new JdoEvent(TestEvents.header, TestEvents.partsEmpty);
        events.add(event);
        
        // Abstraction should be updated and contain the new event
        assertTrue(((StubIncrementalAbstraction) abs).events.indexOf(event) == 0);
        assertTrue(abs.isCurrent());
    }

    /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetIncrAbstractionAddSecond() throws Exception {
        System.out.println("getAbstraction: incremental abstraction, add second event after get");
        
        Abstraction abs = events.getAbstraction(StubIncrementalAbstraction.class);
        Event event1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmpty);
        Event event2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmpty);
        events.add(event2);
        events.add(event1);
        
        // Both events should be in the abstraction, in add order
        assertTrue(((StubIncrementalAbstraction) abs).events.indexOf(event2) == 0);
        assertTrue(((StubIncrementalAbstraction) abs).events.indexOf(event1) == 1);        
        assertTrue(abs.isCurrent());
    }

        /**
     * Test of getAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testGetNonShareableAbstraction() throws Exception {
        System.out.println("getAbstraction: non shareable abstraction");
        
        // Override the default shareable attribute of our stub class
        StubAbstraction.classShareAble = false;
        
        // Register two abstractions
        Abstraction abs1 = events.getAbstraction(StubAbstraction.class);
        Abstraction abs2 = events.getAbstraction(StubAbstraction.class);
        
        // Returned abstractions should be different
        assertNotSame(abs1, abs2);
    }



    /**
     * Test of registerAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterNullAbstraction() throws Exception {
        System.out.println("registerAbstraction: null abstraction");
        
        Throwable exc = null;
        try {
            Abstraction result = events.registerAbstraction(null);
        } catch (NullPointerException np) {
            exc = np;
        }
        
        assertSame(NullPointerException.class, exc.getClass());
    }


    /**
     * Test of registerAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterSingletonAbstraction() throws Exception {
        System.out.println("registerAbstraction: singleton abstraction, register two instances");
        
        StubAbstraction abs1 = new StubAbstraction();
        StubAbstraction abs2 = new StubAbstraction();
        Abstraction result1 = events.registerAbstraction(abs1);
        Abstraction result2 = events.registerAbstraction(abs2);
        
        // First registered abstraction should be returned in both cases
        assertSame(abs1, result1);
        assertSame(result1, result2);
    }


    /**
     * Test of registerAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterNonSingletonAbstraction() throws Exception {
        System.out.println("registerAbstraction: non-singleton abstraction, register two instances");
        
        StubAbstraction abs1 = new StubAbstraction("A");
        StubAbstraction abs2 = new StubAbstraction("B");
        Abstraction result1 = events.registerAbstraction(abs1);
        Abstraction result2 = events.registerAbstraction(abs2);
        
        // Distinct abstractions should be maintained for each registration
        assertSame(abs1, result1);
        assertSame(abs2, result2);
        assertNotSame(result1, result2);
    }

    
    /**
     * Test of registerAbstraction method, of class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterNonShareableSingletonAbstraction() throws Exception {
        System.out.println("registerAbstraction: equal abstraction, not shareable, register two instances");
        
        // Create 2 non-shareable singleton abstractions
        StubIncrementalAbstraction abs1 = new StubIncrementalAbstraction();
        abs1.setShareable(false);
        StubIncrementalAbstraction abs2 = new StubIncrementalAbstraction();
        abs2.setShareable(false);
        
        // Try adding them
        Throwable exc = null;
        try {
            Abstraction result1 = events.registerAbstraction(abs1);
            Abstraction result2 = events.registerAbstraction(abs2);
        } catch (DuplicateAbstractionException dup) {
            exc = dup;
        }

        assertNotNull(exc);
    }

    /**
     * Test of registerAction method for AddEventAction, in class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterAddAction() throws Exception {
        System.out.println("registerAction: AddEventAction, single action registration");

        SimpleAddEventAction action = new SimpleAddEventAction("A");
        events.registerAction(action);
        events.addEvent(event1);

        assertTrue(action.received.contains(event1));
    }

    /**
     * Test of registerAction method for AddEventAction, in class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterAddActionMultiple() throws Exception {
        System.out.println("registerAction: AddEventAction, multiple action registration");

        SimpleAddEventAction action1 = new SimpleAddEventAction("A");
        SimpleAddEventAction action2 = new SimpleAddEventAction("B");
        events.registerAction(action1);
        events.registerAction(action2);
        events.addEvent(event1);

        assertTrue(action1.received.contains(event1));
        assertTrue(action2.received.contains(event1));
    }


    /**
     * Test of registerAction method for AddEventAction, in class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterRemoveAction() throws Exception {
        System.out.println("registerAction: AddEventAction, single action registration");

        SimpleRemoveEventAction action = new SimpleRemoveEventAction("A");
        events.registerAction(action);
        events.addEvent(event1);
        events.remove(event1);

        assertTrue(action.removed.contains(event1));
    }

    /**
     * Test of registerAction method for AddEventAction, in class com.eventswarm.eventset.EventSet.
     */
    public void testRegisterRemoveActionMultiple() throws Exception {
        System.out.println("registerAction: AddEventAction, multiple action registration");

        SimpleRemoveEventAction action1 = new SimpleRemoveEventAction("A");
        SimpleRemoveEventAction action2 = new SimpleRemoveEventAction("B");
        events.registerAction(action1);
        events.registerAction(action2);
        events.addEvent(event1);
        events.remove(event1);

        assertTrue(action1.removed.contains(event1));
        assertTrue(action2.removed.contains(event1));
    }

    /**
     * Test of iterator method, of class com.eventswarm.eventset.EventSet.
     */
    public void testIterator() {
        System.out.println("iterator");
        
        // Add two events, out of order
        Event event1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmpty);
        Event event2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmpty);
        Event event3 = new JdoEvent(TestEvents.headerA3, TestEvents.partsEmpty);
        events.add(event2);
        events.add(event1);
                
        // iterate over them, adding a new event to test "snapshot" property
        Iterator<Event> iter = events.iterator();
        events.add(event3);
        List<Event> list = new ArrayList<Event>(2);
        while (iter.hasNext()) {
            list.add(iter.next());
        }
  
        // Ensure that the iterator works in order and that we have a true snapshot
        assertTrue(list.indexOf(event1) == 0);
        assertTrue(list.indexOf(event2) == 1);
        assertTrue(list.size() == 2);
    }

    public void testAddDupeIdSameTimestamp() {
        Date ts = new Date();
        Event event1 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "http://myfeed.com#article?h=JYLo0gBdUjxlVvNGXjWYsnEwRXU="), TestEvents.partsSingleMap);
        Event event2 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "1"), TestEvents.partsSingleMap);
        Event event3 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "2"), TestEvents.partsSingleMap);
        Event event4 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "http://myfeed.com#article?h=JYLo0gBdUjxlVvNGXjWYsnEwRXU="), TestEvents.partsSingleMap);
        events.execute((AddEventTrigger) null, event1);
        events.execute((AddEventTrigger) null, event2);
        events.execute((AddEventTrigger) null, event3);
        events.execute((AddEventTrigger) null, event4);
        assertEquals(3, events.size());
        assertTrue(events.contains(event1));
        assertTrue(events.contains(event2));
        assertTrue(events.contains(event3));
        assertTrue(events.contains(event4));
        assertNotSame(event4, events.first());
    }

    /**
     * Note that this test will generate log warnings because we should never have this situation (dupe id, different timestamp)
     */
    public void testAddDupeIdDiffTimestamp() {
        Date ts = new Date();
        Event event1 = new JdoEvent(new JdoHeader(ts, new JdoSource("EventSetTest"), "1"), TestEvents.partsSingleMap);
        Event event2 = new JdoEvent(new JdoHeader(new Date(ts.getTime()+1), new JdoSource("EventSetTest"), "1"), TestEvents.partsSingleMap);
        events.execute((AddEventTrigger) null, event1);
        events.execute((AddEventTrigger) null, event2);
        assertEquals(2, events.size());
        assertTrue("EventSet should contain event1", events.contains(event1));
        assertTrue("EventSet should contain event2", events.contains(event2));
    }

    class SimpleAddEventAction implements AddEventAction {
        private String name;
        public Set<Event> received = new HashSet<Event>();

        public SimpleAddEventAction (String name) {
            this.name = name;
        }

        public void execute(AddEventTrigger trigger, Event event) {
            received.add(event);
        }
    }

    class SimpleRemoveEventAction implements RemoveEventAction {
        private String name;
        public Set<Event> removed = new HashSet<Event>();

        public SimpleRemoveEventAction (String name) {
            this.name = name;
        }

        public void execute(RemoveEventTrigger trigger, Event event) {
            removed.add(event);
        }
    }
}
