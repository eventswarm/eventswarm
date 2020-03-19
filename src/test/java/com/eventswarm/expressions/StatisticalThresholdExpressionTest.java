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
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class StatisticalThresholdExpressionTest {
    Event event1, event2, event3, event4, event5;
    Map<Event, Number> data = new HashMap<Event, Number>();
    ValueRetriever<Number> retriever;

    @Before
    public void setUp() throws Exception {
        event1 = TestEvents.eventBeforeDiffSrcAfterSeq;
        event2 = TestEvents.eventAfterSameSrcBeforeSeq;
        event3 = TestEvents.event;
        event4 = TestEvents.eventAfterSameSrcAfterSeq;
        event5 = TestEvents.eventAfterDiffSrcAfterSeq;
        retriever = new ValueRetriever<Number>() {
            public Number getValue(Event event) {
                return data.get(event);
            }
        };
    }

    @Test
    public void construct_default_min() throws Exception {
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0);
        assertNotNull(instance);
        assertEquals(StatisticalThresholdExpression.MINIMUM_COUNT, instance.getMinCount());
        assertEquals(1.0, instance.getMultiple());
        assertEquals(retriever, instance.getRetriever());
    }

    @Test
    public void construct_min0() throws Exception {
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 0);
        assertNotNull(instance);
        assertEquals(0, instance.getMinCount());
        assertEquals(1.0, instance.getMultiple());
        assertEquals(retriever, instance.getRetriever());
    }

    @Test
    public void construct_min1() throws Exception {
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 1);
        assertNotNull(instance);
        assertEquals(1, instance.getMinCount());
        assertEquals(1.0, instance.getMultiple());
        assertEquals(retriever, instance.getRetriever());
    }

    @Test
    public void construct_min1_limit1() throws Exception {
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 1, 1);
        assertNotNull(instance);
        assertEquals(1, instance.getMinCount());
        assertEquals(1.0, instance.getMultiple());
        assertEquals(retriever, instance.getRetriever());
        assertEquals(1, instance.getLimit());
    }

    @Test
    public void add_less_than_min_false() throws Exception {
        data.put(event1, 1.0);
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 1);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.hasMatched(event1));
    }

    @Test
    public void add_at_min_false() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        data.put(event3, 2.0);
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 2);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.hasMatched(event1));
        instance.execute((AddEventTrigger) null, event2);
        assertFalse(instance.hasMatched(event2));
        instance.execute((AddEventTrigger) null, event3);
        assertFalse(instance.hasMatched(event3));
    }

    @Test
    public void add_at_min_true() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        data.put(event3, 2.0);
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 0.9, 2);
        instance.execute((AddEventTrigger) null, event1);
        assertFalse(instance.hasMatched(event1));
        instance.execute((AddEventTrigger) null, event2);
        assertFalse(instance.hasMatched(event2));
        instance.execute((AddEventTrigger) null, event3);
        assertTrue(instance.hasMatched(event3));
    }

    @Test
    public void add_less_than_min_true() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        data.put(event3, 2.0);
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 0.9, 3);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        assertFalse(instance.hasMatched(event3));
    }

    @Test
    public void remove_empty() throws Exception {
        data.put(event1, 1.0);
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 1);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(0, instance.getStats().getCount());
    }

    @Test
    public void remove_leaving_empty() throws Exception {
        data.put(event1, 1.0);
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 1);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(0, instance.getStats().getCount());
    }

    @Test
    public void remove_leaving_one() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        StatisticalThresholdExpression instance = new StatisticalThresholdExpression(retriever, 1.0, 1);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(1, instance.getStats().getCount());
        assertEquals(2.0, instance.getStats().getMean());
        assertEquals(0.0, instance.getStats().getStdDev());
    }

}
