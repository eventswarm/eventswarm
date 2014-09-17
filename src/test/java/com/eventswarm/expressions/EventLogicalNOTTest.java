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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author andyb
 */
public class EventLogicalNOTTest {

    Event event1 = TestEvents.jdoEvent;
    Event event2 = TestEvents.eventAfterDiffSrcAfterSeq;
    
    public EventLogicalNOTTest() {
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
     * Test logical not with false expression, single event
     */
    @Test
    public void testNegateFalse_NoEvent() {
        System.out.println("Logical NOT of false expression, no events");
        EventExpression expr = new FalseEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        assertFalse(instance.isTrue());
        assertTrue(instance.getMatches().isEmpty());
        assertFalse(instance.hasMatched(event1));
    }


    /**
     * Test logical not with false expression, single event
     */
    @Test
    public void testNegateFalse_OneEvent() {
        System.out.println("Logical NOT of false expression, single event");
        EventExpression expr = new FalseEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, event1);
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.getMatches().size() == 1);
        assertTrue(action.matches.contains(event1));
        assertTrue(action.matches.size() == 1);
        assertTrue(expr.getMatches().isEmpty());
    }


    /**
     * Test logical not with false expression, multiple events
     */
    @Test
    public void testNegateFalse_MultipleEvents() {
        System.out.println("Logical NOT of false expression, multiple events");
        EventExpression expr = new FalseEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.hasMatched(event2));
        assertTrue(instance.getMatches().size() == 2);
        assertTrue(action.matches.contains(event1));
        assertTrue(action.matches.contains(event2));
        assertTrue(action.matches.size() == 2);
        assertTrue(expr.getMatches().isEmpty());
    }


    /**
     * Test logical not with false expression, same event twice
     */
    @Test
    public void testNegateFalse_EventRepeated() {
        System.out.println("Logical NOT of false expression, single event repeated");
        EventExpression expr = new FalseEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event1);
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.getMatches().size() == 1);
        assertTrue(action.matches.contains(event1));
        assertTrue(action.matches.size() == 2);
        assertTrue(expr.getMatches().isEmpty());
    }


    /**
     * Test logical not with false expression, null event
     */
    @Ignore @Test
    public void testNegateFalse_NullEvent() {
        System.out.println("Logical NOT of false expression, single event");
        fail("Need to update for expected behaviour when events are null");
        EventExpression expr = new FalseEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, null);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(null));
        assertTrue(action.matches.contains(null));
        assertTrue(expr.getMatches().isEmpty());
    }

    /**
     * Test logical not with TRUE expression
     */
    @Test
    public void testNegateTrue() {
        System.out.println("Logical NOT of true expression");
        EventExpression expr = new TrueEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.hasMatched(event1));
        assertTrue(instance.getMatches().isEmpty());
        assertFalse(action.called);
    }


    /**
     * Test logical not with true expression, null event
     */
    @Ignore @Test
    public void testNegateTrue_NullEvent() {
        System.out.println("Logical NOT of false expression, single event");
        fail("Need to update for expected behaviour when events are null");
        EventExpression expr = new TrueEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, null);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(null));
        assertTrue(action.matches.isEmpty());
    }

    /**
     * Test of getExpression method, of class EventLogicalNOT.
     */
    @Test
    public void testGetExpression() {
        System.out.println("Test getExpression");
        EventExpression expr = new TrueEventExpression();
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        EventExpression result = instance.getExpression();
        assertEquals(expr, result);
    }

    /**
     * Test of handling of null expressions
     */
    @Test
    public void testNullExpression() {
        System.out.println("Test null expression");
        EventExpression expr = null;
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 0);
    }

    /**
     * Test of handling of true complex expressions
     */
    @Test
    public void testTrueComplexExpression() {
        System.out.println("Test true complex expression");
        List<EventExpression> list = new ArrayList<EventExpression>(2);
        list.add(new TrueExpression()); list.add(new TrueExpression());
        EventExpression expr = new ANDExpression(list);
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 0);
    }

    /**
     * Test of handling of true complex expressions
     */
    @Test
    public void testFalseComplexExpression() {
        System.out.println("Test false complex expression");
        List<EventExpression> list = new ArrayList<EventExpression>(2);
        list.add(new TrueExpression()); list.add(new FalseEventExpression());
        EventExpression expr = new ANDExpression(list);
        EventLogicalNOT instance = new EventLogicalNOT(expr);
        instance.execute((AddEventTrigger) null, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertEquals(instance.getMatches().size(), 1);
    }
}