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

package com.eventswarm.schedules;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.eventswarm.events.*;
import com.eventswarm.events.jdo.*;

/**
 *
 * @author andyb
 */
public class EventClockTest {

    static String SOURCE = "THIS";
    static Event event1 = new JdoEvent(new JdoHeader(new Date(1), 1, new JdoSource(SOURCE)), (Map<String,EventPart>) null);
    static Event event2 = new JdoEvent(new JdoHeader(new Date(2), 1, new JdoSource(SOURCE)), (Map<String,EventPart>) null);
    static Event event3 = new JdoEvent(new JdoHeader(new Date(3), 1, new JdoSource(SOURCE)), (Map<String,EventPart>) null);

    public EventClockTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getSource method, of class EventClock.
     */
    @Test
    public void testConstructor() {
        System.out.println("constructor test");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        assertEquals(source.action, instance);
    }


    /**
     * Test of getSource method, of class EventClock.
     */
    @Test
    public void testGetSource() {
        System.out.println("getSource");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        Object result = instance.getSource();
        assertEquals(source, result);
    }

    /**
     * Test of getTime method, of class EventClock.  Initial time should be epoch.
     */
    @Test
    public void testGetTime() {
        System.out.println("getTime, initial state");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        Date expResult = new Date(0);
        Date result = instance.getTime();
        assertEquals(expResult, result);
    }

    /**
     * Test of execute method, null event, no actions, should be ignored
     */
    @Test
    public void testExecuteNullEvent() {
        System.out.println("execute, null event");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        instance.execute(source, null);
        assertEquals(new Date(0), instance.getTime());
    }

    /**
     * Test of execute method, time greater, no actions, time should be updated
     */
    @Test
    public void testExecuteTimeGreater() {
        System.out.println("execute, time greater, no actions registered");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        instance.execute(source, event1);
        assertEquals(event1.getHeader().getTimestamp(), instance.getTime());
    }


    /**
     * Test of execute method, time equal, no actions, time should be unchanged
     */
    @Test
    public void testExecuteTimeEqual() {
        System.out.println("execute, time equal, no actions registered");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        instance.execute(source, event1);
        instance.execute(source, event1);
        assertEquals(event1.getHeader().getTimestamp(), instance.getTime());
    }


    /**
     * Test of execute method, time less, no actions, time should be unchanged
     */
    @Test
    public void testExecuteTimeLess() {
        System.out.println("execute, time less, no actions registered");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        instance.execute(source, event2);
        instance.execute(source, event1);
        assertEquals(event2.getHeader().getTimestamp(), instance.getTime());
    }

    /**
     * Test of execute method, time greater, action registered, action should be
     * called
     */
    @Test
    public void testExecuteTimeGreaterWithActions() {
        System.out.println("execute, time greater, action registered");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        TestAction action = new TestAction();
        instance.registerAction(action);
        instance.execute(source, event1);
        Date expected = event1.getHeader().getTimestamp();
        assertEquals(expected, instance.getTime());
        assertEquals(1, action.actions.size());
        assertTrue(action.actions.contains(expected));
    }


    /**
     * Test of execute method, time equal, action registered, action should not
     * be called
     */
    @Test
    public void testExecuteTimeEqualWithActions() {
        System.out.println("execute, time equal, action registered");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        TestAction action = new TestAction();
        instance.registerAction(action);
        instance.execute(source, event1);
        instance.execute(source, event1);
        assertEquals(1, action.actions.size());
        assertTrue(action.actions.contains(event1.getHeader().getTimestamp()));
    }


    /**
     * Test of execute method, time less, action registered, action should not
     * be called
     */
    @Test
    public void testExecuteTimeLessWithActions() {
        System.out.println("execute, time less, action registered");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        TestAction action = new TestAction();
        instance.registerAction(action);
        instance.execute(source, event2);
        instance.execute(source, event1);
        assertEquals(1, action.actions.size());
        assertFalse(action.actions.contains(event1.getHeader().getTimestamp()));
    }


    /**
     * Test of execute method, time greater, multiple actions, all actions
     * should be called
     */
    @Test
    public void testExecuteMultipleActions() {
        System.out.println("execute, multiple actions");
        EventSource source = new EventSource();
        EventClock instance = new EventClock(source);
        TestAction action1 = new TestAction();
        TestAction action2 = new TestAction();
        instance.registerAction(action1);
        instance.registerAction(action2);
        instance.execute(source, event1);
        assertEquals(1, action1.actions.size());
        assertTrue(action1.actions.contains(event1.getHeader().getTimestamp()));
        assertEquals(1, action2.actions.size());
        assertTrue(action2.actions.contains(event1.getHeader().getTimestamp()));
    }



    /**
     * Test of execute method, time greater, wrong source, event should be ignored
     */
    @Test
    public void testExecuteWrongSource() {
        System.out.println("execute, time greater, wrong source");
        EventSource source = new EventSource();
        EventSource other = new EventSource();
        EventClock instance = new EventClock(source);
        TestAction action = new TestAction();
        instance.registerAction(action);
        instance.execute(other, event1);
        assertTrue(action.actions.isEmpty());
        assertEquals(new Date(0), instance.getTime());
    }



    /**
     * Simple trigger class 
     */
    private class EventSource implements AddEventTrigger {

        protected AddEventAction action = null;

        public void registerAction(AddEventAction action) {
            this.action = action;
        }

        public void unregisterAction(AddEventAction action) {
            this.action = null;
        }
    }


    /**
     * Simple action class that records all execute statements
     */
    private class TestAction implements TickAction {

        protected ArrayList<Date> actions = new ArrayList<Date>();
        
        public void execute(TickTrigger trigger, Date time) {
            this.actions.add(time);
        }

    }
}