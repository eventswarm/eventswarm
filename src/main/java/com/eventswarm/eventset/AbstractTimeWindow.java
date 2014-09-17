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
 * Abstract class for all time window implementations.
 * 
 * This class includes common constructors and other methods that should be used
 * by all time windows.
 *
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import com.eventswarm.util.IntervalUnit;
import com.eventswarm.util.Interval;
import com.eventswarm.events.Event;
import com.eventswarm.RemoveEventTrigger;
import org.apache.log4j.*;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;

public abstract class AbstractTimeWindow 
        extends AbstractFilter implements RemoveEventTrigger, WindowChangeTrigger
{

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(AbstractTimeWindow.class);  
    
    /** window size in milliseconds */
    protected long windowSize;

    /** actions registered for the EventSetChangeTrigger of this window */
    protected HashSet<WindowChangeAction> actions;

    /** thread for removing events as they go outside the processing window */

    /** hide no-argument constructor so that it can't be used */
    private AbstractTimeWindow() {
    }

    /**
     * Flag set when the eventset is initially filling.  Cleared when the
     * earliest event is removed.
     */
    private boolean filling;

    /* date format for debug output */
    private static final SimpleDateFormat FMT = new SimpleDateFormat("HH:mm:ss.SSS");

    /**
     * Constructor for ProcessingTimeWindows with window size in seconds
     * 
     * @param windowSize Size of window in seconds
     */
    public AbstractTimeWindow(long windowSize) {
        setup(IntervalUnit.SECONDS.getIntervalMillis(windowSize));
    }

    /**
     * Constructor for WindowSize expressed an Interval object
     *
     * @param interval
     */
    public AbstractTimeWindow(Interval interval) {
        log.debug("Creating time window with window size of " + Long.toString(interval.getInterval()) + " " + interval.getUnit().toString());
        setup(interval.getIntervalMillis());
    }


    /**
     * Constructor for WindowSize expressed using specific IntervaUnits
     * 
     * @param units Units for window size (i.e. SECONDS, MINUTES, HOURS, DAYS, WEEKS);
     * @param windowSize
     */
    public AbstractTimeWindow(IntervalUnit units, long windowSize) {
        log.debug("Creating time window with window size of " + Long.toString(windowSize) + " " + units.toString());
        setup(units.getIntervalMillis(windowSize));
    }

    private void setup(long windowSize) {
        this.windowSize = windowSize;
        log.debug("Window size is " + Long.toString(this.windowSize) + " milliseconds");
        this.actions = new HashSet<WindowChangeAction>();
        this.filling = true;
        this.init();
    }

    /**
     * Abstract method to be replaced by instance initialisation code in child
     * classes.  
     * 
     * Child classes can assume that the window size has been defined in milliseconds.
     */
    protected abstract void init();

    /**
     * Return true if the time window has not yet removed any events.
     *
     * The removal of the first event from the time window indicates that the
     * time window has been filled and needs to be shifted to include the next
     * event.  During "filling" (i.e. before the first removal), it is possible
     * that there are more events that fit into the current window.  If this is
     * important to any of your downstream abstractions, you should check this
     * flag before reporting results from your application.
     *
     * @return
     */
    public boolean isFilling() {
        return this.filling;
    }

    protected void setFilled() {
        this.filling = false;
    }


    public void reset() {
        this.actions.clear();
    }

    /**
     * All events should be accepted by time windows.
     * 
     * @param event 
     * @return always true
     */
    @Override
    protected boolean include(Event event) {
        return true;
    }

    /**
     * Overrides the remove event action to ensure upstream abstractions do not 
     * remove events from time windows.
     * 
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        // ignore upstream event removal
    }

    /**
     * Return the window size in milliseconds
     *
     * @return
     */
    public long getWindowSize() {
        return windowSize;
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

    /**
     * Remove any events that are pushed outside the time window bounded by the
     * specified time.
     *
     * This method does not lock the buffer: it assumes the calling method is
     * doing the locking.
     *
     * @param newTime
     */
    protected void adjustTimeWindow (long newTime) {
        // remove any events that are now outside the time window
        try {
            if (!this.eventSet.isEmpty()) {
                for (Event first = this.eventSet.first();
                     first.getHeader().getTimestamp().getTime() + this.windowSize < newTime;
                     first = this.eventSet.first())
                {
                    this.filling = false;
                    log.debug("Removing event with timestamp " +
                              FMT.format(first.getHeader().getTimestamp()));
                    this.remove(first);
                }
            }
        } catch (NoSuchElementException exc) {
            // this happens when the EventSet is empty, in which case
            // we want to just fall out of the loop.  Not pretty, but more
            // efficient and easier to read than explicitly checking
            // 'isEmpty()' all the time.
        }
    }

}
