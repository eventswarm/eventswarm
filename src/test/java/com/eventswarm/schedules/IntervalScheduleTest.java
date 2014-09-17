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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.schedules;

import com.eventswarm.util.Interval;
import com.eventswarm.util.IntervalUnit;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andyb
 */
public class IntervalScheduleTest {

    public IntervalScheduleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    public static MyClock clock0 = new MyClock(0);
    public static Date date0 = new Date(0);
    public static Date date400 = new Date(400);
    public static Date date1000 = new Date(1000);
    public static Date date1400 = new Date(1400);
    public static Date date2000 = new Date(2000);
    public static Date date2400 = new Date(2400);
    public static Interval interval1s = new Interval(1, IntervalUnit.SECONDS);
    /**
     * Constructor test, all three attributes specified
     */
    @Test
    public void testConstruct3Parms() {
        System.out.println("IntervalSchedule constructor with 3 parameters");
        Interval interval = new Interval(1, IntervalUnit.SECONDS);
        IntervalSchedule instance = new IntervalSchedule(clock0, date0, interval);
        Date next = new Date(1000);
        assertEquals(interval, instance.getInterval());
        assertEquals(date0, instance.getSeed());
        assertEquals(next, instance.next());
    }

    /**
     * Constructor test, all three attributes specified
     */
    @Test
    public void testConstruct2Parms() {
        System.out.println("IntervalSchedule constructor with 2 parameters");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(date0, interval);
        instance.setTime(date0);
        Date next = new Date(1000);
        assertEquals(interval, instance.getInterval());
        assertEquals(date0, instance.getSeed());
        assertEquals(next, instance.next());
    }

    /**
     * Constructor test, all three attributes specified
     */
    @Test
    public void testConstruct1Parm() {
        System.out.println("IntervalSchedule constructor with 1 parameter");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(interval);
        instance.setTime(instance.getSeed());
        Date next = new Date(instance.getSeed().getTime() + interval.getIntervalMillis());
        assertEquals(interval, instance.getInterval());
        assertEquals(next, instance.next());
    }


    /**
     * Constructor test, all three attributes specified
     */
    @Test
    public void testConstructSeedGreaterNotMultiple() {
        System.out.println("IntervalSchedule constructor, seed not multiple of interval, less than one interval greater than start");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(clock0, date400, interval);
        assertEquals(interval, instance.getInterval());
        System.out.println("Next scheduled time is " + Long.toString(instance.next().getTime()));
        assertEquals(date400, instance.next());
    }

    /**
     * Constructor test, all three attributes specified
     */
    @Test
    public void testConstructSeedGreaterNotMultiple2() {
        System.out.println("IntervalSchedule constructor, seed not multiple of interval, more than one interval greater than start");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(clock0, date1400, interval);
        assertEquals(interval, instance.getInterval());
        System.out.println("Next scheduled time is " + Long.toString(instance.next().getTime()));
        assertEquals(date400, instance.next());
    }


    /**
     * Constructor test, all three attributes specified
     */
    @Test
    public void testConstructSeedLessNotMultiple() {
        System.out.println("IntervalSchedule constructor, seed not multiple of interval, less than one interval less than start");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(new MyClock(1000), date400, interval);
        assertEquals(interval, instance.getInterval());
        System.out.println("Next scheduled time is " + Long.toString(instance.next().getTime()));
        assertEquals(date1400, instance.next());
    }


    /**
     * Constructor test, all three attributes specified
     */
    @Test
    public void testConstructSeedLessNotMultiple2() {
        System.out.println("IntervalSchedule constructor, seed not multiple of interval, more than one interval less than start");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(new MyClock(2000), date400, interval);
        assertEquals(interval, instance.getInterval());
        System.out.println("Next scheduled time is " + Long.toString(instance.next().getTime()));
        assertEquals(date2400, instance.next());
    }

    /**
     * setTime, null value, should throw exception
     */
    @Test
    public void testSetTimeNull() {
        System.out.println("setTime null");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(clock0, date0, interval);
        Date next = instance.next();
        try {
            instance.setTime(null);
            fail("Null pointer exception should be thrown");
        } catch (NullPointerException exc) {
            // right place, but check nothing bad has happened
            assertEquals(next, instance.next());
        }
    }


    /**
     * setTime, seed zero, same value, should not affect next
     */
    @Test
    public void testSetTimeSame() {
        System.out.println("setTime same value");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(clock0, date0, interval);
        Date next = instance.next();
        instance.setTime(date0);
        assertEquals(next, instance.next());
    }


    /**
     * setTime, seed zero, exact next value, next should jump one interval forward
     */
    @Test
    public void testSetTimeExactNext() {
        System.out.println("setTime exact next value");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(clock0, date0, interval);
        Date next = instance.next();
        instance.setTime(next);
        assertEquals(next.getTime() + interval.getIntervalMillis(), instance.next().getTime());
    }


    /**
     * setTime, seed zero, less than next value, next should be unchanged
     */
    @Test
    public void testSetTimeLessNext() {
        System.out.println("setTime less than next value");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(clock0, date0, interval);
        Date next = instance.next();
        instance.setTime(new Date(next.getTime() - 1));
        assertEquals(next, instance.next);
    }


    /**
     * setTime, seed zero, greater than next value, next should jump one interval
     */
    @Test
    public void testSetTimeGreaterNext() {
        System.out.println("setTime greater than next value");
        Interval interval = interval1s;
        IntervalSchedule instance = new IntervalSchedule(clock0, date0, interval);
        Date next = instance.next();
        instance.setTime(new Date(next.getTime() + 1));
        assertEquals(next.getTime() + interval.getIntervalMillis(), instance.next().getTime());
    }

    private static class MyClock implements Clock {

        Date time;

        public MyClock(long time) {
            this.time = new Date(time);
        }
        public Object getSource() {
            return MyClock.class;
        }

        public Date getTime() {
            return this.time;
        }

        public boolean isInitialised() {
            return true;
        }

    }
}