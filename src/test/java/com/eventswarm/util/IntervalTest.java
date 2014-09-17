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

package com.eventswarm.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andyb
 */
public class IntervalTest {

    public IntervalTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test interval specified in seconds
     */
    @Test
    public void testIntervalSeconds() {
        System.out.println("Interval specified in seconds");
        Interval instance = new Interval(5, IntervalUnit.SECONDS);
        long expResult = 5L * 1000L;
        long result = instance.getIntervalMillis();
        assertEquals(expResult, result);
        assertEquals(5L, instance.getInterval());
        assertEquals(IntervalUnit.SECONDS, instance.getUnit());
    }


    /**
     * Test interval specified in minutes
     */
    @Test
    public void testGetIntervalMinutes() {
        System.out.println("Interval specified in minutes");
        Interval instance = new Interval(5, IntervalUnit.MINUTES);
        long expResult = 5L * 60L * 1000L;
        long result = instance.getIntervalMillis();
        assertEquals(expResult, result);
        assertEquals(5L, instance.getInterval());
        assertEquals(IntervalUnit.MINUTES, instance.getUnit());
    }


    /**
     * Test interval specified in hours
     */
    @Test
    public void testGetIntervalHours() {
        System.out.println("Interval specified in hours");
        Interval instance = new Interval(5, IntervalUnit.HOURS);
        long expResult = 5L * 60L * 60L * 1000L;
        long result = instance.getIntervalMillis();
        assertEquals(expResult, result);
        assertEquals(5L, instance.getInterval());
        assertEquals(IntervalUnit.HOURS, instance.getUnit());
    }


    /**
     * Test interval specified in days
     */
    @Test
    public void testGetIntervalDays() {
        System.out.println("Interval specified in hours");
        Interval instance = new Interval(5, IntervalUnit.DAYS);
        long expResult = 5L * 24L * 60L * 60L * 1000L;
        long result = instance.getIntervalMillis();
        assertEquals(expResult, result);
        assertEquals(5L, instance.getInterval());
        assertEquals(IntervalUnit.DAYS, instance.getUnit());
    }


    /**
     * Test interval specified in days
     */
    @Test
    public void testGetIntervalWeeks() {
        System.out.println("Interval specified in hours");
        Interval instance = new Interval(5, IntervalUnit.WEEKS);
        long expResult = 5L * 7L * 24L * 60L * 60L * 1000L;
        long result = instance.getIntervalMillis();
        assertEquals(expResult, result);
        assertEquals(5L, instance.getInterval());
        assertEquals(IntervalUnit.WEEKS, instance.getUnit());
    }

    
    /**
     * Test equals, same object
     */
    @Test
    public void testEqualsIdentity() {
        System.out.println("equals, identity test (same object)");
        Interval instance = new Interval(5, IntervalUnit.SECONDS);
        assertTrue(instance.equals(instance));
    }


    /**
     * Test equals, same parameters, different objects
     */
    @Test
    public void testEqualsSameParms() {
        System.out.println("equals, same parameters different objects");
        Interval instance1 = new Interval(5, IntervalUnit.SECONDS);
        Interval instance2 = new Interval(5, IntervalUnit.SECONDS);
        assertTrue(instance1.equals(instance2));
        assertTrue(instance2.equals(instance1));
    }

    /**
     * Test equals, different parameters, different objects
     */
    @Test
    public void testEqualsDiffParms() {
        System.out.println("equals, different parameters different objects");
        Interval instance1 = new Interval(120, IntervalUnit.SECONDS);
        Interval instance2 = new Interval(2, IntervalUnit.MINUTES);
        assertTrue(instance1.equals(instance2));
        assertTrue(instance2.equals(instance1));
    }


    /**
     * Test equals, not interval object
     */
    @Test
    public void testEqualsNonInterval() {
        System.out.println("equals, not interval object");
        Interval instance1 = new Interval(120, IntervalUnit.SECONDS);
        Object instance2 = new Object();
        assertFalse(instance1.equals(instance2));
    }


    /**
     * Test hashcode, value less than Integer.MAX_VALUE
     */
    @Test
    public void testHashcodeSmall() {
        System.out.println("hashcode, small value");
        Interval instance = new Interval(5, IntervalUnit.SECONDS);
        int code = instance.hashCode();
        int result = 5000;
        assertEquals(result, code);
    }


    /**
     * Test hashcode, value less than Integer.MAX_VALUE
     */
    @Test
    public void testHashcodeLarge() {
        System.out.println("hashcode, large value");
        Interval instance = new Interval(5, IntervalUnit.WEEKS);
        int code = instance.hashCode();
        int result = (int) (instance.getIntervalMillis() % (long) Integer.MAX_VALUE);
        assertEquals(result, code);
    }


    /**
     * Test hashcode same for two equal values
     */
    @Test
    public void testHashcodeEqual() {
        System.out.println("hashcode, same for two equal values");
        Interval instance1 = new Interval(120, IntervalUnit.SECONDS);
        Interval instance2 = new Interval(2, IntervalUnit.MINUTES);
        int code1 = instance1.hashCode();
        int code2 = instance2.hashCode();
        assertEquals(code1, code2);
    }

}