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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.SizeThresholdAction;
import com.eventswarm.SizeThresholdTrigger;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.eventset.PassThruImpl;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.Before;

import java.util.SortedSet;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ThresholdMatchAddActionTest {
    SizeThresholdTrigger trigger, triggerPt;
    EventSet eventSet;
    AddEventAction catcher;
    Event result;

    @Before
    public void setup() throws Exception {
       eventSet = new EventSet();
       trigger = new SizeTrigger(eventSet);
       triggerPt = new SizeTrigger(new PassThruImpl());
       catcher = new AddEventAction() {
            @Override
            public void execute(AddEventTrigger trigger, Event event) {
                result = event;
            }
        };
    }

    @Test
    public void testExecuteNullTrigger() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(2);
        instance.registerAction(catcher);
        Event event = makeEvent(1);
        instance.execute(null, event, 2);
        assertEquals(event, result);
    }

    @Test
    public void testExecuteNullSource() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(2);
        instance.registerAction(catcher);
        Event event = makeEvent(1);
        SizeThresholdTrigger trigger1= new SizeTrigger(null);
        instance.execute(trigger1, event, 2);
        assertEquals(event, result);
    }

    @Test
    public void testExecuteThreshold1() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(1);
        instance.registerAction(catcher);
        addEvents(2, eventSet);
        instance.execute(trigger, eventSet.first(), 1);
        assertEquals(eventSet.first(), result);
    }

    @Test
    public void testExecuteThreshold2PassThru() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(2);
        instance.registerAction(catcher);
        Event event = makeEvent(1);
        instance.execute(triggerPt, event, 2);
        assertEquals(event, result);
    }

    @Test
    public void testExecuteThreshold2EventsetEmpty() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(2);
        instance.registerAction(catcher);
        SizeThresholdTrigger trigger1= new SizeTrigger(new EventSet());
        Event event = makeEvent(1);
        instance.execute(trigger1, event, 2);
        assertNull(result);
    }

    @Test
    public void testExecuteThreshold2Limit3() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(3);
        instance.registerAction(catcher);
        addEvents(2, eventSet);
        instance.execute(trigger, eventSet.first(), 2);
        assertTrue(Activity.class.isInstance(result));
        SortedSet<Event> events = ((Activity) result).getEvents();
        assertEquals(2, events.size());
        assertEquals(events.first(), eventSet.first());
        assertEquals(events.last(), eventSet.last());
    }

    @Test
    public void testExecuteThreshold3Limit2() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(2);
        instance.registerAction(catcher);
        addEvents(3, eventSet);
        instance.execute(trigger, eventSet.first(), 3);
        assertTrue(Activity.class.isInstance(result));
        SortedSet<Event> events = ((Activity) result).getEvents();
        assertEquals(2, events.size());
        assertFalse(events.contains(eventSet.first()));
        assertEquals(events.last(), eventSet.last());
    }

    @Test
    public void testExecuteThreshold2Eventset1() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(2);
        instance.registerAction(catcher);
        addEvents(1, eventSet);
        instance.execute(trigger, eventSet.first(), 2);
        assertEquals(result, eventSet.first());
    }

    @Test
    public void testExecuteThreshold3Eventset2() throws Exception {
        ThresholdMatchAddAction instance = new ThresholdMatchAddAction(3);
        instance.registerAction(catcher);
        addEvents(2, eventSet);
        instance.execute(trigger, eventSet.first(), 3);
        assertTrue(Activity.class.isInstance(result));
        SortedSet<Event> events = ((Activity) result).getEvents();
        assertEquals(2, events.size());
        assertEquals(events.first(), eventSet.first());
        assertEquals(events.last(), eventSet.last());
    }

    private void addEvents(int count, AddEventAction action) {
        for (int i=0; i < count; i++) {
            action.execute(null, makeEvent(i));
        }
    }

    private Event makeEvent(int id) {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':" + Integer.toString(id) + "}"));
    }

    private static class SizeTrigger implements SizeThresholdTrigger {
        AddEventTrigger source;

        public SizeTrigger(AddEventTrigger source) {
            this.source = source;
        }

        @Override
        public void registerAction(SizeThresholdAction action) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void unregisterAction(SizeThresholdAction action) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public AddEventTrigger getSource() {
            return source;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
