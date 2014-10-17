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
import com.eventswarm.expressions.Value;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class StatisticsAbstractionTest {
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
            @Override
            public Number getValue(Event event) {
                return data.get(event);
            }
        };
    }

    @Test
    public void test_construct() throws Exception {
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        Assert.assertEquals(0, instance.getCount());
        Assert.assertEquals(0.0, instance.getMean(), 0.0);
        Assert.assertEquals(0.0, instance.getVariance(), 0.0);
        Assert.assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_first() throws Exception {
        data.put(event1, 1.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertEquals(1, instance.getCount());
        Assert.assertEquals(1.0, instance.getMean(), 0.0);
        Assert.assertEquals(0.0, instance.getVariance(), 0.0);
        Assert.assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_two_different() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        Assert.assertEquals(2, instance.getCount());
        Assert.assertEquals(1.5, instance.getMean(), 0.0);
        Assert.assertEquals(0.25, instance.getVariance(), 0.0);
        Assert.assertEquals(0.5, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_add_three_different() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        data.put(event3, 3.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        Assert.assertEquals(3, instance.getCount());
        Assert.assertEquals(6.0 / 3.0, instance.getMean(), 0.0);
        Assert.assertEquals(2.0 / 3.0, instance.getVariance(), 0.0);
        Assert.assertTrue((instance.getStdDev()-0.8165) < 0.00005);
    }

    @Test
    public void test_add_three_same() throws Exception {
        data.put(event1, 2.0);
        data.put(event2, 2.0);
        data.put(event3, 2.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        Assert.assertEquals(3, instance.getCount());
        Assert.assertEquals(6.0 / 3.0, instance.getMean(), 0.0);
        Assert.assertEquals(0.0, instance.getVariance(), 0.0);
        Assert.assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_remove_empty() throws Exception {
        data.put(event1, 2.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((RemoveEventTrigger) null, event1);
        Assert.assertEquals(0, instance.getCount());
        Assert.assertEquals(0.0, instance.getMean(), 0.0);
        Assert.assertEquals(0.0, instance.getVariance(), 0.0);
        Assert.assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_remove_leave_empty() throws Exception {
        data.put(event1, 2.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((RemoveEventTrigger) null, event1);
        Assert.assertEquals(0, instance.getCount());
        Assert.assertEquals(0.0, instance.getMean(), 0.0);
        Assert.assertEquals(0.0, instance.getVariance(), 0.0);
        Assert.assertEquals(0.0, instance.getStdDev(), 0.0);
    }


    @Test
    public void test_remove_leave_one() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((RemoveEventTrigger) null, event1);
        Assert.assertEquals(1, instance.getCount());
        Assert.assertEquals(2.0, instance.getMean(), 0.0);
        Assert.assertEquals(0.0, instance.getVariance(), 0.0);
        Assert.assertEquals(0.0, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_remove_leave_two() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        data.put(event3, 3.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        instance.execute((RemoveEventTrigger) null, event1);
        Assert.assertEquals(2, instance.getCount());
        Assert.assertEquals(2.5, instance.getMean(), 0.0);
        Assert.assertEquals(0.25, instance.getVariance(), 0.0);
        Assert.assertEquals(0.5, instance.getStdDev(), 0.0);
    }

    @Test
    public void test_get_value_empty() throws Exception {
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        Assert.assertEquals(0, instance.getCountValue().getValue());
        Assert.assertEquals(0.0, instance.getMeanValue().getValue());
        Assert.assertEquals(0.0, instance.getVarianceValue().getValue());
        Assert.assertEquals(0.0, instance.getStdDevValue().getValue());
    }

    @Test
    public void test_get_count_value_after_change() throws Exception {
        data.put(event1, 1.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        Value<Number> valueInstance = instance.getCountValue();
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertEquals(1, valueInstance.getValue());
    }

    @Test
    public void test_get_mean_value_after_change() throws Exception {
        data.put(event1, 1.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        Value<Number> valueInstance = instance.getMeanValue();
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertEquals(1.0, valueInstance.getValue());
    }

    @Test
    public void test_get_variance_value_after_change() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        Value<Number> valueInstance = instance.getVarianceValue();
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        Assert.assertEquals(0.25, valueInstance.getValue());
    }

    @Test
    public void test_get_stddev_value_after_change() throws Exception {
        data.put(event1, 1.0);
        data.put(event2, 2.0);
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        Value<Number> valueInstance = instance.getStdDevValue();
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        Assert.assertEquals(0.5, valueInstance.getValue());
    }

    @Test
    public void test_add_null_value() throws Exception {
        StatisticsAbstraction instance = new StatisticsAbstraction(retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertEquals(0, instance.getCount());
        Assert.assertEquals(0.0, instance.getMean(), 0.0);
        Assert.assertEquals(0.0, instance.getVariance(), 0.0);
        Assert.assertEquals(0.0, instance.getStdDev(), 0.0);
    }

}
