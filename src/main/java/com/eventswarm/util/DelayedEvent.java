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

package com.eventswarm.util;

/**
 *
 * @author zoki
 */
import java.util.concurrent.Delayed;
import com.eventswarm.events.Event;
import java.util.concurrent.TimeUnit;
        
public class DelayedEvent implements Delayed {
    
    private long expiryTime;
    private Event event;

    public DelayedEvent(long expiryTime, Event event) {
        this.expiryTime = expiryTime;
        this.event = event;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public Event getEvent() {
        return event;
    }

    public long getDelay(TimeUnit unit) {
        // native time is in milliseconds
        long millis = this.expiryTime - System.currentTimeMillis();
        // convert to appropriate units and return
        return unit.convert(millis, TimeUnit.MILLISECONDS);
    }

    /**
     * Compares two Delayed objects to determine their relative order in a manner
     * consistent with their delay times.
     * 
     * If the other has a shorter delay time, this method will return a positive 
     * value.  If the other has a longer delay time, this method will return a 
     * negative value.  If their delay times are the same but the other is not a
     * DelayedEvent, this method returns a positive value (i.e. after).  If the
     * delay times are the same and the other is a delayed event, this method
     * returns the result of compareTo on the two events.
     * 
     * @param other
     * @return difference between delay times, or if equal, arbitrary order
     */
    public int compareTo(Delayed other) {
        int difference = (int) (this.getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
        if (difference != 0) {
            // delay times are different, so return difference
            return difference;
        } else {
            // same delay, so apply a consistent ordering
            if (DelayedEvent.class.isInstance(other)) {
                // If both are DelayedEvents, compare their events
                return this.event.compareTo(((DelayedEvent) other).getEvent());
            } else {
                // if the other is not a DelayedEvent, return 1 and hope that 
                // this is consistent with the other implementation of compareTo
                return 1;
            }
        }
    }

}
