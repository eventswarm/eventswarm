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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

/**
 *
 * @author andyb
 */
public class FalseTransitionTest extends TestCase {

    public FalseTransitionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    private Event event1, event2, event3;
    private EventMatchActionImpl action;
    private MutableExpression expr;

    public void setUp() throws Exception {
        event1 = TestEvents.jdoEvent;
        event2 = TestEvents.jdoEventAfterDiffSrcConcSeq;
        event3 = TestEvents.jdoEventAfterDiffSrcAfterSeq;
        action = new EventMatchActionImpl();
        expr = new MutableExpression();
    }

    /**
     * Test of execute method, of class FalseTransition.
     */
    @Test
    public void testFirstTransition() {
        System.out.println("First transition from true to false");
        FalseTransition instance = new FalseTransition(expr);
        instance.registerAction(action);
        expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertTrue(action.called);
        assertEquals(1, action.matches.size());
        assertTrue(action.matches.contains(event2));
    }

    /**
     * Test of execute method, of class FalseTransition.
     */
    @Test
    public void testFalseToFalseTransition() {
        System.out.println("Transition from false to false");
        FalseTransition instance = new FalseTransition(expr);
        instance.registerAction(action);
        expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event2);
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event3);
        assertFalse(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertTrue(action.matches.contains(event2));
    }

    /**
     * Test of execute method, of class FalseTransition.
     */
    @Test
    public void testSecondTransition() {
        System.out.println("Second transition from true to false");
        FalseTransition instance = new FalseTransition(expr, true);
        instance.registerAction(action);
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event1);
        expr.setResult(true);
        instance.execute((AddEventTrigger) null, event2);
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.isTrue());
        assertEquals(2, action.matches.size());
        assertTrue(action.matches.contains(event1));
        assertTrue(action.matches.contains(event3));
    }

    /**
     * Test of clear method, of class FalseTransition.
     */
    /**
     * Test of clear method, of class FalseTransition.
     */
    @Test
    public void testClearTrueInstance() {
        System.out.println("Clear makes true instance false");
        FalseTransition instance = new FalseTransition(expr, true);;
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        assertFalse(instance.isTrue());
    }

    /**
     * Test of clear method, of class FalseTransition.
     */
    @Test
    public void testAddAfterClear() {
        System.out.println("Add after clear makes instance true");
        FalseTransition instance = new FalseTransition(expr, true);
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
    }
}