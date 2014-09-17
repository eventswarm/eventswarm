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
package com.eventswarm.powerset;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;
import static com.eventswarm.events.jdo.TestEvents.*;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.expressions.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class PowersetExpressionTest {
    private MutableExpression expr = new MutableExpression();
    private ArrayList<Event> eventMatches = new ArrayList<Event>();
    private ArrayList<ComplexExpressionMatchEvent> complexMatches = new ArrayList<ComplexExpressionMatchEvent>();
    private MutableComplexExpression cexpr = new MutableComplexExpression();

    public ExpressionCreator creator = new ExpressionCreator() {
        @Override
        public Expression newExpression(PowersetExpression owner) {
            return expr;
        }
    };

    public ExpressionCreator cpxCreator = new ExpressionCreator() {
        @Override
        public Expression newExpression(PowersetExpression owner) {
            return cexpr;
        }
    };

    private EventMatchAction eventAction = new EventMatchAction() {
        @Override
        public void execute(EventMatchTrigger trigger, Event event) {
            eventMatches.add(event);
        }
    };

    private ComplexExpressionMatchAction complexAction = new ComplexExpressionMatchAction() {
        @Override
        public void execute(ComplexExpressionMatchTrigger trigger, ComplexExpressionMatchEvent event) {
            complexMatches.add(event);
        }
    };

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void construct() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator);
        assertNotNull(instance);
    }

    @Test
    public void true_add() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator);
        instance.registerAction(eventAction);
        expr.setResult(true);
        EventSet es = new EventSet();
        Event event = jdoEvent;
        es.execute((AddEventTrigger) null, event);
        instance.execute((PowersetAddEventTrigger) null, es, event);
        assertTrue(eventMatches.contains(event));
        assertEquals(1, eventMatches.size());
    }

    @Test
    public void false_add() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator);
        instance.registerAction(eventAction);
        expr.setResult(false);
        EventSet es = new EventSet();
        Event event = jdoEvent;
        es.execute((AddEventTrigger) null, event);
        instance.execute((PowersetAddEventTrigger) null, es, event);
        assertFalse(eventMatches.contains(event));
        assertEquals(0, eventMatches.size());
    }

    /**
     * Test that adding a second event to a subset correctly skips matching on the first event
     *
     * @throws Exception
     */
    @Test
    public void true_add_second() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator);
        instance.registerAction(eventAction);
        EventSet es = new EventSet();
        Event event1 = jdoEvent;
        Event event2 = jdoEventAfterDiffSrcAfterSeq;
        expr.setResult(false);
        es.execute((AddEventTrigger) null, event1);
        instance.execute((PowersetAddEventTrigger) null, es, event1);
        expr.setResult(true);
        es.execute((AddEventTrigger) null, event2);
        instance.execute((PowersetAddEventTrigger) null, es, event2);
        assertEquals(event2, eventMatches.get(0));
        assertEquals(1, eventMatches.size());
    }

    /**
     * Test that adding a third event to a subset correctly skips matching on the first two events
     *
     * @throws Exception
     */
    @Test
    public void true_add_third() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator);
        instance.registerAction(eventAction);
        EventSet es = new EventSet();
        Event event1 = jdoEventBeforeDiffSrcBeforeSeq;
        Event event2 = jdoEventBeforeSameSrcBeforeSeq;
        Event event3 = jdoEvent;
        expr.setResult(false);
        es.execute((AddEventTrigger) null, event1);
        instance.execute((PowersetAddEventTrigger) null, es, event1);
        es.execute((AddEventTrigger) null, event2);
        instance.execute((PowersetAddEventTrigger) null, es, event2);
        expr.setResult(true);
        es.execute((AddEventTrigger) null, event3);
        instance.execute((PowersetAddEventTrigger) null, es, event3);
        assertEquals(event3, eventMatches.get(0));
        assertEquals(1, eventMatches.size());
    }


    /**
     * Test that an eventset is "registered" (expression allocated permanently) when the threshold is reached
     *
     * @throws Exception
     */
    @Test
    public void threshold_add() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator, 1);
        instance.registerAction(eventAction);
        expr.setResult(true);
        Event event1 = jdoEventBeforeDiffSrcBeforeSeq;
        Event event2 = jdoEventBeforeSameSrcBeforeSeq;
        Event event3 = jdoEvent;
        EventSet es = new EventSet();
        es.execute((AddEventTrigger) null, event1);
        instance.execute((PowersetAddEventTrigger) null, es, event1);
        es.execute((AddEventTrigger) null, event2);
        instance.execute((PowersetAddEventTrigger) null, es, event2);
        assertTrue(instance.isRegistered(es));
        es.execute((AddEventTrigger) null, event3);
        instance.execute((PowersetAddEventTrigger) null, es, event3);
        assertEquals(event1, eventMatches.get(0));
        assertEquals(event2, eventMatches.get(1));
        assertEquals(event3, eventMatches.get(2));
    }

    @Test
    public void remove_set_after_registered() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator, 1);
        instance.registerAction(eventAction);
        expr.setResult(true);
        Event event1 = jdoEventBeforeDiffSrcBeforeSeq;
        Event event2 = jdoEventBeforeSameSrcBeforeSeq;
        EventSet es = new EventSet();
        es.execute((AddEventTrigger) null, event1);
        instance.execute((PowersetAddEventTrigger) null, es, event1);
        es.execute((AddEventTrigger) null, event2);
        instance.execute((PowersetAddEventTrigger) null, es, event2);
        assertTrue(instance.isRegistered(es));
        instance.execute((RemoveSetTrigger) null, es, event1);
        assertFalse(instance.isRegistered(es));
    }

    @Test
    public void serialized_actions_false() throws Exception {
        PowersetExpression instance = new PowersetExpression(cpxCreator);
        instance.registerAction(eventAction);
        instance.registerAction(complexAction);
        cexpr.setResult(true);
        EventSet es = new EventSet();
        Event event = jdoEvent;
        es.execute((AddEventTrigger) null, event);
        instance.execute((PowersetAddEventTrigger) null, es, event);
        assertEquals(1, eventMatches.size());
        assertEquals(event, eventMatches.get(0));
        assertEquals(1, complexMatches.size());
        assertEquals(event, complexMatches.get(0).first());
    }

    /**
     * This test doesn't show that serialization works, but shows that it doesn't break the code
     *
     * @throws Exception
     */
    @Test
    public void serialized_actions_true() throws Exception {
        PowersetExpression instance = new PowersetExpression(cpxCreator);
        instance.registerAction(eventAction);
        instance.registerAction(complexAction);
        cexpr.setResult(true);
        instance.enableSerializedActions();
        EventSet es = new EventSet();
        Event event = jdoEvent;
        es.execute((AddEventTrigger) null, event);
        instance.execute((PowersetAddEventTrigger) null, es, event);
        assertEquals(1, eventMatches.size());
        assertEquals(event, eventMatches.get(0));
        assertEquals(1, complexMatches.size());
        assertEquals(event, complexMatches.get(0).first());
    }

    /**
     * Test that recycling correctly clears the expression after use
     *
     */
    public void recycle() throws Exception {
        PowersetExpression instance = new PowersetExpression(creator);
        instance.registerAction(eventAction);
        Event event1 = jdoEventBeforeDiffSrcBeforeSeq;
        Event event2 = jdoEventBeforeSameSrcBeforeSeq;
        EventSet es1 = new EventSet();
        EventSet es2 = new EventSet();
        expr.setResult(true);
        es1.execute((AddEventTrigger) null, event1);
        instance.execute((PowersetAddEventTrigger) null, es1, event1);
        assertEquals(event1, eventMatches.get(0));
        assertEquals(0, expr.getMatches().size());
        expr.setResult(false);
        es2.execute((AddEventTrigger) null, event2);
        instance.execute((PowersetAddEventTrigger) null, es2, event2);
        assertFalse(eventMatches.contains(event2));
        assertEquals(0, expr.getMatches().size());
    }
}
