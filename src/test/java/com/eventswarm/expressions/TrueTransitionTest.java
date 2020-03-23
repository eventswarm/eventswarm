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
import com.eventswarm.events.Activity;
import com.eventswarm.events.jdo.TestEvents;
import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

/**
 *
 * @author andyb
 */
public class TrueTransitionTest extends TestCase {

    public TrueTransitionTest() {
    }

    private Event event1, event2, event3;
    private EventMatchActionImpl action;
    
    public void setUp() throws Exception {
        event1 = TestEvents.jdoEvent;
        event2 = TestEvents.jdoEventAfterDiffSrcConcSeq;
        event3 = TestEvents.jdoEventAfterDiffSrcAfterSeq;
        action = new EventMatchActionImpl();
    }

    public void tearDown() throws Exception {
    }

    /**
     * Test of execute method, of class TrueTransition.
     */
    @Test
    public void testFirstTransition() {
        System.out.println("First transition with alway true expression, initial = default (false)");
        TrueTransition instance = new TrueTransition(new TrueExpression());
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, event1);
        assertTrue(instance.isTrue());
        assertTrue(action.called);
        assertTrue(action.matches.contains(event1));
    }

    /**
     * Test of execute method, of class TrueTransition.
     */
    @Test
    public void testTrueAfterTransition() {
        System.out.println("Second true result after transition");
        TrueTransition instance = new TrueTransition(new TrueExpression());
        instance.registerAction(action);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertFalse(instance.isTrue());
        assertFalse(action.matches.contains(event2));
    }

    /**
     * Test of execute method, of class TrueTransition.
     */
    @Test
    public void testSecondTransition() {
        MutableExpression expr = new MutableExpression();
        System.out.println("Second transition from false to true");
        TrueTransition instance = new TrueTransition(expr);
        instance.registerAction(action);
        expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        expr.setResult(false);
        instance.execute((AddEventTrigger) null, event2);
        expr.setResult(true);
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.isTrue());
        assertTrue(action.matches.contains(event1));
        assertFalse(action.matches.contains(event2));
        assertTrue(action.matches.contains(event3));
    }


    /**
     * Test of clear method, of class TrueTransition.
     */
    @Test
    public void testClearTrueInstance() {
        System.out.println("Clear makes true instance false");
        TrueTransition instance = new TrueTransition(new TrueExpression());;
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        assertFalse(instance.isTrue());
    }

    /**
     * Test of clear method, of class TrueTransition.
     */
    @Test
    public void testAddAfterClear() {
        System.out.println("Add after clear makes instance true");
        TrueTransition instance = new TrueTransition(new TrueExpression());
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
    }

    /**
     * Test first hit on count wrapped around MatchCountExpression
     */
    @Test
    public void testWithMatchCountAbove() {
        System.out.println("MatchCount above threshold");
        TrueTransition instance = new TrueTransition(new MatchCountExpression(new TrueExpression(), 2));
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(1, instance.getMatches().size());
        assertTrue(Activity.class.isInstance(instance.getMatches().first()));
    }

    /**
     * Test first hit on count wrapped around MatchCountExpression
     */
    @Test
    public void testWithMatchCountAboveThenBelow() {
        System.out.println("MatchCount above then below threshold");
        TrueTransition instance = new TrueTransition(new MatchCountExpression(new TrueExpression(), 2));
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(false, instance.isTrue());
        assertEquals(1, instance.getMatches().size());
    }

    /**
     * Test first hit on count wrapped around MatchCountExpression
     */
    @Test
    public void testWithMatchCountAboveThenBelowThenAbove() {
        System.out.println("MatchCount above then below threshold then above again");
        TrueTransition instance = new TrueTransition(new MatchCountExpression(new TrueExpression(), 2));
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event3);
        assertEquals(true, instance.isTrue());
        assertEquals(2, instance.getMatches().size());
    }
}