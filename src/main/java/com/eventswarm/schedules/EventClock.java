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
 * Clock class that maintains a clock based on the stream of events received from
 * a particular source.
 *
 * The current implementation has millisecond precision.  Events from other
 * sources are ignored to ensure the integrity of the clock.  Similarly, a 
 * clock cannot change its source.  
 * 
 * It is preferable that the event source only delivers events with timestamps
 * from the same or synchronised clocks to ensure consistency of schedulers
 * and other entities dependent on the accuracy of time from this source.
 *
 * @author andyb
 */
import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import java.util.Set;
import java.util.HashSet;
import java.util.Date;
import org.apache.log4j.Logger;

public class EventClock 
        implements Clock, AddEventAction, FirstTickTrigger, TickTrigger
{

    // Source of events
    private AddEventTrigger source = null;

    // current time (timestamp of last event received in milliseconds)
    private Date time;

    // set of actions registered for the TickTrigger
    private Set<TickAction> actions;

    // Set of actions registered for the FirstTickTrigger
    private Set<FirstTickAction> initActions;

    // Flag indicating if clock is initialised
    private boolean initialised;

    private static Logger log = Logger.getLogger(EventClock.class);
    
    /**
     * Create a new EventClock for the specified AddEventTrigger source.
     * 
     * @param source
     */
    public EventClock(AddEventTrigger source) {
        this.source = source;
        this.time = new Date(0);
        this.actions = new HashSet<TickAction>();
        this.initActions = new HashSet<FirstTickAction>();
        source.registerAction(this);
    }

    public boolean isInitialised() {
        return this.initialised;
    }

    /**
     * Return the source of this EventClock.
     *
     * @return
     */
    public Object getSource() {
        return this.source;
    }

    /**
     * Return the current time of this EventClock, which will be the largest
     * timestamp of any event received from the nominated source.
     * 
     * @return
     */
    public Date getTime() {
        return this.time;
    }

    /**
     * Update this EventClock using the provided Event, ignoring any events from
     * a source that does not match this EventClock's source.
     *
     * All registered EventTickActions will be called if the current time is
     * updated by this event. Null events will be ignored.
     * 
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // only accept events from the identified source
        if (event != null) {
            if (source.equals(trigger)) {
                Date newTime = event.getHeader().getTimestamp();
                // If not previously initialised, call registered FirstTickActions
                if (!initialised) {
                    initialised = true;
                    for (FirstTickAction action : this.initActions) {
                        action.execute(this, newTime);
                    }
                }
                if (this.time.compareTo(newTime) < 0) {
                    this.time = newTime;
                    for (TickAction action : this.actions) {
                        action.execute(this, time);
                    }
                } else {
                    log.debug("Out of order event received, ignored");
                }
            } else {
                log.debug("Event received from wrong source, ignored");
            }
        } else {
            log.debug("Null event received, ignored");
        }
    }

    /**
     * Register an TickAction against this EventClock
     *
     * @param action
     */
    public void registerAction(TickAction action) {
        this.actions.add(action);
    }

    /**
     * Unregister an TickAction
     * 
     * @param action
     */
    public void unregisterAction(TickAction action) {
        this.actions.remove(action);
    }

    public void registerAction(FirstTickAction action) {
        this.initActions.add(action);
    }

    public void unregisterAction(FirstTickAction action) {
        this.initActions.remove(action);
    }

}
