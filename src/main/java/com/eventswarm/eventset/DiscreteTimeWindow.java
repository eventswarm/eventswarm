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

package com.eventswarm.eventset;

/**
 * Sliding time window implementation based on Event timestamps that moves the 
 * window only in response to the addition of new events.
 * 
 * Old events are removed when a new event is received with a timestamp greater
 * than the window size ahead of the old events.  This class is primarily 
 * intended for post-processing of pre-sorted events and can result in incorrect
 * windows when events are delivered out-of-order.  Out-of-order events are 
 * processed, but fire the OutOfOrderTrigger to notify downstream
 * clients that are interested to know about it.
 * 
 * For example, if the oldest event in the window (E1) occurred at 10am and the 
 * window size is one hour, an event with a timestamp of 11:01am will cause the 
 * removal of E1.  If another event (E2) is subsequently delivered with a 
 * timestamp of 10:59am, a (correct) window that includes both E1 and E2 will 
 * have been missed.  
 * 
 * Note that as with other time windows, the class does not allow upstream 
 * abstractions to remove events from the window.
 * 
 * Note also that this class relies on the use of a SortedSet in the EventSet 
 * and a time-consistent implementation of the compareTo method of JdoEvent for 
 * time-based ordering of events.  If the EventSet or JdoEvent implementation 
 * changes, it might be necessary to re-implement this class.
 * 
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */
import com.eventswarm.schedules.TickTrigger;
import com.eventswarm.util.IntervalUnit;
import com.eventswarm.util.Interval;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.*;
import java.util.*;
import java.text.SimpleDateFormat;


public class DiscreteTimeWindow 
        extends AbstractTimeWindow 
        implements OutOfOrderTrigger // TickAction
{

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(DiscreteTimeWindow.class);

    /** set of actions registered against the OutOfOrderTrigger */
    private Set<OutOfOrderAction> incActions;
    
    /**
     * Records the millisecond time of the current "end" of the time window 
     * (i.e. time of oldest event + window size). 
     */
    private long end;

    /* date format for debug output */
    private static final SimpleDateFormat FMT = new SimpleDateFormat("HH:mm:ss.SSS");

    public DiscreteTimeWindow(IntervalUnit units, long windowSize) {
        super(units, windowSize);
    }

    public DiscreteTimeWindow(long windowSize) {
        super(windowSize);
    }
    
    public DiscreteTimeWindow(Interval interval) {
        super(interval);
    }

    @Override
    protected void init() {
        this.incActions = new HashSet<OutOfOrderAction>();
        this.end = 0;
    }

    /**
     * In a discrete time window, the addition of an event triggers the removal
     * of old events if required to correctly maintain the size of the time 
     * window.
     * 
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // we always need to know the time of the new event
        long time = event.getHeader().getTimestamp().getTime();
        long last = -1;
        if (!this.eventSet.isEmpty()) {
            last = this.eventSet.last().getHeader().getTimestamp().getTime();
        }
        // grab a write lock on the EventSet to ensure our operations are consistent
        this.lock.writeLock().lock();
        try {
            // if event is out-of-order, fire the out-of-order trigger
            if (last > time) {
                // out of order event, so fire our inconsistency trigger but
                // we still continue on to add the event to our window
                log.debug("Out-of-order event received");
                for (OutOfOrderAction action : this.incActions) {
                    action.execute(this, event);
                }
            } else {
                // if not out of order, remove any events pushed out by the
                // time of this new event
                log.debug("Adjusting time window for end time " +
                          FMT.format(event.getHeader().getTimestamp()));
                this.adjustTimeWindow(time);
            }

            // Don't add events that are before the start of the time window
            if (last > (time + this.windowSize)) {
                log.debug("Event is prior to current time window. Ignoring");
            } else {
                // use the parent class method to add the new event to the EventSet
                super.execute(trigger, event);
            }

            // fire the WindowChangeTrigger
            this.fire();

            log.debug("Time window now contains " + Integer.toString(this.size()) + " events");
        } finally {
            // make sure we always unlock
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Returns the current end of the time window
     * 
     * This value is calculated by adding the size of the time window to the 
     * timestamp of the oldest event.  It can be used to give an indication of 
     * how close the window is to real-time.  Note that the end of the window 
     * can be ahead of (greater than) the current local time when the window is
     * filling or when the oldest event has occurred only recently.    
     * 
     * @return time of oldest event + windowsize or 0 if the window is empty
     */
    public long getEnd() {
        if (this.eventSet.isEmpty()) {
            return 0;
        } else {
            return (this.eventSet.first().getHeader().getTimestamp().getTime()
                             + this.windowSize);
        }
    }

    public void registerAction(OutOfOrderAction action) {
        this.incActions.add(action);
    }

    public void unregisterAction(OutOfOrderAction action) {
        this.incActions.remove(action);
    }

    public void reset() {
        this.incActions.clear();
        super.reset();
    }
    
    private void fire(Event event) {
    }

    /**
     * Adjust the time window to reflect the time, removing any events that are
     * pushed outside the time window.
     *
     * @param trigger
     * @param time -- long value returned by a Date getTime invocation
     */
    public void execute(TickTrigger trigger, long time) {
        log.debug("Adjusting time window for event");
        this.lock.writeLock().lock();
        try {
            this.adjustTimeWindow(time);
        } finally {
             // make sure we always unlock
            this.lock.writeLock().unlock();
        }
    }
}
