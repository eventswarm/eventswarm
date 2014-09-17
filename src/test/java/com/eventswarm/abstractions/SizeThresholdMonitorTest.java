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

import com.eventswarm.*;
import com.eventswarm.events.Event;
import com.eventswarm.abstractions.SizeThresholdMonitor;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.TestEvents;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Created with IntelliJ IDEA.
 * User: zoki
 */
public class SizeThresholdMonitorTest {

    @Before
    public void setUp() {

        event1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmpty);
        event2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmpty);
        event3 = new JdoEvent(TestEvents.headerA3, TestEvents.partsEmpty);
    }

    public static Event event1, event2, event3;

    @Test
    public void testExecuteThresholdReached() throws Exception {
        // Setup of SizeThresholdAction stuff
        SimpleThresholdAction action = new SimpleThresholdAction();  // Action for when SizeThreshold is reached
        SizeThresholdMonitor instance = new SizeThresholdMonitor(3L);           // Setup of SizeThresholdMonitor's Threshold
        instance.registerAction(action);                           // Register SizeThreshold action
        SimpleAddTrigger triggerInstance = new SimpleAddTrigger();

        // Simulate now additions of events
        instance.execute(triggerInstance, event1);
        instance.execute(triggerInstance, event2);
        instance.execute(triggerInstance, event3);

        assertTrue(action.fired);
        assertEquals(instance.getSize(), 3L);
        assertEquals(triggerInstance, instance.getSource());
    }

    @Test
    public void testExecuteThresholdNotReached() throws Exception {
        // Setup of SizeThresholdAction stuff
        SimpleThresholdAction action = new SimpleThresholdAction();  // Action for when SizeThreshold is reached
        SizeThresholdMonitor instance = new SizeThresholdMonitor(3L);  // Setup of SizeThresholdMonitor's Threshold
        instance.registerAction(action);                           // Register SizeThreshold action
        SimpleAddTrigger triggerInstance = new SimpleAddTrigger();

        // Simulate now additions of events
        instance.execute(triggerInstance, event1);
        instance.execute(triggerInstance, event2);

        assertFalse(action.fired);
        assertEquals(instance.getSize(), 2L);
        assertNull(instance.getSource());
    }

    @Test
    public void testExecuteThresholdWithResetThresholdReached () throws Exception {
        // Setup of SizeThresholdAction stuff
        SimpleThresholdAction action = new SimpleThresholdAction();  // Action for when SizeThreshold is reached
        SizeThresholdMonitor instance = new SizeThresholdMonitor(3L, 1L);  // Setup of SizeThresholdMonitor's Threshold
        instance.registerAction(action);                           // Register SizeThreshold action
        SimpleAddTrigger triggerInstance = new SimpleAddTrigger();

        // Simulate now additions of events
        instance.execute(triggerInstance, event1);
        instance.execute(triggerInstance, event2);
        instance.execute(triggerInstance, event3);

        assertTrue(action.fired);
        System.out.println("passed fired test");
        assertEquals(3L, instance.getSize());
        System.out.println("passed size test");
        assertEquals(1L,instance.getResetAt());
        assertEquals(triggerInstance, instance.getSource());
    }

    @Test
    public void testExecuteThresholdWithResetThresholdNotReached () throws Exception {
        // Setup of SizeThresholdAction stuff
        SimpleThresholdAction action = new SimpleThresholdAction();  // Action for when SizeThreshold is reached
        SizeThresholdMonitor instance = new SizeThresholdMonitor(3L, 1L);  // Setup of SizeThresholdMonitor's Threshold
        instance.registerAction(action);                           // Register SizeThreshold action
        SimpleAddTrigger triggerInstance = new SimpleAddTrigger();

        // Simulate now additions of events
        instance.execute(triggerInstance, event1);
        instance.execute(triggerInstance, event2);
        //instance.execute(triggerInstance, event3);

        assertFalse(action.fired);
        System.out.println("passed fired test");
        assertEquals(instance.getSize(), 2L);
        System.out.println("passed size test");
        assertTrue(instance.isEnabled());
        System.out.println("passed enabled flag test");
        assertNull(instance.getSource());
    }


    /**
     * Test of SizeThresholdAction registration.
     */
    @Test
    public void testRegisterAction() throws Exception {
        System.out.println("registerAction");
        SizeThresholdAction action = new SimpleThresholdAction();
        SizeThresholdMonitor instance = new SizeThresholdMonitor(5L, 7L);
        instance.registerAction(action);
        System.out.println("registerAction - success");
    }


    @Test
    public void testUnregisterAction() throws Exception {
        System.out.println("unregesterAction");
        SizeThresholdAction action = new SimpleThresholdAction();
        SizeThresholdMonitor instance = new SizeThresholdMonitor(5L);
        instance.registerAction(action);
        instance.unregisterAction(action);
        System.out.println("unregisterAction - success");

    }

    /**
     * Test of SizeThresholdMonitor constructor.
     */

    @Test
    public void testForConstructorDefault() throws Exception {
        System.out.println("constructor test");
        SizeThresholdMonitor instance = new SizeThresholdMonitor(5L);
        System.out.println("unregisterAction - success");
        System.out.println("threshold is " + instance.getThreshold());
        System.out.println("size is " + instance.getSize());

    }


    class SimpleThresholdAction implements SizeThresholdAction {
        public boolean fired = false;
        public long size;
        public void execute(SizeThresholdTrigger trigger, Event event, long size) {
            this.fired = true;
            this.size = size;
        }
    }

    class SimpleAddEventAction implements AddEventAction {
        public void execute(AddEventTrigger trigger, Event event) {
            System.out.println("new event added" + event) ;
        }
    }
    class SimpleAddTrigger implements AddEventTrigger {
        @Override
        public void registerAction(AddEventAction action) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void unregisterAction(AddEventAction action) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
