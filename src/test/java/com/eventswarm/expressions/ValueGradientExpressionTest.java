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
import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ValueGradientExpressionTest implements EventMatchAction, ComplexExpressionMatchAction {
  ValueRetriever<Double> retriever = new JsonEvent.DoubleRetriever("value");
  ArrayList<Event> matches = new ArrayList<Event>();
  ArrayList<ComplexExpressionMatchEvent> complexMatches = new ArrayList<ComplexExpressionMatchEvent>();
  
  @Test
  public void testConstruct() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, -1);
    assertNotNull(subject);
  }


  @Test
  public void testNotEnough() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, -1);
    subject.registerAction((EventMatchAction) this);
    Event first = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, first);
    assertEquals(0, matches.size());
  }

  @Test
  public void testMatchDown() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, -1);
    subject.registerAction((EventMatchAction) this);
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
    subject.registerAction((EventMatchAction) this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(0, matches.size());
  }


  @Test
  public void testMatchUp() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction((EventMatchAction) this);
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
    subject.registerAction((EventMatchAction) this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(0.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(0, matches.size());
  }

  @Test
  public void testMatchFlat() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 0);
    subject.registerAction((EventMatchAction) this);
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
    subject.registerAction((EventMatchAction) this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    assertEquals(0, matches.size());
  }

  @Test
  public void testNextMatch() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction((EventMatchAction) this);
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
    subject.registerAction((EventMatchAction) this);
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
    subject.registerAction((EventMatchAction) this);
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
  public void testIsTrueFull() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction((EventMatchAction) this);
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
  public void testTrueFull() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction((EventMatchAction) this);
    Event first = makeEvent(5.0);
    Event second = makeEvent(10.0);
    Event third = makeEvent(5.0);
    subject.execute((AddEventTrigger) null, first);
    subject.execute((AddEventTrigger) null, second);
    subject.execute((AddEventTrigger) null, third);
    assertEquals(1, matches.size());
    assertTrue(subject.isTrue());
  }

  @Test
  public void testIsTrueNotFull() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction((EventMatchAction) this);
    Event first = makeEvent(5.0);
    subject.execute((AddEventTrigger) null, first);
    assertFalse(subject.isTrue());
  }

  @Test
  public void testHasMatched() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    subject.registerAction((EventMatchAction) this);
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
    subject.registerAction((EventMatchAction) this);
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
    subject.registerAction((EventMatchAction) this);
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

  @Test
  public void testGradientInAnd() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    EventExpression[] seq = {subject, new TrueEventExpression()};
    ANDExpression and = new ANDExpression(Arrays.asList(seq));
    and.registerAction((ComplexExpressionMatchAction) this);
    and.registerAction((EventMatchAction) this);
    Event first = makeEvent(0.0);
    Event second = makeEvent(5.0);
    and.execute((AddEventTrigger) null, first);
    and.execute((AddEventTrigger) null, second);
    assertTrue(and.isTrue());
    assertEquals(1, complexMatches.size());
    assertEquals(1, matches.size());
    assertEquals(second, matches.get(0));
    Activity gradient = (Activity) complexMatches.get(0).getEvents().first();
    assertEquals(first, gradient.first());
    assertEquals(second, gradient.last());
  }


  @Test
  public void testNoGradientInAnd() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    EventExpression[] seq = {subject, new TrueEventExpression()};
    ANDExpression and = new ANDExpression(Arrays.asList(seq));
    and.registerAction((ComplexExpressionMatchAction) this);
    and.registerAction((EventMatchAction) this);
    Event first = makeEvent(0.0);
    and.execute((AddEventTrigger) null, first);
    assertFalse(and.isTrue());
    assertEquals(0, complexMatches.size());
    assertEquals(0, matches.size());
  }

  @Test
  public void testGradientInSequenceOverlap() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    EventExpression[] seq = {subject, new TrueEventExpression()};
    SequenceExpression sequence = new SequenceExpression(Arrays.asList(seq));
    sequence.registerAction((ComplexExpressionMatchAction) this);
    sequence.registerAction((EventMatchAction) this);
    Event first = makeEvent(0.0);
    Event second = makeEvent(5.0);
    Event third = makeEvent(10.0);
    sequence.execute((AddEventTrigger) null, first);
    sequence.execute((AddEventTrigger) null, second);
    sequence.execute((AddEventTrigger) null, third);
    assertTrue(sequence.isTrue());
    assertEquals(1, complexMatches.size());
    assertEquals(1, matches.size());
    assertEquals(third, matches.get(0));
    Activity gradient = (Activity) complexMatches.get(0).getEvents().first();
    assertEquals(first, gradient.first());
    assertEquals(second, gradient.last());
  }


  @Test
  public void testGradientInSequenceDistinct() {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    EventExpression[] seq = {subject, new TrueEventExpression()};
    SequenceExpression sequence = new SequenceExpression(Arrays.asList(seq));
    sequence.registerAction((ComplexExpressionMatchAction) this);
    sequence.registerAction((EventMatchAction) this);
    Event first = makeEvent(0.0);
    Event second = makeEvent(5.0);
    Event third = makeEvent(0.0);
    sequence.execute((AddEventTrigger) null, first);
    sequence.execute((AddEventTrigger) null, second);
    sequence.execute((AddEventTrigger) null, third);
    assertTrue(sequence.isTrue());
    assertEquals(1, complexMatches.size());
    assertEquals(1, matches.size());
    assertEquals(third, matches.get(0));
    Activity gradient = (Activity) complexMatches.get(0).getEvents().first();
    assertEquals(first, gradient.first());
    assertEquals(second, gradient.last());
  }

  @Test
  public void testGradientInSequenceFiltered() throws Exception {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    EventExpression[] seq = {subject, new TrueEventExpression()};
    SequenceExpression sequence = new SequenceExpression(Arrays.asList(seq));
    sequence.registerAction((ComplexExpressionMatchAction) this);
    sequence.registerAction((EventMatchAction) this);
    Event first = makeEvent(0.0);
    Event second = makeEvent(5.0);
    Event third = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{}")); // event that won't match gradient
    sequence.execute((AddEventTrigger) null, first);
    sequence.execute((AddEventTrigger) null, second);
    sequence.execute((AddEventTrigger) null, third);
    assertTrue(sequence.isTrue());
    assertEquals(1, complexMatches.size());
    assertEquals(1, matches.size());
    assertEquals(third, matches.get(0));
    Activity gradient = (Activity) complexMatches.get(0).getEvents().first();
    assertEquals(first, gradient.first());
    assertEquals(second, gradient.last());
  }


  @Test
  public void testGradientInSequenceWithIntervening() throws Exception {
    ValueGradientExpression<Double> subject = new ValueGradientExpression<Double>(2, retriever, 1);
    EventExpression[] seq = {subject, new TrueEventExpression()};
    SequenceExpression sequence = new SequenceExpression(Arrays.asList(seq));
    sequence.registerAction((ComplexExpressionMatchAction) this);
    sequence.registerAction((EventMatchAction) this);
    Event first = makeEvent(0.0);
    Event second = makeEvent(5.0);
    Event third = makeEvent(0.0);
    Event fourth = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{}")); // event that won't match gradient
    sequence.execute((AddEventTrigger) null, first);
    sequence.execute((AddEventTrigger) null, second);
    sequence.execute((AddEventTrigger) null, third);
    sequence.execute((AddEventTrigger) null, fourth);
    assertTrue(sequence.isTrue());
    assertEquals(2, complexMatches.size());
    assertEquals(2, matches.size());
    assertEquals(third, matches.get(0));
    assertEquals(fourth, matches.get(1));
    Activity gradient = (Activity) complexMatches.get(0).getEvents().first();
    assertEquals(first, gradient.first());
    assertEquals(second, gradient.last());
    gradient = (Activity) complexMatches.get(1).getEvents().first();
    assertEquals(first, gradient.first());
    assertEquals(second, gradient.last());
  }

  public Event makeEvent(Double value) {
    return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'value': " + value.toString() + "}"));
  }

  public void execute(EventMatchTrigger trigger, Event event) {
    matches.add(event);
  }

  public void execute(ComplexExpressionMatchTrigger trigger, ComplexExpressionMatchEvent event) {
    complexMatches.add(event);
  }
}
