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
package com.eventswarm.schedules;

import com.eventswarm.util.Interval;
import com.eventswarm.util.IntervalUnit;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SystemClockTickTriggerTest implements TickAction {
    long started;
    ArrayList<Date> ticks = new ArrayList<Date>();

    // This is the usual delay between getting the current time and initialising the Timer
    // Might need to adjust depending on where the test is being run.
    // Only affects first-tick
    static int START_DELAY = 33;

    @Test
    public void first_tick_default() throws Exception {
        started = new Date().getTime();
        long period = 1000;
        System.out.println("Start timestamp: " + Long.toString(started));
        started = new Date().getTime();
        SystemClockTickTrigger instance = new SystemClockTickTrigger();
        instance.registerAction(this);
        synchronized (ticks) {
            ticks.wait();
            assertEquals(1, ticks.size());
            // add an estimated start delay to the expected elapsed time
            assertTrue(hasElapsed(ticks.get(0).getTime(), period + START_DELAY));
        }
        instance.stop();
    }

    @Test
    public void two_ticks_default() throws Exception {
        started = new Date().getTime();
        long period = 1000;
        System.out.println("Start timestamp: " + Long.toString(started));
        SystemClockTickTrigger instance = new SystemClockTickTrigger();
        instance.registerAction(this);
        synchronized (ticks) {
            ticks.wait();
            ticks.wait();
            assertEquals(2, ticks.size());
            // add actual start delay to the expected elapsed time
            assertTrue(hasElapsed(ticks.get(1).getTime(), 2*period + start_delay(period)));
        }
        instance.stop();
    }

    @Test
    public void first_tick_millis() throws Exception {
        started = new Date().getTime();
        long period = 1000;
        System.out.println("Start timestamp: " + Long.toString(started));
        SystemClockTickTrigger instance = new SystemClockTickTrigger(period);
        instance.registerAction(this);
        synchronized (ticks) {
            ticks.wait();
            assertEquals(1, ticks.size());
            // add an estimated start delay to the expected elapsed time
            assertTrue(hasElapsed(ticks.get(0).getTime(), period + START_DELAY));
        }
        instance.stop();
    }

    @Test
    public void two_ticks_millis() throws Exception {
        started = new Date().getTime();
        long period = 1000;
        System.out.println("Start timestamp: " + Long.toString(started));
        SystemClockTickTrigger instance = new SystemClockTickTrigger(period);
        instance.registerAction(this);
        synchronized (ticks) {
            ticks.wait();
            ticks.wait();
            assertEquals(2, ticks.size());
            // add actual start delay to the expected elapsed time
            assertTrue(hasElapsed(ticks.get(1).getTime(), 2*period + start_delay(period)));
        }
        instance.stop();
    }


    @Test
    public void first_tick_interval() throws Exception {
        started = new Date().getTime();
        Interval interval = new Interval(1L, IntervalUnit.SECONDS);
        long period = interval.getIntervalMillis();
        System.out.println("Start timestamp: " + Long.toString(started));
        SystemClockTickTrigger instance = new SystemClockTickTrigger(interval);
        instance.registerAction(this);
        synchronized (ticks) {
            ticks.wait();
            assertEquals(1, ticks.size());
            // add an estimated start delay to the expected elapsed time
            assertTrue(hasElapsed(ticks.get(0).getTime(), period + START_DELAY));
        }
        instance.stop();
    }

    @Test
    public void two_ticks_interval() throws Exception {
        started = new Date().getTime();
        Interval interval = new Interval(1L, IntervalUnit.SECONDS);
        long period = interval.getIntervalMillis();
        System.out.println("Start timestamp: " + Long.toString(started));
        SystemClockTickTrigger instance = new SystemClockTickTrigger(interval);
        instance.registerAction(this);
        synchronized (ticks) {
            ticks.wait();
            ticks.wait();
            assertEquals(2, ticks.size());
            // add actual start delay to the expected elapsed time
            assertTrue(hasElapsed(ticks.get(1).getTime(), 2*period + start_delay(period)));
        }
        instance.stop();
    }

    public void execute(TickTrigger trigger, Date time) {
        synchronized (ticks) {
            ticks.add(time);
            ticks.notify();
        }
    }

    /**
     * Implement a fuzzy comparison with the start time to ensure we don't get caught out with processing delays,
     * allowing 3 ms of delay
     *
     * @param timestamp timestamp of time to be compared
     * @param expected expected elapsed time in milliseconds
     * @return
     */
    public boolean hasElapsed(long timestamp, long expected) {
        System.out.println("Finish timestamp: " + Long.toString(timestamp));
        long actual = timestamp - started;
        System.out.println("Expected elapsed time: " + Long.toString(expected) + ", actual elapsed time: " + Long.toString(actual));
        return (Math.abs(actual - expected) - START_DELAY < 3);
    }

    /**
     * Method to work out the original start delay so we have some accuracy in subsequent comparisons. Only works after
     * at least one tick.
     *
     * @return
     */
    public long start_delay(long period) {
        return (ticks.get(0).getTime() - started - period);
    }
}
