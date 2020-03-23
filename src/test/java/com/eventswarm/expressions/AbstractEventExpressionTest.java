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
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.eventswarm.events.jdo.TestEvents;

/**
 *
 * @author andyb
 */
public class AbstractEventExpressionTest {

    public AbstractEventExpressionTest() {

    }

    Event event1 = TestEvents.jdoEvent;
    Event event2 = TestEvents.eventAfterDiffSrcAfterSeq;

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
     * Test adding event to a true expression, no registered actions
     */
    @Test
    public void testMatchExpr_NoEvents() {
        System.out.println("No events added, true expression, no registered actions");
        AddEventTrigger trigger = null;
        EventExpression instance = new TrueEventExpression();
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertTrue(instance.getMatches().isEmpty());
    }

    /**
     * Test adding event to a true expression, no registered actions
     */
    @Test
    public void testMatchExpr_TrueExpression() {
        System.out.println("Simple add, true expression, no registered actions");
        AddEventTrigger trigger = null;
        EventExpression instance = new TrueEventExpression();
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.getMatches().contains(event1));
    }

    /**
     * Test adding event to a true expression, one registered action
     */
    @Test
    public void testMatchExpr_TrueExpressionWithAction() {
        System.out.println("Simple add, true expression, one registered actions");
        AddEventTrigger trigger = null;
        EventExpression instance = new TrueEventExpression();
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.getMatches().contains(event1));
        assertTrue(action.matches.contains(event1));
        assertTrue(action.matches.size() == 1);
    }


    /**
     * Test adding event to a true expression, two registered actions
     */
    @Test
    public void testMatchExpr_TrueExpressionWithActions() {
        System.out.println("Simple add, true expression, two registered actions");
        AddEventTrigger trigger = null;
        EventExpression instance = new TrueEventExpression();
        EventMatchActionImpl action1 = new EventMatchActionImpl();
        EventMatchActionImpl action2 = new EventMatchActionImpl();
        instance.registerAction(action1);
        instance.registerAction(action2);
        instance.execute(trigger, event1);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.getMatches().contains(event1));
        assertTrue(instance.getMatches().size() == 1);
        assertTrue(action1.matches.contains(event1));
        assertTrue(action1.matches.size() == 1);
        assertTrue(action2.matches.contains(event1));
        assertTrue(action2.matches.size() == 1);
    }


    /**
     * Test adding multiple events to a true expression, one registered action
     */
    @Test
    public void testMultipleMatchExpr_TrueExpressionWithAction() {
        System.out.println("Add two events, true expression, one registered actions");
        AddEventTrigger trigger = null;
        EventExpression instance = new TrueEventExpression();
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        instance.execute(trigger, event2);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.hasMatched(event2));
        assertTrue(instance.getMatches().contains(event1));
        assertTrue(instance.getMatches().contains(event2));
        assertTrue(action.matches.contains(event1));
        assertTrue(action.matches.contains(event2));
        assertTrue(action.matches.size() == 2);
    }


    /**
     * Test adding event to a false expression, no registered actions
     */
    @Test
    public void testMatchExpr_FalseExpression() {
        System.out.println("Simple add, false expression, no registered actions");
        AddEventTrigger trigger = null;
        EventExpression instance = new FalseEventExpression();
        instance.execute(trigger, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertFalse(instance.getMatches().contains(event1));
    }


    /**
     * Test adding event to a true expression, one registered action
     */
    @Test
    public void testMatchExpr_FalseExpressionWithAction() {
        System.out.println("Simple add, false expression, one registered actions");
        AddEventTrigger trigger = null;
        EventExpression instance = new FalseEventExpression();
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        instance.execute(trigger, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertFalse(instance.getMatches().contains(event1));
        assertFalse(action.called);
    }

    /**
     * Test of event removal with empty set of events.
     */
    @Test
    public void testRemoveEvent_EmptySet() {
        System.out.println("Remove, true expression, no events");
        RemoveEventTrigger trigger = null;
        EventExpression instance = new TrueEventExpression();
        instance.execute(trigger, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertTrue(instance.getMatches().isEmpty());
    }


    /**
     * Test of event removal with empty set of events, action registered
     */
    @Test
    public void testRemoveEvent_EmptySetWithAction() {
        System.out.println("Remove, true expression, no events, action registered");
        EventExpression instance = new TrueEventExpression();
        EventMatchActionImpl action = new EventMatchActionImpl();
        instance.registerAction(action);
        RemoveEventTrigger trigger = null;
        instance.execute(trigger, event1);
        assertFalse(action.called);
    }

    /**
     * Test of event removal with one event matching the removal request.
     */
    @Test
    public void testRemoveEvent_SingleEventMatching() {
        System.out.println("Remove, true expression, one matching event");
        EventExpression instance = new TrueEventExpression();
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event1);
        assertFalse(instance.isTrue());
        assertFalse(instance.hasMatched(event1));
        assertTrue(instance.getMatches().isEmpty());
    }


    /**
     * Test of event removal with one event matching the removal request.
     */
    @Test
    public void testRemoveEvent_SingleEventNotMatching() {
        System.out.println("Remove, true expression, one event not matching");
        EventExpression instance = new TrueEventExpression();
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertTrue(instance.getMatches().size() == 1);
    }

   /**
     * Test of event removal with one event matching the removal request.
     */
    @Test
    public void testRemoveEvent_TwoEventsSingleEventMatching() {
        System.out.println("Remove, true expression, two events, one matching event");
        EventExpression instance = new TrueEventExpression();
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertTrue(instance.hasMatched(event1));
        assertFalse(instance.hasMatched(event2));
        assertTrue(instance.getMatches().size() == 1);
    }

    /**
     * Test of unregisterAction method, of class AbstractEventExpression.
     */
    @Test
    public void testUnregisterAction() {
        System.out.println("unregisterAction");
        EventMatchActionImpl action = new EventMatchActionImpl();
        AbstractEventExpression instance = new TrueEventExpression();
        instance.registerAction(action);
        instance.unregisterAction(action);
        instance.execute((AddEventTrigger) null, event1);
        assertTrue(instance.hasMatched(event1));
        assertFalse(action.called);
    }

}