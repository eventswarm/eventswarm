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

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.util.Interval;
import com.eventswarm.util.IntervalUnit;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class BoundedDiscreteTimeWindow extends DiscreteTimeWindow {
    private int limit;

    private static Logger logger = Logger.getLogger(BoundedDiscreteTimeWindow.class);

    public BoundedDiscreteTimeWindow(IntervalUnit units, long windowSize, int limit) {
        super(units, windowSize);
        this.limit = limit;
    }

    public BoundedDiscreteTimeWindow(long windowSize, int limit) {
        super(windowSize);
        this.limit = limit;
    }

    public BoundedDiscreteTimeWindow(Interval interval, int limit) {
        super(interval);
        this.limit = limit;
    }

    /**
     * Override superclass add method to first check the size and remove events if size is above the limit
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        this.lock.writeLock().lock();
        try {
            // Adding an event, so make space if we're at or above the limit
            int remove = this.size() - this.limit + 1;
            if (remove <= 0) {
                // have space, so add
                super.execute(trigger, event);
            } else {
                // no space, have to remove one or more
                if (remove > 1) {
                    // was over the limit for some reason, so get rid of the extras
                    logger.warn("Window size (" + Integer.toString(this.size()) + ") is greater than limit (" +
                            Integer.toString(limit) + "). Removing an extra " + Integer.toString(remove - 1) + " events");
                    for (int i = 1; i < remove; i++) {
                        this.remove(first());
                    }
                }
                // at the limit now
                if (event.isAfter(this.first()) && !this.contains(event)) {
                    // if new event and newer than first, remove first and add
                    this.remove(first());
                    super.execute(trigger,event);
                } else {
                    // we're trying to shove an older event in a window at the limit: ignore it but warn
                    logger.warn("Window at limit, but event is older than first event in window or already in window, ignoring");
                }
            }
        } finally {
            // make sure we always unlock
            this.lock.writeLock().unlock();
        }
    }
}
