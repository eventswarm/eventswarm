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
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.schedules.Schedule;
import com.eventswarm.schedules.ScheduleTrigger;
import com.eventswarm.schedules.TickAction;
import com.eventswarm.schedules.TickTrigger;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class AtTimeExpressionTest {
    Event event;
    Map parts = new HashMap<String, EventPart>();
    EventMatchActionImpl matches = new EventMatchActionImpl();

    @Test
    public void construct() throws Exception {
        AtTimeExpression instance = new AtTimeExpression(new TrueExpression());
        assertNotNull(instance);
    }

    @Test
    public void constructWithLimit() throws Exception {
        TrueExpression expr = new TrueExpression();
        AtTimeExpression instance = new AtTimeExpression(22, expr);
        assertNotNull(instance);
        assertEquals(22, instance.getLimit());
        assertEquals(22, expr.getLimit());
    }

    @Test
    public void subordinateMatched() throws Exception {
        event = new JdoEvent(new JdoHeader(new Date().getTime(), "AtTimeExpressionTest"), parts);
        AtTimeExpression instance = new AtTimeExpression(22, new TrueExpression());
        instance.execute((AddEventTrigger) null, event);
        assertEquals(event, instance.getLastMatch());
        assertFalse(instance.isTrue());
    }

    @Test
    public void triggerBeforeMatch() throws Exception {
        AtTimeExpression instance = new AtTimeExpression(22, new TrueExpression());
        instance.registerAction(matches);
        instance.execute((ScheduleTrigger) null, (Schedule) null, new Date());
        assertFalse(matches.called);
        assertEquals(0, matches.matches.size());
        assertFalse(instance.isTrue());
    }

    @Test
    public void triggerAfterMatch() throws Exception {
        event = new JdoEvent(new JdoHeader(new Date().getTime(), "AtTimeExpressionTest"), parts);
        AtTimeExpression instance = new AtTimeExpression(22, new TrueExpression());
        instance.registerAction(matches);
        instance.execute((AddEventTrigger) null, event);
        Date tickTime = new Date();
        instance.execute((ScheduleTrigger) null, (Schedule) null, tickTime);
        assertTrue(matches.called);
        assertEquals(1, matches.matches.size());
        assertEquals(event, matches.matches.get(0));
        assertEquals(tickTime, instance.getLastTick());
        assertTrue(instance.isTrue());
    }

    @Test
    public void triggerAfterRemove() throws Exception {
        event = new JdoEvent(new JdoHeader(new Date().getTime(), "AtTimeExpressionTest"), parts);
        AtTimeExpression instance = new AtTimeExpression(22, new TrueExpression());
        instance.registerAction(matches);
        instance.execute((AddEventTrigger) null, event);
        instance.execute((RemoveEventTrigger) null, event);
        Date tickTime = new Date();
        instance.execute((ScheduleTrigger) null, (Schedule) null, tickTime);
        assertFalse(matches.called);
        assertEquals(0, matches.matches.size());
        assertFalse(instance.isTrue());
        assertEquals(tickTime, instance.getLastTick());
    }

    @Test
    public void remainsTrueAfterRemove() throws Exception {
        event = new JdoEvent(new JdoHeader(new Date().getTime(), "AtTimeExpressionTest"), parts);
        AtTimeExpression instance = new AtTimeExpression(22, new TrueExpression());
        instance.registerAction(matches);
        instance.execute((AddEventTrigger) null, event);
        Date tickTime = new Date();
        instance.execute((ScheduleTrigger) null, (Schedule) null, tickTime);
        instance.execute((RemoveEventTrigger) null, event);
        assertTrue(matches.called);
        assertEquals(1, matches.matches.size());
        assertTrue(instance.isTrue());
        assertEquals(tickTime, instance.getLastTick());
    }

    @Test
    public void becomesFalseAfterTick() throws Exception {
        event = new JdoEvent(new JdoHeader(new Date().getTime(), "AtTimeExpressionTest"), parts);
        AtTimeExpression instance = new AtTimeExpression(22, new TrueExpression());
        instance.registerAction(matches);
        instance.execute((AddEventTrigger) null, event);
        instance.execute((ScheduleTrigger) null, (Schedule) null, new Date());
        instance.execute((RemoveEventTrigger) null, event);
        Date tickTime = new Date();
        instance.execute((ScheduleTrigger) null, (Schedule) null, tickTime);
        assertTrue(matches.called);
        assertEquals(1, matches.matches.size());
        assertFalse(instance.isTrue());
        assertEquals(tickTime, instance.getLastTick());
    }
}
