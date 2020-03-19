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
package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MatchCountExpressionTest extends TestCase {
    private EventMatchActionImpl action = new EventMatchActionImpl();
    private MutableExpression mutable_expr = new MutableExpression();
    private Event event1, event2, event3;

    @Before
    public void setUp() throws Exception {
        event1 = TestEvents.eventBeforeSameSrcBeforeSeq;
        event2 = TestEvents.event;
        event3 = TestEvents.eventAfterDiffSrcAfterSeq;
    }

    @Test
    public void test_construct_thresh0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        assertTrue(instance.isTrue());
    }

    @Test
    public void test_construct_thresh1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        assertFalse(instance.isTrue());
    }

    @Test
    public void test_add_thresh0_size0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
    }

    @Test
    public void test_add_thresh0_size1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(2, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(1)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
    }

    @Test
    public void test_add_thresh1_size0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
    }

    @Test
    public void test_add_false_thresh1_size0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(false);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.isTrue());
        assertEquals(0, action.matches.size());
    }

    @Test
    public void test_add_thresh1_size1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(2, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(1)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
    }

    @Test
    public void test_add_thresh1_size1_limit1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1, 5, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(2, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(1)).getEvents().size()));
        assertFalse(((Activity) action.matches.get(1)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
    }


    @Test
    public void test_add_thresh1_size2_limit2() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1, 5, 2);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.isTrue());
        assertEquals(3, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(2)).getEvents().size()));
        assertFalse(((Activity) action.matches.get(2)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(2)).getEvents().contains(event2));
        assertTrue(((Activity) action.matches.get(2)).getEvents().contains(event3));
    }

    // should correctly reset limit if threshold is greater than limit
    @Test
    public void test_add_thresh2_size1_limit1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 2, 5, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event2));
    }

    // should correctly reset limit if threshold is greater than limit
    @Test
    public void test_add_thresh2_size2_limit1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 2, 5, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.isTrue());
        assertEquals(2, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(1)).getEvents().size()));
        assertFalse(((Activity) action.matches.get(1)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event3));
    }

    @Test
    public void test_add_false_thresh1_size1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        mutable_expr.setResult(false);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertFalse(((Activity) action.matches.get(0)).getEvents().contains(event2));
    }

    @Test
    public void test_add_thresh2_size0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 2);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.isTrue());
        assertEquals(0, action.matches.size());
    }

    @Test
    public void test_add_thresh2_size1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 2);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event2));
    }


    @Test
    public void test_add_thresh2_size2() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 2);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.isTrue());
        assertEquals(2, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event2));
        assertEquals(3, (((Activity) action.matches.get(1)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event3));
    }


    @Test
    public void test_add_false_thresh2_size1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 2);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        mutable_expr.setResult(false);
        instance.execute((AddEventTrigger) null, event2);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertFalse(((Activity) action.matches.get(0)).getEvents().contains(event2));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event3));
    }

    @Test
    public void test_add_false_thresh2_size2() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 2);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        mutable_expr.setResult(false);
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(2, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event2));
        assertFalse(((Activity) action.matches.get(0)).getEvents().contains(event3));
    }

    @Test
    public void test_remove_size0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((RemoveEventTrigger) null, event1);
        assertTrue(instance.isTrue());
        assertEquals(0, action.matches.size());
    }


    @Test
    public void test_remove_size1_thresh0_matched() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event1);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
    }


    @Test
    public void test_remove_size1_thresh1_matched() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event1);
        assertFalse(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
    }

    @Test
    public void test_remove_size1_thresh1_unmatched() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(1, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
    }

    @Test
    public void test_remove_size2_thresh1_matched() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        assertTrue(instance.isTrue());
        assertEquals(2, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertEquals(2, (((Activity) action.matches.get(1)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event1));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
    }

    @Test
    public void testClear_size0_thresh0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        instance.registerAction(action);
        instance.clear();
        assertTrue(instance.isTrue());
        assertEquals(0, action.matches.size());
    }

    @Test
    public void testClear_size1_thresh0() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        assertTrue(instance.isTrue());
        assertEquals(0, instance.getMatches().size());
        assertEquals(1, action.matches.size());
    }

    @Test
    public void testClear_size1_thresh0_then_add() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 0);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(1, instance.getMatches().size());
        assertEquals(2, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertEquals(1, (((Activity) action.matches.get(1)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
    }


    @Test
    public void testClear_size1_thresh1() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        assertFalse(instance.isTrue());
        assertEquals(0, instance.getMatches().size());
        assertEquals(1, action.matches.size());
    }


    @Test
    public void testClear_size1_thresh1_then_add() throws Exception {
        MatchCountExpression instance = new MatchCountExpression(mutable_expr, 1);
        instance.registerAction(action);
        mutable_expr.setResult(true);
        instance.execute((AddEventTrigger) null, event1);
        instance.clear();
        instance.execute((AddEventTrigger) null, event2);
        assertTrue(instance.isTrue());
        assertEquals(1, instance.getMatches().size());
        assertEquals(2, action.matches.size());
        assertEquals(1, (((Activity) action.matches.get(0)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(0)).getEvents().contains(event1));
        assertEquals(1, (((Activity) action.matches.get(1)).getEvents().size()));
        assertTrue(((Activity) action.matches.get(1)).getEvents().contains(event2));
    }
}
