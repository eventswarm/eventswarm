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
 * Schedule to fire a fixed intervals relative to a "seed" time.
 *
 * @author andyb
 */
import com.eventswarm.util.Interval;
import java.util.Date;

public class IntervalSchedule extends Schedule {

    private Date seed;
    private Interval interval;
    private long intMillis;
    private long seedMillis;
    
    /**
     * Create new interval schedule using specified clock, seed and interval.
     *
     * @param clock Initialise the schedule using the specified clock
     * @param seed Scheduled times will always be a whole number of intervals
     *             greater or less than this seed time. 
     * @param interval An interval object specifying the interval between schedule
     *                 activations
     */
    public IntervalSchedule(Clock clock, Date seed, Interval interval) {
        super();
        this.setAttributes(clock, seed, interval);
    }

    /**
     * Create new interval schedule using the specified seed and interval. 
     * 
     * Since no clock is supplied, the time needs to be initialised by an
     * explicit call to setTime or by subsequent calls to the setClock method of
     * the parent class.
     *
     * @param seed Scheduled times will always be a whole number of intervals
     *             greater or less than this seed time.
     * @param interval An interval object specifying the interval between schedule
     *                 activations
     */
    public IntervalSchedule(Date seed, Interval interval) {
        super();
        this.setAttributes(null, seed, interval);
    }

    /**
     * Create a new interval schedule using the specified interval and using the
     * system time as the seed.
     *
     * Since no clock is supplied, the time needs to be initialised by an
     * explicit call to setTime or by subsequent calls to the setClock method of
     * the parent class.
     * 
     * @param interval
     */
    public IntervalSchedule(Interval interval) {
        this.setAttributes(null, new Date(), interval);
    }

    private void setAttributes(Clock clock, Date seed, Interval interval) {
        this.seed = seed;
        this.interval = interval;
        this.intMillis = interval.getIntervalMillis();
        this.seedMillis = seed.getTime();
        // rely on parent to set the initial time
        this.setClock(clock);
    }

    /**
     * Get the value of interval
     *
     * @return the value of interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * Get the value of this schedule's seed
     *
     * @return the value of seed
     */
    public Date getSeed() {
        return seed;
    }

    /**
     * Set the next time to a whole number of intervals strictly after
     * the specified time.
     *
     * @param currentTime
     */
    @Override
    public void setTime(Date currentTime) {
        long time = currentTime.getTime();
        long intervals = Math.abs((time - seedMillis) / intMillis);
        if (time >= seedMillis) {
            // New time is >= seed, so add intervals and skip one ahead
            this.next = new Date(seedMillis + (intervals + 1) * intMillis);
        } else {
            // new time is < than seed, so remove intervals
            this.next = new Date(seedMillis - (intervals) * intMillis);
        }
    }

}
