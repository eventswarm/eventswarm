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
package com.eventswarm.eventset;

/**
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

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.*;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AtMostNWindow extends AbstractFilter implements WindowChangeTrigger
{

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(AtMostNWindow.class);
    
    /** Number of events to keep in window */
    private int windowSize;

    /** actions registered for the EventSetChangeTrigger of this window */
    protected HashSet<WindowChangeAction> actions;

    /** need our own lock to group actions into a transaction that makes sense */
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * Create a window that holds the last <code>windowSize</code> events.
     * 
     * Minimum window size is 1.  If a zero or negative size is requested, a 
     * window of size 1 will be created.
     * 
     * @param windowSize
     */
    public AtMostNWindow(int windowSize) {
        super();
        if (windowSize < 1) {
            this.windowSize = 1;
        } else {
            this.windowSize = windowSize;
        }
        this.actions = new HashSet<WindowChangeAction>();
    }

    /** Always accepts events for addition to the set */
    @Override
    protected boolean include(Event event) {
        return true;
    }

    /**
     * Add a new event to the window.  If the window is full (N >= windowSize),
     * then remove events until it returns to the correct maximum size.
     * 
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        this.lock.writeLock().lock();
        try {
            // Adding an event, so make space if we're at or above the limit
            int size = this.eventSet.size();
            int remove = size - this.windowSize + 1;
            boolean add;
            if (remove <= 0) {
                add = true; // window not full, so definitely add
            } else {
                if (event.isBefore(this.eventSet.first())) {
                    add = false;
                    log.warn("Event is older than earliest event in window, ignoring");
                } else {
                    add = true;
                    for (int i = 0; i < remove; i++) {
                        // call parent class method: local method generates window change trigger, which is not desired
                        super.execute((RemoveEventTrigger) this, this.eventSet.first());
                    }
                    // It's bad if we remove more than one (we were above the limit, for some reason)
                    if (remove > 1) {
                        log.info("Window size (" + Integer.toString(size) + ") was greater than limit (" +
                                Integer.toString(windowSize) + "). Removed an extra " + Integer.toString(remove - 1) + " events");
                    }
                } 
            } 

            if (add) {
                super.execute(trigger, event);
                // fire the WindowChangeTrigger
                this.fire();
            }

            log.debug("Window now contains " + Integer.toString(this.size()));
        } finally {
            // make sure we always unlock
            this.lock.writeLock().unlock();
        }
    }

    /**
     * Call parent implementation, but also generate a WindowChangeTrigger
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        this.lock.writeLock().lock();
        try {
            super.execute(trigger, event);
            // fire the WindowChangeTrigger
            this.fire();
            log.debug("Window now contains " + Integer.toString(this.size()));
        } finally {
            // make sure we always unlock
            this.lock.writeLock().unlock();
        }
    }


    
    public boolean isFilling() {
        return this.eventSet.size() < this.windowSize;
    }

    public void reset() {
        this.actions.clear();
        super.reset();
    }
    /**
     * Register an EventSet change action
     *
     * @param action
     */
    public void registerAction(WindowChangeAction action) {
        this.actions.add(action);
    }

    /**
     * Unregister an EventSet change action
     *
     * @param action
     */
    public void unregisterAction(WindowChangeAction action) {
        this.actions.remove(action);
    }


    protected void fire() {
        for (WindowChangeAction action : this.actions) {
            action.execute(this, this);
        }
    }
}
