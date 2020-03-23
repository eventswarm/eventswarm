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

import com.eventswarm.AddEventTrigger;
import com.eventswarm.AddEventAction;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import org.junit.Test;
import static org.junit.Assert.*;
import com.eventswarm.events.jdo.*;
import java.util.Map;
import java.util.Date;
import java.util.ArrayList;

/**
 *
 * @author andyb
 */
public class SchedulerPassThruTest implements AddEventAction, ScheduleAction {
    
    static String SOURCE = "THIS";
    static Event event1 = new JdoEvent(new JdoHeader(new Date(999), 1, new JdoSource(SOURCE)), (Map<String,EventPart>) null);
    static Event event2 = new JdoEvent(new JdoHeader(new Date(1000), 1, new JdoSource(SOURCE)), (Map<String,EventPart>) null);
    static Event event3 = new JdoEvent(new JdoHeader(new Date(2000), 1, new JdoSource(SOURCE)), (Map<String,EventPart>) null);

    ArrayList<Object> objects = new ArrayList<Object>();

    public SchedulerPassThruTest() {
    }

    /**
     * Add null event, no downstream listeners or schedules, should be ignored
     */
    @Test
    public void testNullInitial() {
        System.out.println("execute, null event, no schedules or listeners");
        SchedulerPassThru instance = new SchedulerPassThru();
        instance.execute((AddEventTrigger) null, null);
        assertTrue(this.objects.isEmpty());
    }



    /**
     * Add null event, with downstream AddEventAction, null event is ignored
     */
    @Test
    public void testNullWithAddAction() {
        System.out.println("execute, null event, AddEventAction registered");
        SchedulerPassThru instance = new SchedulerPassThru();
        instance.registerAction((AddEventAction) this);
        instance.execute((AddEventTrigger) null, null);
        assertTrue(this.objects.isEmpty());
    }


    /**
     * Add null event, with downstream AddEventAction and ScheduleAction, null event is ignored
     */
    @Test
    public void testNullWithScheduleAndAddAction() {
        System.out.println("execute, null event, AddEventAction and ScheduleAction registered");
        SchedulerPassThru instance = new SchedulerPassThru();
        PlusSchedule schedule = new PlusSchedule(1000);
        instance.registerAction((AddEventAction) this);
        instance.registerAction(schedule, (ScheduleAction) this);
        instance.execute((AddEventTrigger) null, null);
        assertTrue(this.objects.isEmpty());
    }


    /**
     * Add event, with downstream AddEventAction and ScheduleAction, schedule not fired
     */
    @Test
    public void testEventScheduleNotFired() {
        System.out.println("Add event, scheduled time not reached");
        SchedulerPassThru instance = new SchedulerPassThru();
        PlusSchedule schedule = new PlusSchedule(1000);
        instance.registerAction((AddEventAction) this);
        instance.registerAction(schedule, (ScheduleAction) this);
        instance.execute((AddEventTrigger) null, event1);
        assertEquals(1, this.objects.size());
        assertTrue(this.objects.contains(event1));
    }


    /**
     * Add event, with downstream AddEventAction and ScheduleAction, schedule fired
     */
    @Test
    public void testEventScheduleFired() {
        System.out.println("Add event, scheduled time reached");
        SchedulerPassThru instance = new SchedulerPassThru();
        PlusSchedule schedule = new PlusSchedule(1000);
        instance.registerAction((AddEventAction) this);
        instance.registerAction(schedule, (ScheduleAction) this);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(2, this.objects.size());
        assertEquals(event2.getHeader().getTimestamp(), this.objects.get(0));
        assertEquals(event2, this.objects.get(1));
    }


    /**
     * Add 2 events, with downstream AddEventAction and ScheduleAction,
     * schedule fired after first event
     */
    @Test
    public void test2EventsScheduleFired() {
        System.out.println("Add 2 events, schedule fired by second event");
        SchedulerPassThru instance = new SchedulerPassThru();
        PlusSchedule schedule = new PlusSchedule(1000);
        instance.registerAction((AddEventAction) this);
        instance.registerAction(schedule, (ScheduleAction) this);
        instance.execute((AddEventTrigger) null, event1);
        instance.execute((AddEventTrigger) null, event2);
        assertEquals(3, this.objects.size());
        assertEquals(event1, this.objects.get(0));
        assertEquals(event2.getHeader().getTimestamp(), this.objects.get(1));
        assertEquals(event2, this.objects.get(2));
    }


    /**
     * Add 2 events, with downstream AddEventAction and ScheduleAction,
     * schedule fired after first event
     */
    @Test
    public void test2EventsScheduleFiredTwice() {
        System.out.println("Add 2 events, schedule fired by both events");
        SchedulerPassThru instance = new SchedulerPassThru();
        PlusSchedule schedule = new PlusSchedule(1000);
        instance.registerAction((AddEventAction) this);
        instance.registerAction(schedule, (ScheduleAction) this);
        instance.execute((AddEventTrigger) null, event2);
        instance.execute((AddEventTrigger) null, event3);
        assertEquals(4, this.objects.size());
        assertEquals(event2.getHeader().getTimestamp(), this.objects.get(0));
        assertEquals(event2, this.objects.get(1));
        assertEquals(event3.getHeader().getTimestamp(), this.objects.get(2));
        assertEquals(event3, this.objects.get(3));
    }

    /**
     * Simple add event action that stores received events in a list. List order
     * indicates order of actions.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        this.objects.add(event);
    }

    /**
     * Schedule action puts the date into the same list as AddEvent.  List order
     * indicates order of actions.
     * 
     * @param trigger
     * @param schedule
     * @param time
     */
    public void execute(ScheduleTrigger trigger, Schedule schedule, Date time) {
        this.objects.add(time);
    }

}