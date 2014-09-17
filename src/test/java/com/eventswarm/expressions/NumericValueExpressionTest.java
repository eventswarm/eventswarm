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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.expressions.NumericValueExpression.Comparator;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class NumericValueExpressionTest {
    Event event1, event2, event3;
    ValueRetriever<Number> retriever;
    Map<Event, Number> data = new HashMap<Event, Number>();

    @Before
    public void setUp() throws Exception {
        event1 = TestEvents.eventBeforeDiffSrcAfterSeq;
        event2 = TestEvents.event;
        event3 = TestEvents.eventAfterDiffSrcAfterSeq;
        retriever = new ValueRetriever<Number>() {
            @Override
            public Number getValue(Event event) {
                return data.get(event);
            }
        };
    }

    @Test
    public void construct() throws Exception {
        Value<Number> value = new ConstantValue<Number>(1);
        NumericValueExpression instance = new NumericValueExpression(Comparator.EQUAL, value, retriever);
        Assert.assertNotNull(instance);
    }

    @Test
    public void equals_true_integer() throws Exception {
        data.put(event1, 1);
        Value<Number> value = new ConstantValue<Number>(1);
        NumericValueExpression instance = new NumericValueExpression(Comparator.EQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void less_true_integer() throws Exception {
        data.put(event1, 1);
        Value<Number> value = new ConstantValue<Number>(2);
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESS, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_integer_same_value() throws Exception {
        data.put(event1, 2);
        Value<Number> value = new ConstantValue<Number>(2);
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_integer_diff_value() throws Exception {
        data.put(event1, 1);
        Value<Number> value = new ConstantValue<Number>(2);
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greater_true_integer() throws Exception {
        data.put(event1, 2);
        Value<Number> value = new ConstantValue<Number>(1);
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATER, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_integer_same_value() throws Exception {
        data.put(event1, 2);
        Value<Number> value = new ConstantValue<Number>(2);
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_integer_diff_value() throws Exception {
        data.put(event1, 2);
        Value<Number> value = new ConstantValue<Number>(1);
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void equals_true_long() throws Exception {
        data.put(event1, new Long(1));
        Value<Number> value = new ConstantValue<Number>(new Long(1));
        NumericValueExpression instance = new NumericValueExpression(Comparator.EQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void less_true_long() throws Exception {
        data.put(event1, new Long(1));
        Value<Number> value = new ConstantValue<Number>(2);
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESS, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_long_same_value() throws Exception {
        data.put(event1, new Long(2));
        Value<Number> value = new ConstantValue<Number>(new Long(2));
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_long_diff_value() throws Exception {
        data.put(event1, new Long(1));
        Value<Number> value = new ConstantValue<Number>(new Long(2));
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greater_true_long() throws Exception {
        data.put(event1, new Long(2));
        Value<Number> value = new ConstantValue<Number>(new Long(1));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATER, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_long_same_value() throws Exception {
        data.put(event1, new Long(2));
        Value<Number> value = new ConstantValue<Number>(new Long(2));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_long_diff_value() throws Exception {
        data.put(event1, new Long(2));
        Value<Number> value = new ConstantValue<Number>(new Long(1));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void equals_true_float() throws Exception {
        data.put(event1, new Float(1.0));
        Value<Number> value = new ConstantValue<Number>(new Float(1.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.EQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void less_true_float() throws Exception {
        data.put(event1, new Float(1.0));
        Value<Number> value = new ConstantValue<Number>(2);
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESS, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_float_same_value() throws Exception {
        data.put(event1, new Float(2.0));
        Value<Number> value = new ConstantValue<Number>(new Float(2.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_float_diff_value() throws Exception {
        data.put(event1, new Float(1.0));
        Value<Number> value = new ConstantValue<Number>(new Float(2.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greater_true_float() throws Exception {
        data.put(event1, new Float(2.0));
        Value<Number> value = new ConstantValue<Number>(new Float(1.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATER, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_float_same_value() throws Exception {
        data.put(event1, new Float(2.0));
        Value<Number> value = new ConstantValue<Number>(new Float(2.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_float_diff_value() throws Exception {
        data.put(event1, new Float(2.0));
        Value<Number> value = new ConstantValue<Number>(new Float(1.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void equals_true_double() throws Exception {
        data.put(event1, new Double(1.0));
        Value<Number> value = new ConstantValue<Number>(new Double(1.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.EQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void less_true_double() throws Exception {
        data.put(event1, new Double(1.0));
        Value<Number> value = new ConstantValue<Number>(2);
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESS, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_double_same_value() throws Exception {
        data.put(event1, new Double(2.0));
        Value<Number> value = new ConstantValue<Number>(new Double(2.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void lessequal_true_double_diff_value() throws Exception {
        data.put(event1, new Double(1.0));
        Value<Number> value = new ConstantValue<Number>(new Double(2.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.LESSOREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greater_true_double() throws Exception {
        data.put(event1, new Double(2.0));
        Value<Number> value = new ConstantValue<Number>(new Double(1.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATER, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_double_same_value() throws Exception {
        data.put(event1, new Double(2.0));
        Value<Number> value = new ConstantValue<Number>(new Double(2.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }

    @Test
    public void greaterequal_true_double_diff_value() throws Exception {
        data.put(event1, new Double(2.0));
        Value<Number> value = new ConstantValue<Number>(new Double(1.0));
        NumericValueExpression instance = new NumericValueExpression(Comparator.GREATEROREQUAL, value, retriever);
        instance.execute((AddEventTrigger) null, event1);
        Assert.assertTrue(instance.hasMatched(event1));
    }
}
