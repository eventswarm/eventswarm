/**
 * Copyright 2020 Andrew Berry
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
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class ValueGradientExpressionTest implements EventMatchAction {
  ValueRetriever<Double> retriever = new JsonEvent.DoubleRetriever("value");
  ArrayList<Event> matches = new ArrayList<Event>();
  
  @Test
  public void testConstruct() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, -1);
    assertNotNull(subject);
  }


  @Test
  public void testNotEnough() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, -1);
    subject.registerAction(this);
    Event first = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, first);
    assertEquals(0, matches.size());
  }

  @Test
  public void testMatchDown() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, -1);
    subject.registerAction(this);
    Event first = makeEvent(10.0);
    Event second = makeEvent(5.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(1, matches.size());
    Activity match = (Activity) matches.get(0);
    assertEquals(first, match.first());
    assertEquals(second, match.last());
  }

  @Test
  public void testNotMatchDown() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, -1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(0, matches.size());
  }


  @Test
  public void testMatchUp() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(0.0);
    Event second = makeEvent(5.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(1, matches.size());
    Activity match = (Activity) matches.get(0);
    assertEquals(first, match.first());
    assertEquals(second, match.last());
  }

  @Test
  public void testNotMatchUp() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(0.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(0, matches.size());
  }

  @Test
  public void testMatchFlat() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 0);
    subject.registerAction(this);
    Event first = makeEvent(1.0);
    Event second = makeEvent(1.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(1, matches.size());
    Activity match = (Activity) matches.get(0);
    assertEquals(first, match.first());
    assertEquals(second, match.last());
  }

  @Test
  public void testNotMatchFlat() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 0);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(0, matches.size());
  }

  @Test
  public void testNextMatch() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    Event third = makeEvent(15.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    assertEquals(2, matches.size());
    Activity match = (Activity) matches.get(1);
    assertEquals(second, match.first());
    assertEquals(third, match.last());
  }

  @Test
  public void testMatchThenNotMatch() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    Event third = makeEvent(5.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    assertEquals(1, matches.size());
    Activity match = (Activity) matches.get(0);
    assertEquals(first, match.first());
    assertEquals(second, match.last());
  }

  @Test
  public void testNotMatchThenMatch() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(10.0);
    Event second = makeEvent(5.0);
    Event third = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    assertEquals(1, matches.size());
    Activity match = (Activity) matches.get(0);
    assertEquals(second, match.first());
    assertEquals(third, match.last());
  }

  @Test
  public void testIsTrue() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    Event third = makeEvent(15.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    assertEquals(2, matches.size());
    assertTrue(subject.isTrue());
  }

  @Test
  public void testNotTrue() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    Event third = makeEvent(5.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    assertEquals(1, matches.size());
    assertFalse(subject.isTrue());
  }

  @Test
  public void testHasMatched() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    Event third = makeEvent(15.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    assertEquals(2, matches.size());
    assertFalse(subject.hasMatched(first)); // not in window
    assertTrue(subject.hasMatched(second));
    assertTrue(subject.hasMatched(third));
    Event fourth = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, fourth); // make expression false
    assertFalse(subject.hasMatched(third));
    assertFalse(subject.hasMatched(fourth));
  }


  @Test
  public void testOutOfOrderMatch() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, first);
    assertEquals(1, matches.size());
    Activity match = (Activity) matches.get(0);
    assertEquals(first, match.first());
    assertEquals(second, match.last());
  }

  @Test
  public void testSkippedMatch() throws Exception {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction(this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    Event third = makeEvent(15.0);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    subject.execute((AddEventTrigger) null, first); // shouldn't be added to the window, too old
    assertEquals(1, matches.size());
    Activity match = (Activity) matches.get(0);
    assertEquals(second, match.first());
    assertEquals(third, match.last());
  }


  public Event makeEvent(Double value) {
    return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'value': " + value.toString() + "}"));
  }

  public void execute(EventMatchTrigger trigger, Event event) {
    matches.add(event);
  }
}
