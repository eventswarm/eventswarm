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

/**
 * Scheduler to schedule actions based on a TickTrigger source
 *
 * This scheduler calls scheduled actions as soon as a tick arrives indicating
 * that the next due date of a schedule has passed.
 * 
 * This scheduler could be attached to any discrete tick source, for example,
 * an EventClock that generates ticks based on event timestamps, or a real time
 * clock that generates tick events every minute.
 *
 * @author andyb
 */
import java.util.HashSet;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Date;
import java.util.Iterator;
import org.apache.log4j.Logger;

public class TickScheduler implements ScheduleTrigger, TickAction {

    // Set of actions registered for schedule triggers
    private HashMap<Schedule, HashSet<ScheduleAction>> actions;
    private TreeSet<Schedule> queue;
    private HashSet<Schedule> hold;

    // Current time associated with the schedule
    private Date time;

    private static Logger log = Logger.getLogger(TickScheduler.class);
    
    /**
     * Create a new TickScheduler.  This scheduler needs to be registered against
     * one or more TickTrigger sources to function.
     *
     * Note that it is dangerous to use multiple sources of ticks unless their
     * times are based on the same clock (you can get out-of-order ticks).
     */
    public TickScheduler() {
        this.setAttributes();
    }


    /**
     * Create a new TickScheduler and register it against the identified source
     * of ticks.
     */
    public TickScheduler(TickTrigger source) {
        source.registerAction(this);
        this.setAttributes();
    }

    private void setAttributes() {
        this.actions = new HashMap<Schedule, HashSet<ScheduleAction>>();
        this.queue = new TreeSet<Schedule>();
        this.hold = new HashSet<Schedule>();
        // set our initial time to the beginning of the epoch
        this.time = new Date(0);
    }

    /**
     * Return the current time known by the scheduler
     * 
     * @return
     */
    public Date getTime() {
        return this.time;
    }
    
    /**
     * Add a schedule to this scheduler if it does not already exist, queuing
     * the schedule based on the time it should next fire.
     * 
     * @param schedule
     */
    private void addSchedule(Schedule schedule) {
        if (!this.actions.containsKey(schedule)) {
            schedule.setTime(this.time);
            this.actions.put(schedule, new HashSet<ScheduleAction>());
            this.queue.add(schedule);
        }
    }

    /**
     * Remove a schedule from this scheduler if it exists and has no associated
     * actions.
     *
     * @param schedule
     */
    private void removeSchedule(Schedule schedule) {
        if (this.actions.containsKey(schedule) && this.actions.get(schedule).isEmpty()) {
            this.actions.remove(schedule);
            this.queue.remove(schedule);
        }
    }

    /**
     * Register an action against the specified schedule, adding the schedule
     * if it does not already exist.
     * 
     * @param schedule
     * @param action
     */
    public void registerAction(Schedule schedule, ScheduleAction action) {
        if (action == null) {
            throw new NullPointerException("Scheduler action cannot be null");
        }
        this.addSchedule(schedule);
        this.actions.get(schedule).add(action);
    }

    /**
     * Unregister an action from the specified schedule, removing the schedule
     * if it no longer has any registered actions. 
     * 
     * If the schedule does not exist or is null, this method has no effect.
     * Similarly, if the action is null or does not exist, it is ignored.
     * 
     * @param schedule
     * @param action
     */
    public void unregisterAction(Schedule schedule, ScheduleAction action) {
        if (actions.containsKey(schedule)) {
            actions.get(schedule).remove(action);
            this.removeSchedule(schedule);
        } else {
            log.debug("Attempt to unregister an unknown schedule");
        }
    }

    /**
     * Update the time associated with this scheduler and fire any actions that
     * are now due (i.e. their next date is <= the updated time)
     *
     * Ticks containing times that are before the current time will be ignored.
     *
     * Schedules with more than one due date prior to the new time will be fired
     * multiple times, once for each earlier due date. For example, if the
     * previous time was 11:01 and a schedule is set to run every 10 minutes at
     * 11:10, 11:20 etc, a tick trigger with a time of 11:31 will fire the
     * schedule trigger with times 11:10, 11:20 and 11:30.
     * 
     * Actions associated with multiple schedules will be called once for each
     * schedule due date that has become due. 
     * 
     * @param trigger
     * @param time
     */
    public void execute(TickTrigger trigger, Date time) {
        if (this.time.before(time)) {
            // update the current time
            this.time = time;

            // fire any schedules that have arrived
            Iterator<Schedule> iter = this.queue.iterator();
            Schedule sched;
            while (iter.hasNext()) {
                sched = iter.next();
                if (sched.compareTo(time) <= 0) {
                    // remove it from the queue
                    iter.remove();
                    // fire any actions, firing multiple times if multiple scheduled
                    // times have been passed by the new time
                    while (sched.compareTo(time) <= 0) {
                        this.fire(sched);
                        sched.setTime(sched.next());
                    }
                    // hold this schedule for adding back into the queue
                    this.hold.add(sched);
                } else {
                    // exit loop when we hit a schedule that is not ready to fire
                    break;
                }
            }

            // Add fired and reset schedules back into the queue
            this.queue.addAll(this.hold);
            this.hold.clear();
        } else {
            log.debug("Out of order tick received and ignored");
        }
    }

    /**
     * Fire the actions associated with an identified schedule
     * 
     * @param sched
     */
    private void fire(Schedule sched) {
        for (ScheduleAction action : this.actions.get(sched)) {
            // Fire the action, indicating the schedule time that has passed
            action.execute(this, sched, sched.next());
        }
    }

    /**
     * Return the current queue of schedules waiting to be fired
     * 
     * This method is primarily intended for use in unit testing. The queue
     * should not be interfered with.
     * 
     * @return
     */
    protected TreeSet<Schedule> getQueue() {
        return this.queue;
    }

    /**
     * Return the current map of schedules and associated actions
     *
     * This method is primarily intended for use in unit testing assertions. The 
     * actions should not be interfered with.
     */
    protected HashMap<Schedule, HashSet<ScheduleAction>> getActions () {
        return this.actions;
    }
}
