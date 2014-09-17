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
 * MutablePassThru that fires any scheduled actions that should occur before
 * the time of each event processed.
 *
 * This class should inserted prior to any processing of received events to
 * ensure that scheduled actions that should be taken before the time of an
 * event are processed.
 *
 * @author andyb
 */
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.eventset.MutablePassThruImpl;
import java.util.Date;
import org.apache.log4j.Logger;

public class SchedulerPassThru 
        extends MutablePassThruImpl
        implements ScheduleTrigger, Clock, TickTrigger, FirstTickTrigger
{

    private TickScheduler scheduler;
    private EventClock clock;

    private static Logger log = Logger.getLogger("SchedulerPassThru.class");

    /**
     * Create a new instance with an EventClock using this instance as an event
     * source and a TickScheduler receiving ticks from the EventClock.
     */
    public SchedulerPassThru() {
        super();
        this.clock = new EventClock(this);
        this.scheduler = new TickScheduler(this.clock);
    }

    /**
     * Override the default AddEventAction implementation to update our clock
     * and the attached TickScheduler before passing the event onwards.
     *
     * Null events are ignored (filtered out of the stream)
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        if (event != null) {
            this.clock.execute(this, event);
            super.execute(trigger, event);
        } else {
            log.debug("Null event received");
        }
    }

    /**
     * Delegate unregister of schedule actions to TickScheduler instance.
     *
     * @param schedule
     * @param action
     */
    public void unregisterAction(Schedule schedule, ScheduleAction action) {
        scheduler.unregisterAction(schedule, action);
    }

    /**
     * Delegate registration of schedule actions to TickScheduler instance.
     *
     * @param schedule
     * @param action
     */
    public void registerAction(Schedule schedule, ScheduleAction action) {
        scheduler.registerAction(schedule, action);
    }

    /**
     * Delegate unregister of clock tick actions to EventClock instance
     * 
     * @param action
     */
    public void unregisterAction(TickAction action) {
        clock.unregisterAction(action);
    }

    /**
     * Delegate registration of clock tick actions to EventClock instance
     *
     * @param action
     */
    public void registerAction(TickAction action) {
        clock.registerAction(action);
    }

    public void unregisterAction(FirstTickAction action) {
        clock.unregisterAction(action);
    }

    public void registerAction(FirstTickAction action) {
        clock.registerAction(action);
    }

    public boolean isInitialised() {
        return clock.isInitialised();
    }

    /**
     * Delegate getTime method to EventClock instance
     * @return
     */
    public Date getTime() {
        return clock.getTime();
    }

    /**
     * Clock source is this passthru instance
     * 
     * @return
     */
    public Object getSource() {
        return (this);
    }
}
