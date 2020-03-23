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
package com.eventswarm.abstractions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * Test results verified against <a href="http://easycalculation.com/statistics/standard-deviation.php">
 *     http://easycalculation.com/statistics/standard-deviation.php</a>
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class WeightedStatisticsAbstractionTest {
    Event event1, event2, event3, event4, event5;
    Map<Event, Number> values = new HashMap<Event, Number>();
    Map<Event, Integer> weights = new HashMap<Event, Integer>();
    ValueRetriever<Number> valueRetriever;
    ValueRetriever<Integer> weightRetriever;

    static double APPROACHING_ZERO = 0.1e-10;

    @Before
    public void setUp() throws Exception {
        event1 = TestEvents.eventBeforeDiffSrcAfterSeq;
        event2 = TestEvents.eventAfterSameSrcBeforeSeq;
        event3 = TestEvents.event;
        event4 = TestEvents.eventAfterSameSrcAfterSeq;
        event5 = TestEvents.eventAfterDiffSrcAfterSeq;
        valueRetriever = new ValueRetriever<Number>() {
            public Number getValue(Event event) {
                return values.get(event);
            }
        };
        weightRetriever = new ValueRetriever<Integer>() {
            public Integer getValue(Event event) {
                return weights.get(event);
            }
        };
    }

    @Test
    public void test_construct() throws Exception {
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        assertEquals(0, instance.getCount());
        assertEquals(0.0, instance.getMean(), 0.0);
        assertEquals(0.0, instance.getVariance(), 0.0);
        assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_first_weight1() throws Exception {
        values.put(event1, 1.0);
        weights.put(event1, 1);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        assertEquals(1, instance.getCount());
        assertEquals(1.0, instance.getMean(), 0.0);
        assertEquals(0.0, instance.getVariance(), 0.0);
        assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_first_weight2() throws Exception {
        values.put(event1, 1.0);
        weights.put(event1, 2);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        assertEquals(2, instance.getCount());
        assertEquals(1.0, instance.getMean(), 0.0);
        assertEquals(0.0, instance.getVariance(), 0.0);
        assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_two_weight1() throws Exception {
        values.put(event1, 1.0);
        values.put(event2, 2.0);
        weights.put(event1, 1);
        weights.put(event2, 1);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(2, instance.getCount());
        assertEquals(1.5, instance.getMean(), 0.0);
        assertEquals(0.25, instance.getVariance(), 0.0);
        assertEquals(0.5, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_two_weight2() throws Exception {
        values.put(event1, 1.0);
        values.put(event2, 2.0);
        weights.put(event1, 2);
        weights.put(event2, 2);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(4, instance.getCount());
        assertEquals(1.5, instance.getMean(), 0.0);
        assertEquals(0.25, instance.getVariance(), 0.0);
        assertEquals(0.5, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_two_mixed_weight() throws Exception {
        values.put(event1, 1.0);
        values.put(event2, 2.0);
        weights.put(event1, 2);
        weights.put(event2, 1);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(3, instance.getCount());
        assertEquals(4.0/3.0, instance.getMean(), 0.0);
        assertTrue((instance.getVariance() - 0.2222) < 0.00005);
        assertTrue((instance.getStdDev() - 0.4714) < 0.00005);
    }

    @Test
    public void test_remove_first_weight1() throws Exception {
        values.put(event1, 1.0);
        weights.put(event1, 1);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(0, instance.getCount());
        assertTrue(instance.getMean() < APPROACHING_ZERO);
        assertTrue(instance.getVariance() < APPROACHING_ZERO);
        assertTrue(instance.getStdDev() < APPROACHING_ZERO);
    }

    @Test
    public void test_remove_first_weight2() throws Exception {
        values.put(event1, 1.0);
        weights.put(event1, 2);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(0, instance.getCount());
        assertTrue(instance.getMean() < APPROACHING_ZERO);
        assertTrue(instance.getVariance() < APPROACHING_ZERO);
        assertTrue(instance.getStdDev() < APPROACHING_ZERO);
    }

    @Test
    public void test_remove_leave_one_weight1() throws Exception {
        values.put(event1, 1.0);
        weights.put(event1, 1);
        values.put(event2, 2.0);
        weights.put(event2, 1);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(1, instance.getCount());
        assertEquals(2.0, instance.getMean(), 0.0);
        assertTrue(instance.getVariance() < APPROACHING_ZERO);
        assertTrue(instance.getStdDev() < APPROACHING_ZERO);
    }

    @Test
    public void test_remove_leave_one_weight2() throws Exception {
        values.put(event1, 1.0);
        weights.put(event1, 2);
        values.put(event2, 2.0);
        weights.put(event2, 2);
        WeightedStatisticsAbstraction instance = new WeightedStatisticsAbstraction(valueRetriever, weightRetriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        assertEquals(2, instance.getCount());
        assertEquals(2.0, instance.getMean(), 0.0);
        assertTrue(instance.getVariance() < APPROACHING_ZERO);
        assertTrue(Double.isNaN(instance.getStdDev()));
    }
}
