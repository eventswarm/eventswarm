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
package com.eventswarm.abstractions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.NumericValue;
import com.eventswarm.events.jdo.TestEvents;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoEventURL;
import java.util.HashMap;
import java.net.URL;

/**
 *
 * @author zoki
 */
public class StandardDeviationTest {

    static final String NAME = "MyValue";
    static MyPart part1 = new MyPart(1.0);
    static MyPart part2 = new MyPart(2);
    static MyPart part3 = new MyPart(3);
    static MyPart part4 = new MyPart(0);
    static MyPart part5 = new MyPart(-100);
    static JdoEventURL urlPart = new JdoEventURL(null);
    static HashMap<String, EventPart> parts1 = new HashMap<String, EventPart>();
    static HashMap<String, EventPart> parts2 = new HashMap<String, EventPart>();
    static HashMap<String, EventPart> parts3 = new HashMap<String, EventPart>();
    static HashMap<String, EventPart> parts4 = new HashMap<String, EventPart>();
    static HashMap<String, EventPart> parts5 = new HashMap<String, EventPart>();
    static HashMap<String, EventPart> partsURL = new HashMap<String, EventPart>();
    static Event event1, event2, event3, event4, event5, eventURL;

    static {
        parts1.put(NAME, part1);
        parts2.put(NAME, part2);
        parts3.put(NAME, part3);
        parts4.put(NAME, part4);
        parts5.put(NAME, part5);

        partsURL.put(NAME, urlPart);






        event1 = new JdoEvent(TestEvents.headerA1, parts1);
        event2 = new JdoEvent(TestEvents.headerA2, parts2);
        event3 = new JdoEvent(TestEvents.headerA3, parts3);
        event4 = new JdoEvent(TestEvents.headerA2, parts4);
        event5 = new JdoEvent(TestEvents.headerA2, parts5);

        eventURL = new JdoEvent(TestEvents.headerA2, partsURL);


    }

    public StandardDeviationTest() {
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

    @After
    public void tearDown() {
    }

    @Test
    public void testForConstructorDefault() {
        System.out.println("\nTesting Default Constructor");
        StandardDeviation instance = new StandardDeviation(NAME);
        System.out.println("Standard Deviation is " + instance.getValue());
        assertEquals(0.0, instance.getValue());         // method from CalculationAbstraction
        assertEquals(0.0, instance.mean);
        assertEquals(0, instance.n);
    }

    @Test
    public void testForNumericsOnly() {
        System.out.println("\nTesting numerics only");
        StandardDeviation instance = new StandardDeviation(NAME);
        System.out.println("Standard Deviation before is " + instance.getValue());
        instance.calculate(event1);
        instance.calculate(event2);
        System.out.println("Standard Deviation is " + instance.getValue());
        instance.calculate(event3);
        System.out.println("Standard Deviation is " + instance.getValue());
        instance.calculate(event4);

        System.out.println("Current n is: " + instance.n);
        System.out.println("Current Mean is: " + instance.mean);
        System.out.println("Current Variance is: " + instance.currentVariance);
        System.out.println("Current M2 is: " + instance.m2);


        System.out.println("Standard Deviation after is " + instance.getValue());
    }

    @Test
    public void testForAllZeros() {
        System.out.println("\nTesting all zero inputs ");
        StandardDeviation instance = new StandardDeviation(NAME);
        System.out.println("Standard Deviation before is " + instance.getValue());
        instance.calculate(event4);
        instance.calculate(event4);
        instance.calculate(event4);
        instance.calculate(event4);
        System.out.println("Standard Deviation after is " + instance.getValue());
        assertEquals(4, instance.n);
    }

    @Test
    public void testForNonNumericValueEvent() {
        System.out.println("\nTesting for non-numeric (e.g. URL) values");
        StandardDeviation instance = new StandardDeviation(NAME);
        instance.calculate(eventURL);
        System.out.println("Standard Deviation is " + instance.getValue());
    }

    /**
     * Test of reset method, of class StandardDeviation.
     */
    @Test
    public void testResetAtTheEnd() {
        System.out.println("\nTesting for Reset at the End");
        StandardDeviation instance = new StandardDeviation(NAME);
        instance.calculate(event1);
        instance.calculate(event2);
        System.out.println("Standard Deviation is " + instance.getValue());
        instance.calculate(event3);
        System.out.println("Standard Deviation is " + instance.getValue());
        instance.calculate(event4);
        instance.reset();
        assertEquals(0.0, instance.getValue());         // method from CalculationAbstraction
        System.out.println("Standard Deviation is " + instance.getValue());

    }

    @Test
    public void testResetAtMiddle() {
        System.out.println("\nTesting for Reset somewhere in Middle");
        StandardDeviation instance = new StandardDeviation(NAME);
        instance.calculate(event1);
        instance.calculate(event2);
        System.out.println("Standard Deviation is " + instance.getValue());
        instance.reset();
        assertEquals(0.0, instance.getValue());         // method from CalculationAbstraction

        instance.calculate(event3);

        System.out.println("Current n is: " + instance.n);
        System.out.println("Current n is: " + instance.n);
        System.out.println("Current Variance is: " + instance.currentVariance);
        System.out.println("Current M2 is: " + instance.m2);


        System.out.println("Standard Deviation is " + instance.getValue());


        System.out.println("Standard Deviation is " + instance.getValue());
        instance.calculate(event4);
        System.out.println("Standard Deviation is " + instance.getValue());

    }


   @Test
    public void testSingleRemoval () {
        System.out.println("\nTesting for Single Event removal StdDev calculation ");
        StandardDeviation instance = new StandardDeviation(NAME);
        instance.calculate(event1);
        instance.calculate(event2);
        instance.calculate(event3);

        System.out.println("Current n is: " + instance.n);
        System.out.println("Current Mean is: " + instance.mean);
        System.out.println("Current Variance is: " + instance.currentVariance);
        System.out.println("Current M2 is: " + instance.m2);

        System.out.println("Standard Deviation is " + instance.getValue());

        instance.calculateRemove(event1);

        System.out.println("Current n is: " + instance.n);
        System.out.println("Current Mean is: " + instance.mean);
        System.out.println("Current Variance is: " + instance.currentVariance);
        System.out.println("Current M2 is: " + instance.m2);

        System.out.println("Standard Deviation is " + instance.getValue());

        instance.calculateRemove(event2);
        instance.calculateRemove(event3);
        instance.calculateRemove(event2);


        System.out.println("\nCurrent n is: " + instance.n);
        System.out.println("Current Mean is: " + instance.mean);
        System.out.println("Current Variance is: " + instance.currentVariance);
        System.out.println("Current M2 is: " + instance.m2);

        System.out.println("Standard Deviation is " + instance.getValue());

    }




    private static class MyPart implements NumericValue {

        private Number value;

        public MyPart(Number value) {
            this.value = value;
        }

        public Number getValue() {
            return this.value;
        }

        public Event getEvent() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
