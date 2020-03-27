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
/*
 * JdoEventTest.java
 * JUnit based test
 *
 * Created on May 11, 2007, 8:57 AM
 */

package com.eventswarm.events.jdo;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import com.eventswarm.events.*;
import com.eventswarm.eventset.EventSet;

import java.util.*;

public class JdoActivityTest {
      
  Map<String,EventPart> partsEmpty;
  Event ea, ea1, ea2, ea3, eb1, eb2, eb3;

  @Before
  public void setUp() throws Exception {
      // Various sets of EventParts
      partsEmpty = new HashMap<String,EventPart>();
      ea = new JdoEvent(new JdoHeader(new Date(), 0, new JdoSource("A")), partsEmpty);

              
      // create some other timestamps
      Calendar cal = new GregorianCalendar(1999, 1, 1);
      Date oldTs = cal.getTime();
      Date ts = new Date();
      Date newTs = new Date(ts.getTime()+1);
      
      // various headers with diferent timestamps for comparison
      ea1 = new JdoEvent(new JdoHeader(ts, 0, new JdoSource("A")), partsEmpty);
      ea2 = new JdoEvent(new JdoHeader(ts, 1, new JdoSource("A")), partsEmpty);
      ea3 = new JdoEvent(new JdoHeader(newTs, 0, new JdoSource("A")), partsEmpty);
      eb1 = new JdoEvent(new JdoHeader(oldTs, 0, new JdoSource("B")), partsEmpty);
      eb2 = new JdoEvent(new JdoHeader(ts, 0, new JdoSource("B")), partsEmpty);
      eb3 = new JdoEvent(new JdoHeader(newTs, 0, new JdoSource("B")), partsEmpty);
  }

  @Test
  public void testEventBeforeActivity() {
    Activity subject = makeActivity(ea3, eb3);
    assertTrue(ea1.isBefore(subject));
    assertFalse(ea1.isAfter(subject));
    assertFalse(ea1.isConcurrent(subject));
  }

  @Test
  public void testEventAfterActivity() {
    Activity subject = makeActivity(eb1, ea1);
    assertTrue(eb3.isAfter(subject));
    assertFalse(eb3.isBefore(subject));
    assertFalse(eb3.isConcurrent(subject));
  }

  @Test
  public void testActivityAfterEvent() {
    Activity subject = makeActivity(ea3, eb3);
    assertTrue(subject.isAfter(ea1));
    assertFalse(subject.isBefore(ea1));
    assertFalse(subject.isConcurrent(ea1));
  }

  @Test
  public void testActivityBeforeEvent() {
    Activity subject = makeActivity(eb1, ea1);
    assertTrue(subject.isBefore(eb3));
    assertFalse(subject.isAfter(eb3));
    assertFalse(subject.isConcurrent(eb3));
  }

  @Test
  public void testActivityConcurrentEvent() {
    Activity subject = makeActivity(eb1, ea2);
    assertTrue(subject.isConcurrent(eb2));
    assertFalse(subject.isAfter(eb2));
    assertFalse(subject.isBefore(eb2));
  }

  @Test
  public void testEventConcurrentActivity() {
    Activity subject = makeActivity(eb1, ea2);
    assertTrue(eb2.isConcurrent(subject));
    assertFalse(eb2.isAfter(subject));
    assertFalse(eb2.isBefore(subject));
  }

  @Test
  public void testActivityBefore() {
    Activity first = makeActivity(eb1, ea1);
    Activity second = makeActivity(ea3, eb3);
    assertTrue(first.isBefore(second));
    assertFalse(first.isAfter(second));
    assertFalse(first.isConcurrent(second));
  }

  @Test
  public void testActivityAfter() {
    Activity first = makeActivity(eb1, ea1);
    Activity second = makeActivity(ea3, eb3);
    assertTrue(second.isAfter(first));
    assertFalse(second.isBefore(first));
    assertFalse(second.isConcurrent(first));
  }

  @Test
  public void testActivityConcurrent() {
    Activity a1 = makeActivity(ea1, ea2);
    Activity a2 = makeActivity(eb2, eb3);
    assertFalse(a1.isBefore(a2));
    assertFalse(a2.isBefore(a1));
    assertFalse(a1.isAfter(a2));
    assertFalse(a2.isAfter(a1));
    assertTrue(a1.isConcurrent(a2));
    assertTrue(a2.isConcurrent(a1));
  }

  public Activity makeActivity(Event e1, Event e2) {
    return new JdoActivity(new TreeSet<Event>(Arrays.asList(new Event[] {e1,e2})));
  }
}
