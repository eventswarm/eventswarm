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

package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import org.junit.*;

import static org.junit.Assert.*;
import java.util.ArrayList;

/**
 *
 * @author andyb
 */
public class EventLogicalANDTest {

    Event event1 = TestEvents.jdoEvent;
    Event event2 = TestEvents.eventAfterDiffSrcAfterSeq;

    public EventLogicalANDTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test LogicalAND initial state, true AND True, no events processed.
     */
    @Test
    public void testEmpty() {
        System.out.println("True AND True, no events processed");
        EventLogicalAND instance = new EventLogicalAND(new TrueEventExpression(), new TrueEventExpression());
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertTrue(instance.getMatches().isEmpty());
    }

    /**
     * Test LogicalAND, True and True, one event processed
     */
    @Test
    public void testTrueAndTrue_OneEvent() {
        System.out.println("True AND True, one event processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        EventExpression expr2 = new TrueEventExpression();
        EventLogicalAND instance = new EventLogicalAND(expr1, expr2);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 1);
        assertTrue(action.matches.contains(event1));
        //assertTrue(expr1.getMatches().isEmpty());
        //assertTrue(expr2.getMatches().isEmpty());
    }


    /**
     * Test LogicalAND, True and True, two events processed
     */
    @Test
    public void testTrueAndTrue_TwoEvents() {
        System.out.println("True AND True, two events processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        EventExpression expr2 = new TrueEventExpression();
        EventLogicalAND instance = new EventLogicalAND(expr1, expr2);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        instance.execute(trigger, event2);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.hasMatched(event2));
        assertEquals(instance.getMatches().size(), 2);
        assertTrue(action.matches.contains(event1));
        assertTrue(action.matches.contains(event2));
        //assertTrue(expr1.getMatches().isEmpty());
        //assertTrue(expr2.getMatches().isEmpty());
   }
    

    /**
     * Test LogicalAND, True and True, null event
     */
    @Ignore @Test
    public void testTrueAndTrue_NullEvent() {
        System.out.println("True AND True, two events processed");
        fail("Need to update for expected behaviour when events are null");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        EventExpression expr2 = new TrueEventExpression();
        EventLogicalAND instance = new EventLogicalAND(expr1, expr2);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, null);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(null));
        assertEquals(instance.getMatches().size(), 1);
        assertTrue(action.matches.contains(null));
        //assertTrue(expr1.getMatches().isEmpty());
        //assertTrue(expr2.getMatches().isEmpty());
    }


    /**
     * Test LogicalAND, 3xTrue, one event processed
     */
    @Test
    public void test3xTrue_OneEvent() {
        System.out.println("True AND True, one event processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        EventExpression expr2 = new TrueEventExpression();
        EventExpression expr3 = new TrueEventExpression();
        ArrayList<EventExpression> parts = new ArrayList<EventExpression>();
        parts.add(expr1);
        parts.add(expr2);
        parts.add(expr3);
        EventLogicalAND instance = new EventLogicalAND(parts);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 1);
        assertTrue(action.matches.contains(event1));
        //assertTrue(expr1.getMatches().isEmpty());
        //assertTrue(expr2.getMatches().isEmpty());
        //assertTrue(expr3.getMatches().isEmpty());
    }


    /**
     * Test LogicalAND, True False True one event processed
     */
    @Test
    public void testTrueFalseTrue_OneEvent() {
        System.out.println("True AND True, one event processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        EventExpression expr2 = new FalseEventExpression();
        EventExpression expr3 = new TrueEventExpression();
        ArrayList<EventExpression> parts = new ArrayList<EventExpression>();
        parts.add(expr1);
        parts.add(expr2);
        parts.add(expr3);
        EventLogicalAND instance = new EventLogicalAND(parts);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 0);
        assertFalse(action.matches.contains(event1));
        //assertTrue(expr1.getMatches().isEmpty());
        //assertTrue(expr2.getMatches().isEmpty());
        //assertTrue(expr3.getMatches().isEmpty());
    }

    /**
     * Test LogicalAND, True and False, one event processed
     */
    @Test
    public void testTrueAndFalse_OneEvent() {
        System.out.println("True AND False, one event processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        EventExpression expr2 = new FalseEventExpression();
        EventLogicalAND instance = new EventLogicalAND(expr1, expr2);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 0);
        assertTrue(action.matches.isEmpty());
        //assertTrue(expr1.getMatches().isEmpty());
        //assertTrue(expr2.getMatches().isEmpty());
    }


    /**
     * Test LogicalAND, True and False, null event processed
     */
    @Ignore @Test
    public void testTrueAndFalse_NullEvent() {
        System.out.println("True AND False, null event processed");
        fail("Need to update for expected behaviour when events are null");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        EventExpression expr2 = new FalseEventExpression();
        EventLogicalAND instance = new EventLogicalAND(expr1, expr2);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, null);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(null));
        assertEquals(instance.getMatches().size(), 0);
        assertTrue(action.matches.isEmpty());
        //assertTrue(expr1.getMatches().isEmpty());
        //assertTrue(expr2.getMatches().isEmpty());
    }


    /**
     * Test LogicalAND, False and False, one event processed
     */
    @Test
    public void testFalseAndFalse_OneEvent() {
        System.out.println("True AND False, one event processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new FalseEventExpression();
        EventExpression expr2 = new FalseEventExpression();
        EventLogicalAND instance = new EventLogicalAND(expr1, expr2);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 0);
        assertTrue(action.matches.isEmpty());
        assertTrue(expr1.getMatches().isEmpty());
        assertTrue(expr2.getMatches().isEmpty());
    }

    /**
     * Test LogicalAND, single False expression, one event processed
     */
    @Test
    public void testFalseOnly_OneEvent() {
        System.out.println("False only, one event processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new FalseEventExpression();
        ArrayList<EventExpression> parts = new ArrayList<EventExpression>();
        parts.add(expr1);
        EventLogicalAND instance = new EventLogicalAND(parts);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 0);
        assertTrue(action.matches.isEmpty());
        assertTrue(expr1.getMatches().isEmpty());
    }


    /**
     * Test LogicalAND, single True, one event processed
     */
    @Test
    public void testTrueOnly_OneEvent() {
        System.out.println("True only, one event processed");
        AddEventTrigger trigger = null;
        EventExpression expr1 = new TrueEventExpression();
        ArrayList<EventExpression> parts = new ArrayList<EventExpression>();
        parts.add(expr1);
        EventLogicalAND instance = new EventLogicalAND(parts);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 1);
        assertTrue(action.matches.contains(event1));
        //assertTrue(expr1.getMatches().isEmpty());
    }


    /**
     * Test LogicalAND, no expression, one event processed
     */
    @Test
    public void testNoExpression_OneEvent() {
        System.out.println("No expression, one event processed");
        AddEventTrigger trigger = null;
        ArrayList<EventExpression> parts = new ArrayList<EventExpression>();
        EventLogicalAND instance = new EventLogicalAND(parts);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 1);
        assertTrue(action.matches.contains(event1));
    }


    /**
     * Test LogicalAND, null expression, one event processed
     */
    @Test
    public void testNullExpression_OneEvent() {
        System.out.println("Null expression, one event processed");
        AddEventTrigger trigger = null;
        ArrayList<EventExpression> parts = new ArrayList<EventExpression>();
        parts.add(null);
        EventLogicalAND instance = new EventLogicalAND(parts);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 1);
        assertTrue(action.matches.contains(event1));
    }

}