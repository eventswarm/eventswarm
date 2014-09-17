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
 * Simple bean class capturing an Interval
 *
 * @author andyb
 */
import org.apache.log4j.Logger;

public class Interval {

    private long interval, millis;
    private IntervalUnit unit;

    private static Logger log = Logger.getLogger(Interval.class);

    public Interval(long interval, IntervalUnit unit) {
        this.interval = interval;
        this.unit = unit;
        this.millis = unit.getIntervalMillis(interval);
    }

    /**
     * Get time unit associated with this interval (SECOND, MINUTE, HOUR etc).
     *
     * @return the value of unit
     */
    public IntervalUnit getUnit() {
        return unit;
    }

    /**
     * Get the size of the interval in its specified unit.
     *
     * @return the value of interval
     */
    public long getInterval() {
        return interval;
    }


    /**
     * Get the size of the interval in milliseconds.
     * 
     * @return
     */
    public long getIntervalMillis() {
        return millis;
    }

    /**
     * Interval objects are equal if they evaluate to the same number of
     * milliseconds.  All other objects use the Object equals method.
     * 
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (Interval.class.isInstance(obj)) {
            return (this.equals((Interval) obj));
        } else {
            return super.equals(obj);
        }
    }

    /**
     * Specific equals comparison for objects that are Intervals, where objects
     * are equal if they evaluate to the same number of milliseconds
     *
     * @param compare
     * @return
     */
    public boolean equals(Interval compare) {
        return (this.getIntervalMillis() == compare.getIntervalMillis());
    }

    
    /**
     * Override hashcode by returning the remainder of dividing the (long)
     * interval in milliseconds by the maximum (int) value.
     *
     * This provides a result that is consistent with equals, which compares
     * the millisecond value of the interval.
     * 
     * @return
     */
    @Override
    public int hashCode() {
        return (int) (this.getIntervalMillis() % (long) Integer.MAX_VALUE);
    }

    /**
     * Return a string representation based on the units of specification
     * @return
     */
    @Override
    public String toString() {
        if (this.interval > 1) {
            return Long.toString(this.interval) + " " + this.unit.toString() + "s";
        } else {
            return Long.toString(this.interval) + " " + this.unit.toString();
        }
    }

}
