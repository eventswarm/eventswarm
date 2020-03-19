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

import com.eventswarm.*;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * An abstraction to monitor the number of events received from a source (less removed events, if connected)
 * and trigger actions when a threshold is reached (i.e. count == threshold).
 *
 * The monitor will be disabled after firing until the count returns below the 'resetAt' value or the monitor
 * is explicitly reset(). The resetAt value must be less than the threshold: if a greater value is specified,
 * it will be set to (threshold - 1); Note that an explicit reset when size > threshold means that the monitor will
 * fire in the downwards direction. The clear() method will restart the count from zero and reset the monitor.
 *
 * If attaching this monitor to an EventSet that already contains events, callers should set the current size value.
 *
 * Note that downstream actions that rely on the getSource method might exhibit unusual behaviour if this monitor
 * is connected to multiple sources. If aggregate numbers are desired, it is preferable to insert an EventSet or
 * passthru to joins the sources.
 *
 * Copyright 2012 Ensift Pty Ltd
 *
 * @author andyb
 */

public class SizeThresholdMonitor
        implements AddEventAction, RemoveEventAction, SizeThresholdTrigger
{

    /* private logger for log4j */
    private static Logger logger = Logger.getLogger(SizeThresholdMonitor.class);

    public static final long DEFAULT_RESET = 0l;

    /**
     * Hold the current size of EventSet, threshold value and ResetAt value for re-enabling Threshold monitor.
     * Reenabling can be invoked either explicitly by a user or when the threshold is reached
     */

    private long size;
    private long threshold;
    private long resetAt;
    private AddEventTrigger source;

    public boolean isEnabled() {
        return enabled;
    }

    private boolean enabled;

    private Set<SizeThresholdAction> actions = null;

    /**
     * Creates SizeThresholdMonitor which takes just the threshold value and sets current size to zero.
     */
    public SizeThresholdMonitor(long threshold) {
        this(threshold, DEFAULT_RESET);
    }

    /**
     * Creates SizeThresholdMonitor which takes the threshold value and resetAt value.
     */
    public SizeThresholdMonitor(long threshold, long resetAt) {
        this(threshold, resetAt, 0);
    }

    /**
     * Create SizeThresholdMonitor that takes the threshold value and current size of Eventset
     */
    public SizeThresholdMonitor(long threshold, long resetAt, long size) {
        this.threshold = threshold;
        setResetAt(resetAt);
        this.size = size;
        this.actions = new HashSet<SizeThresholdAction>();
        this.enabled = true;
    }

    public long getThreshold() {
        return threshold;
    }

    public long getSize() {
        return size;
    }

    /**
     * Explicitly set the current size associated with the size threshold monitor.
     *
     * You might use this method if you want to add a monitor to an existing eventset (e.g. setSize(eventSet.size()).
     * Note that setting the size will not fire the threshold trigger, even if threshold == size. Only new events
     * can cause the trigger to fire.
     *
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }

    public long getResetAt() {
        return resetAt;
    }

    public void setResetAt(long resetAt) {
        if (resetAt < threshold) {
            this.resetAt = resetAt;
        } else {
            logger.warn("Attempt to define a reset value greater than the threshold");
            this.resetAt = threshold - 1;
        }
    }

    /**
     * Explicitly re-enable the threshold trigger
     *
     * Note that a reset() when the size is greater than the threshold means that the monitor will fire in
     * the downwards (size reducing) direction. Use with care.
     */
    public void reset() {
        this.enabled = true;
    }

    /**
     * Clear the count and re-enable the threshold trigger
     */
    public void clear() {
        this.size = 0;
        this.reset();
    }

    /**
     * Increment EventSet size and fire trigger if this increment makes us equal
     * to the threshold.
     *
     * @param trigger The upstream AddEventTrigger that called the method
     * @param event The upstream event that caused Event Set size increment
     */
    public void execute(AddEventTrigger trigger, Event event) {
        this.size += 1;
        if (this.size == this.threshold && this.enabled) {
            this.source = trigger;
            this.fireSizeThreshold(event);
            this.enabled = false;
        }
        checkResetAt();
    }

    /**
     * Returns the trigger associated with the most recent threshold match or null if no match has occured
     *
     * @return
     */
    public AddEventTrigger getSource() {
        return this.source;
    }

    protected void fireSizeThreshold(Event event) {
        // notify all of the SizeThresholdTrigger listeners
        for (SizeThresholdAction action : this.actions) {
            action.execute(this, event, size);
        }
    }

    protected void checkResetAt() {
        if (this.size == this.resetAt) {
            this.enabled = true;
        }
    }

    /**
     * Decrement EventSet size
     * 
     * @param trigger The upstream RemoveEventTrigger that called the method
     * @param event The upstream event whose removal caused decrement of EventSet size
     */
    public void execute(RemoveEventTrigger trigger, Event event) {
        this.size -= 1;
        checkResetAt();
    }

    public void registerAction(SizeThresholdAction action) {
        this.actions.add(action);
    }


    public void unregisterAction(SizeThresholdAction action) {
        this.actions.remove(action);
    }
}

