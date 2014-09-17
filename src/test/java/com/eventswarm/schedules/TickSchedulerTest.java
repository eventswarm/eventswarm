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

import java.util.Date;
import java.util.HashSet;
import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andyb
 */
public class TickSchedulerTest implements ScheduleAction {

    private HashMap<Schedule, HashSet<Date>> fired = new HashMap<Schedule, HashSet<Date>>();

    public void execute(ScheduleTrigger trigger, Schedule schedule, Date time) {
        if (!fired.containsKey(schedule)) {
            fired.put(schedule, new HashSet<Date>());
        }
        fired.get(schedule).add(time);
    }

    public TickSchedulerTest() {
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

    /**
     * Test for registering a null schedule. Should throw exception
     */
    @Test
    public void testRegisterNullSchedule() {
        try {
            System.out.println("register null schedule");
            TickScheduler instance = new TickScheduler();
            instance.registerAction(null, this);
            fail("Null schedule should cause an exception");
        } catch (NullPointerException exc) {
            // success
        }
    }

    /**
     * Test for registering a null action, should be ignored.
     */
    @Test
    public void testRegisterNullAction() {
        try {
            System.out.println("register null action");
            Schedule schedule = new PlusSchedule(1000);
            TickScheduler instance = new TickScheduler();
            instance.registerAction(schedule, null);
            fail("Null action should cause an exception");
        } catch (NullPointerException exc) {
            // success
        }
    }

    /**
     * Test a basic registration 
     */
    @Test
    public void testRegisterAction() {
        System.out.println("register first action");
        Schedule schedule = new PlusSchedule(1000);
        ScheduleAction action = this;
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule, action);
        assertTrue(instance.getQueue().contains(schedule));
        assertTrue(instance.getActions().get(schedule).contains(this));
    }

    /**
     * Test two actions, one schedule
     */
    @Test
    public void testRegister2Actions1Schedule() {
        System.out.println("register 2 actions 1 schedule");
        Schedule schedule = new PlusSchedule(1000);
        ScheduleAction action1 = new SimpleScheduleAction();
        ScheduleAction action2 = new SimpleScheduleAction();
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule, action1);
        instance.registerAction(schedule, action2);
        assertTrue(instance.getQueue().contains(schedule));
        assertTrue(instance.getActions().get(schedule).contains(action1));
        assertTrue(instance.getActions().get(schedule).contains(action2));
    }

    /**
     * Test two actions, two schedules
     */
    @Test
    public void testRegister2Actions2Schedules() {
        System.out.println("register 2 actions two schedules");
        Schedule schedule1 = new PlusSchedule(1000);
        Schedule schedule2 = new PlusSchedule(500);
        ScheduleAction action1 = new SimpleScheduleAction();
        ScheduleAction action2 = new SimpleScheduleAction();
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule1, action1);
        instance.registerAction(schedule2, action2);
        assertTrue(instance.getQueue().contains(schedule1));
        assertTrue(instance.getQueue().contains(schedule2));
        assertEquals(instance.getQueue().first(), schedule2);
        assertEquals(instance.getActions().get(schedule1).size(), 1);
        assertTrue(instance.getActions().get(schedule1).contains(action1));
        assertEquals(instance.getActions().get(schedule2).size(), 1);
        assertTrue(instance.getActions().get(schedule2).contains(action2));
    }

    /**
     * Test unregister of null schedule, empty scheduler, should do nothing
     */
    @Test
    public void testUnregisterNullSchedule() {
        System.out.println("unregister null schedule, no schedules");
        ScheduleAction action = this;
        TickScheduler instance = new TickScheduler();
        instance.unregisterAction(null, action);
    }


    /**
     * Test unregister of null schedule, other schedules exist
     */
    @Test
    public void testUnregisterNullScheduleOthersExist() {
        System.out.println("unregister null schedule, other schedules exist");
        Schedule schedule = new PlusSchedule(1000);
        ScheduleAction action = this;
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule, action);
        instance.unregisterAction(null, action);
        assertTrue(instance.getQueue().contains(schedule));
        assertTrue(instance.getActions().get(schedule).contains(action));
    }

    /**
     * Test unregister of null action, empty scheduler, should do nothing
     */
    @Test
    public void testUnregisterNullAction() {
        System.out.println("unregister null action, no schedules or actions");
        Schedule schedule = new PlusSchedule(1000);
        TickScheduler instance = new TickScheduler();
        instance.unregisterAction(schedule, null);
    }


    /**
     * Test unregister of null action, scheduler contains schedule
     */
    @Test
    public void testUnregisterNullActionScheduleExists() {
        System.out.println("Unregister null action, existing schedule with actions");
        Schedule schedule = new PlusSchedule(1000);
        ScheduleAction action = this;
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule, action);
        instance.unregisterAction(schedule, null);
        assertTrue(instance.getQueue().contains(schedule));
        assertTrue(instance.getActions().get(schedule).contains(action));
    }
    /**
     * Test unregister of single registered action
     */
    @Test
    public void testUnregisterOnlyAction() {
        System.out.println("unregister only action");
        Schedule schedule = new PlusSchedule(1000);
        ScheduleAction action = this;
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule, action);
        instance.unregisterAction(schedule, action);
        assertFalse(instance.getQueue().contains(schedule));
        assertFalse(instance.getActions().containsKey(schedule));
    }

    /**
     * Test unregister action from schedule with two actions. Should remove
     * action but not schedule
     */
    @Test
    public void testUnregisterActionNotSchedule() {
        System.out.println("Unregister action from schedule with 2 actions");
        Schedule schedule = new PlusSchedule(1000);
        ScheduleAction action1 = new SimpleScheduleAction();
        ScheduleAction action2 = new SimpleScheduleAction();
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule, action1);
        instance.registerAction(schedule, action2);
        instance.unregisterAction(schedule, action1);
        assertTrue(instance.getQueue().contains(schedule));
        assertFalse(instance.getActions().get(schedule).contains(action1));
        assertTrue(instance.getActions().get(schedule).contains(action2));
    }

    /**
     * Test unregister action from schedule with 1 action, leaving other
     * schedule untouched. Should remove action and associated schedule
     */
    @Test
    public void testUnregisterActionAndSchedule() {
        System.out.println("Unregister action and associated schedule, leaving others");
        Schedule schedule1 = new PlusSchedule(1000);
        Schedule schedule2 = new PlusSchedule(500);
        ScheduleAction action1 = new SimpleScheduleAction();
        ScheduleAction action2 = new SimpleScheduleAction();
        TickScheduler instance = new TickScheduler();
        instance.registerAction(schedule1, action1);
        instance.registerAction(schedule2, action2);
        instance.unregisterAction(schedule1, action1);
        assertFalse(instance.getQueue().contains(schedule1));
        assertTrue(instance.getQueue().contains(schedule2));
        assertFalse(instance.getActions().containsKey(schedule1));
        assertEquals(instance.getActions().get(schedule2).size(), 1);
        assertTrue(instance.getActions().get(schedule2).contains(action2));
    }

    /**
     * Test execute, null time, should throw exception
     */
    @Test
    public void testExecuteNullTime() {
        try {
        System.out.println("Update with null time");
        Date time = null;
        TickScheduler instance = new TickScheduler();
        instance.execute(null, null);
        fail("Should throw null pointer exception");
        } catch (NullPointerException exc) {
            // success
        }
    }

    /**
     * Test execute, new time > old time, no actions
     */
    @Test
    public void testExecuteTimeGreater() {
        System.out.println("execute time greater, no actions");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Date newTime = new Date(1);
        trig.tick(newTime);
        assertEquals(instance.getTime(), newTime);
    }


    /**
     * Test execute, new time < old time, no actions
     */
    @Test
    public void testExecuteTimeLess() {
        System.out.println("execute time less, no actions");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Date oldTime = new Date(5);
        Date newTime = new Date(1);
        trig.tick(oldTime);
        trig.tick(newTime);
        assertEquals(instance.getTime(), oldTime);
    }


    /**
     * Test execute, new time = old time, no actions
     */
    @Test
    public void testExecuteTimeEqual() {
        System.out.println("execute time equals, no actions");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Date oldTime = new Date(1);
        Date newTime = new Date(1);
        trig.tick(newTime);
        assertEquals(instance.getTime(), oldTime);
    }


    /**
     * Test execute, actions scheduled, schedule not passed
     */
    @Test
    public void testExecuteScheduleNotPassed() {
        System.out.println("execute, schedule not passed");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Schedule schedule = new PlusSchedule(1000);
        SimpleScheduleAction action = new SimpleScheduleAction();
        instance.registerAction(schedule, action);
        Date newTime = new Date(999);
        trig.tick(newTime);
        assertEquals(instance.getTime(), newTime);
        assertEquals(schedule.next(), new Date(1000));
        assertTrue(action.actions.isEmpty());
    }


    /**
     * Test execute, actions scheduled, schedule passed
     */
    @Test
    public void testExecuteSchedulePassed() {
        System.out.println("execute, schedule passed");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Schedule schedule = new PlusSchedule(1000);
        SimpleScheduleAction action = new SimpleScheduleAction();
        instance.registerAction(schedule, action);
        Date newTime = new Date(1001);
        trig.tick(newTime);
        assertEquals(instance.getTime(), newTime);
        assertEquals(schedule.next(), new Date(2000));
        assertTrue(action.actions.contains(new Date(1000)));
    }


    /**
     * Test execute, action scheduled, schedule time equalled
     */
    @Test
    public void testExecuteScheduleEqualled() {
        System.out.println("execute, schedule equalled");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Schedule schedule = new PlusSchedule(1000);
        SimpleScheduleAction action = new SimpleScheduleAction();
        instance.registerAction(schedule, action);
        Date newTime = new Date(1000);
        trig.tick(newTime);
        assertEquals(instance.getTime(), newTime);
        assertEquals(schedule.next(), new Date(2000));
        assertTrue(action.actions.contains(new Date(1000)));
    }


    /**
     * Test execute, action scheduled, multiple passes of schedule
     */
    @Test
    public void testExecuteSchedulePassedMultiple() {
        System.out.println("execute, multiple passes of schedule");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Schedule schedule = new PlusSchedule(1000);
        SimpleScheduleAction action = new SimpleScheduleAction();
        instance.registerAction(schedule, action);
        Date newTime = new Date(2001);
        trig.tick(newTime);
        assertEquals(instance.getTime(), newTime);
        assertEquals(schedule.next(), new Date(3000));
        assertEquals(action.actions.size(), 2);
        assertTrue(action.actions.contains(new Date(1000)));
        assertTrue(action.actions.contains(new Date(2000)));
    }


    /**
     * Test execute, actions scheduled, schedule passed
     */
    @Test
    public void testExecute2ActionsSchedulePassed() {
        System.out.println("execute, 2 actions, schedule passed");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Schedule schedule = new PlusSchedule(1000);
        SimpleScheduleAction action1 = new SimpleScheduleAction();
        SimpleScheduleAction action2 = new SimpleScheduleAction();
        instance.registerAction(schedule, action1);
        instance.registerAction(schedule, action2);
        Date newTime = new Date(1001);
        trig.tick(newTime);
        assertEquals(instance.getTime(), newTime);
        assertEquals(schedule.next(), new Date(2000));
        assertEquals(action1.actions.size(), 1);
        assertTrue(action1.actions.contains(new Date(1000)));
        assertEquals(action2.actions.size(), 1);
        assertTrue(action2.actions.contains(new Date(1000)));
    }

    /**
     * Test execute, multiple schedules with actions, one schedule passed
     */
    @Test
    public void testExecuteMultiScheduleOnePassed() {
        System.out.println("execute, multi-schedule, one schedule passed");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Schedule schedule1 = new PlusSchedule(1000);
        Schedule schedule2 = new PlusSchedule(500);
        SimpleScheduleAction action1 = new SimpleScheduleAction();
        SimpleScheduleAction action2 = new SimpleScheduleAction();
        instance.registerAction(schedule1, action1);
        instance.registerAction(schedule2, action2);
        Date newTime = new Date(999);
        trig.tick(newTime);
        Date nextExpected = new Date(1000);
        Date passed = new Date(500);
        assertEquals(instance.getTime(), newTime);
        assertEquals(schedule1.next(), nextExpected);
        assertEquals(schedule2.next(), nextExpected);
        assertTrue(action1.actions.isEmpty());
        assertTrue(action2.actions.contains(passed));
    }


    /**
     * Test execute, multiple schedules with actions, two schedules passed
     */
    @Test
    public void testExecuteMultiScheduleTwoPassed() {
        System.out.println("execute, multi-schedule, two schedules passed");
        SimpleTickTrigger trig = new SimpleTickTrigger();
        TickScheduler instance = new TickScheduler();
        trig.registerAction(instance);
        Schedule schedule1 = new PlusSchedule(1000);
        Schedule schedule2 = new PlusSchedule(800);
        SimpleScheduleAction action1 = new SimpleScheduleAction();
        SimpleScheduleAction action2 = new SimpleScheduleAction();
        instance.registerAction(schedule1, action1);
        instance.registerAction(schedule2, action2);
        Date newTime = new Date(1001);
        trig.tick(newTime);
        assertEquals(instance.getTime(), newTime);
        assertEquals(schedule1.next(), new Date(2000));
        assertEquals(schedule2.next(), new Date(1600));
        assertTrue(action1.actions.contains(new Date(1000)));
        assertTrue(action2.actions.contains(new Date(800)));
    }


    /**
     * Simple tick trigger class that handles registrations appropriately and
     * generates ticks on demand.
     */
    private class SimpleTickTrigger implements TickTrigger {

        protected HashSet<TickAction> actions = new HashSet<TickAction>();

        public void registerAction(TickAction action) {
            this.actions.add(action);
        }

        public void unregisterAction(TickAction action) {
            this.actions.remove(action);
        }

        public void tick(Date time) {
            for (TickAction action : this.actions) {
                action.execute(this, time);
            }
        }
    }

}
