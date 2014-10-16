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
package com.eventswarm.util;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.SizeThresholdAction;
import com.eventswarm.SizeThresholdTrigger;
import com.eventswarm.events.Event;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static com.eventswarm.events.jdo.TestEvents.*;


import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EscalatorTest {
    private HashMap<Long, Integer> results = new HashMap<Long,Integer>();
    private HashMap<Long,SizeThresholdAction> actions = new HashMap<Long,SizeThresholdAction>();

    @Before
    public void setup() throws Exception {
    }

    @Test
    public void testConstructWithNoActions() throws Exception {
        Escalator instance = new Escalator(null);
        assertNotNull(instance);
        assertEquals(0, instance.getMonitors().size());
        assertEquals(0, instance.getActions().size());
    }

    @Test
    public void testConstructWithOneAction() throws Exception {
        actions.put(1L, makeAction(1L));
        Escalator instance = new Escalator(actions);
        assertNotNull(instance);
        assertEquals(1, instance.getMonitors().size());
        assertEquals(1, instance.getActions().size());
        assertTrue(instance.getActions().get(1L).contains(actions.get(1L)));
    }

    @Test
    public void testConstructEscalatingActions() throws Exception {
        actions.put(1L, makeAction(1L));
        actions.put(2L, makeAction(2L));
        Escalator instance = new Escalator(actions);
        assertNotNull(instance);
        assertEquals(2, instance.getMonitors().size());
        assertEquals(2, instance.getActions().size());
        assertTrue(instance.getActions().get(1L).contains(actions.get(1L)));
        assertTrue(instance.getActions().get(2L).contains(actions.get(2L)));
    }

    @Test
    public void testAddNewAction() throws Exception {
        Escalator instance = new Escalator(null);
        SizeThresholdAction action = makeAction(1L);
        Escalator result = instance.addThresholdAction(1L, action);
        assertEquals(instance, result);
        assertEquals(1, instance.getMonitors().size());
        assertEquals(1, instance.getActions().size());
        assertTrue(instance.getActions().get(1L).contains(action));
    }

    @Test
    public void testAddSecondAction() throws Exception {
        actions.put(1L, makeAction(1L));
        Escalator instance = new Escalator(actions);
        SizeThresholdAction action = makeAction(1L);
        Escalator result = instance.addThresholdAction(1L, action);
        assertEquals(instance, result);
        assertEquals(1, instance.getMonitors().size());
        assertEquals(1, instance.getActions().size());
        assertTrue(instance.getActions().get(1L).contains(actions.get(1L)));
        assertTrue(instance.getActions().get(1L).contains(action));
    }

    @Test
    public void testExecuteSingleThreshold1() throws Exception {
        SizeThresholdAction action = makeAction(1L);
        Escalator instance = new Escalator(null).addThresholdAction(1L, action);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, (long) results.get(1L));
    }

    @Test
    public void testExecuteSingleThreshold2() throws Exception {
        SizeThresholdAction action1 = makeAction(1L);
        SizeThresholdAction action2 = makeAction(1L);
        Escalator instance = new Escalator(null).addThresholdAction(1L, action1).addThresholdAction(1L, action2);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, (long) results.get(1L));
    }

    @Test
    public void testExecuteMultipleThresholdFirst() throws Exception {
        actions.put(1L, makeAction(1L));
        actions.put(2L, makeAction(2L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, (long) results.get(1L));
    }

    @Test
    public void testExecuteMultipleThresholdSecond() throws Exception {
        actions.put(1L, makeAction(1L));
        actions.put(2L, makeAction(2L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, (long) results.get(1L));
        assertEquals(1, (long) results.get(2L));
    }

    @Test
    public void testResetAtFirst() throws Exception {
        actions.put(1L, makeAction(1L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, (long) results.get(1L));
    }

    @Test
    public void testResetAtSecond() throws Exception {
        actions.put(1L, makeAction(1L));
        actions.put(2L, makeAction(2L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, (long) results.get(1L));
        assertEquals(1, (long) results.get(2L));
    }

    @Test
    public void testReset() throws Exception {
        actions.put(2L, makeAction(2L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.reset();
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, (long) results.get(2L));
    }

    @Test
    public void testClearBeforeMatch() throws Exception {
        actions.put(2L, makeAction(2L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.clear();
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(null, results.get(2L));
    }

    @Test
    public void testClearAfterMatch() throws Exception {
        actions.put(2L, makeAction(2L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, (long) results.get(2L));
        instance.clear();
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1,(long)  results.get(2L));
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, (long) results.get(2L));
    }
    @Test
    public void testAddActionAfterStart() throws Exception {
        actions.put(1L, makeAction(1L));
        Escalator instance = new Escalator(actions);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.addThresholdAction(2L, makeAction(2L));
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, (long) results.get(1L));
        assertEquals(1, (long) results.get(2L));
    }

    @Test
    public void testRemoveOnlyAction() throws Exception {
        SizeThresholdAction action = makeAction(1L);
        Escalator instance = new Escalator(null).addThresholdAction(1L, action);
        instance.removeThresholdAction(1L, action);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(0, instance.getActions().size());
        assertEquals(0, instance.getMonitors().size());
        assertEquals(null, results.get(1L));
    }

    @Test
    public void testRemoveOneOfTwoFromFirst() throws Exception {
        SizeThresholdAction action1 = makeAction(1L);
        SizeThresholdAction action2 = makeAction(1L);
        Escalator instance = new Escalator(null).addThresholdAction(1L, action1).addThresholdAction(1L, action2);
        instance.removeThresholdAction(1L, action1);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, instance.getActions().size());
        assertEquals(1, instance.getMonitors().size());
        assertEquals(1, (long) results.get(1L));
        assertFalse(instance.getActions().get(1L).contains(action1));
        assertTrue(instance.getActions().get(1L).contains(action2));
        assertEquals(1, instance.getMonitors().size());
    }

    @Test
    public void testRemoveSecondThreshold() throws Exception {
        SizeThresholdAction action1 = makeAction(1L);
        SizeThresholdAction action2 = makeAction(2L);
        Escalator instance = new Escalator(null).addThresholdAction(1L, action1).addThresholdAction(2L, action2);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.removeThresholdAction(2L, action2);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, instance.getActions().size());
        assertEquals(1, instance.getMonitors().size());
        assertEquals(1, (long) results.get(1L));
        assertEquals(null, results.get(2L));
        assertTrue(instance.getActions().get(1L).contains(action1));
        assertNull(instance.getActions().get(2L));
        assertNotNull(instance.getMonitors().get(1L));
        assertNull(instance.getMonitors().get(2L));
    }

    private SizeThresholdAction makeAction(long threshold) {
        return new SizeThresholdAction() {
            @Override
            public void execute(SizeThresholdTrigger trigger, Event event, long size) {
                addResult(size);
            }
        };
    }

    private void addResult(long size) {
        if (results.containsKey(size)) {
            int count = results.get(size);
            results.put(size, count + 1);
        } else {
            results.put(size, 1);
        }
    }
}
