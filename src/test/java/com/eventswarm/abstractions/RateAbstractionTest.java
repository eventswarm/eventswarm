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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.RateEvent;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.util.Interval;
import com.eventswarm.util.IntervalUnit;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class RateAbstractionTest {
    static final Interval ONE_SECOND = new Interval(1, IntervalUnit.SECONDS);
    static final Map<String, EventPart> parts = new HashMap<String,EventPart>();
    static final AddEventTrigger trigger = null;
    ArrayList<Event> rateEvents;
    AddEventAction catcher;

    @Before
    public void setup() throws Exception {
        rateEvents = new ArrayList<Event>();
        catcher = new AddEventAction() {
            public void execute(AddEventTrigger trigger, Event event) {
                rateEvents.add(event);
            }
        };
    }

    @Test
    public void testGetRateOne() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 10);
        Thread.sleep(1000); // make sure a full window of time has passed
        instance.execute(trigger, makeEvent());
        Thread.sleep(120);
        assertEquals(1.0 / 1.0, instance.getRate(), 0.0);
        instance.stop();
    }

    @Test
    public void testGetRateDouble() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 10);
        Thread.sleep(1000); // make sure a full window of time has passed
        instance.execute(trigger, makeEvent());
        instance.execute(trigger, makeEvent());
        Thread.sleep(120);
        assertEquals(2.0/1.0, instance.getRate(), 0.0);
        instance.stop();
    }

    @Test
    public void testGetRateAfterDiscard() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 10);
        instance.execute(trigger, makeEvent());
        Thread.sleep(1000);
        instance.execute(trigger, makeEvent());
        Thread.sleep(200);
        assertEquals(1.0 / 1.0, instance.getRate(), 0.0);
        instance.stop();
    }

    @Test
    public void testGetValue() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 10);
        Thread.sleep(1000); // make sure a full window of time has passed
        instance.execute(trigger, makeEvent());
        Thread.sleep(120);
        assertEquals(1.0 / 1.0, instance.getValue());
        instance.stop();
    }

    @Test
    public void testFirstExecute() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 1);
        instance.registerAction(catcher);
        Thread.sleep(950); // make sure most of the window of time has passed
        instance.execute(trigger, makeEvent());
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        assertEquals(1, rateEvents.size());
        assertEquals(2.0/1.0, instance.getRate(), 0.0);
        assertEquals(2.0/1.0, ((RateEvent) rateEvents.get(0)).getRate(), 0.0);
        instance.unregisterAction(catcher);
        instance.stop();
    }

    @Test
    public void testSecondExecute() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 1);
        instance.registerAction(catcher);
        Thread.sleep(950); // make sure a full window of time has almost passed
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        assertEquals(2, rateEvents.size());
        assertEquals(2.0 /1.0, instance.getRate(), 0.0);
        assertEquals(1.0/1.0, ((RateEvent) rateEvents.get(0)).getRate(), 0.0);
        assertEquals(2.0/1.0, ((RateEvent) rateEvents.get(1)).getRate(), 0.0);
        instance.unregisterAction(catcher);
        instance.stop();
    }

    @Test
    public void testHigherThresholdExecute() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 2);
        instance.registerAction(catcher);
        Thread.sleep(950); // make sure a full window of time has almost passed
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        assertEquals(1, rateEvents.size());
        assertEquals(2.0/1.0, instance.getRate(), 0.0);
        assertEquals(2.0/1.0, ((RateEvent) rateEvents.get(0)).getRate(), 0.0);
        instance.unregisterAction(catcher);
        instance.stop();
    }

    @Test
    public void testNoThresholdExecute() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 0);
        instance.registerAction(catcher);
        Thread.sleep(950);
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        assertEquals(0, rateEvents.size());
        instance.unregisterAction(catcher);
        instance.stop();
    }

    /**
     * Not really a unit test, but we expect this abstraction to be used regularly with the statistics abstraction
     * to maintain stats on the rate.
     *
     * @throws Exception
     */
    @Test
    public void testRateStatistics() throws Exception {
        RateAbstraction instance = new RateAbstraction(ONE_SECOND, 100, 1);
        StatisticsAbstraction stats = new StatisticsAbstraction(RateEvent.RATE_NUMBER_RETRIEVER);
        instance.registerAction(stats);
        Thread.sleep(950); // make sure a full window of time has almost passed
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        instance.execute(trigger, makeEvent());
        Thread.sleep(100);
        assertEquals(2, stats.getCount());
        assertEquals(1.5, stats.getMean(), 0.0);
        instance.unregisterAction(catcher);
        instance.stop();
    }

    public Event makeEvent() {
        return new JdoEvent(JdoHeader.getLocalHeader(), parts);
    }
}
