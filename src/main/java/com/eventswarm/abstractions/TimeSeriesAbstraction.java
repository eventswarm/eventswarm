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

/**
 * Records a time series of values, that is, a set of <Date,Number> pairs.
 * 
 * The time series maintains a set of <Date,Number> pairs according to the 
 * values provided through its NumericValueAction interfaces.  
 * Timestamps are extracted from the events associated with the actions. 
 * The primary purpose of this class is to provide a simple mechanism for 
 * plotting data values and analysing trends.
 * 
 * This implementation does not allow multiple values with the same timestamp.  
 * If two or more events have the same timestamp, then only the last received 
 * value associated with the timestamp will be included in the time series.  
 * When any of those events are removed, the value associated with the 
 * timestamp will be removed.
 *
 * This abstraction implements only the ValueRetriever actions, meaning it cannot
 * be attached directly to an EventSet or other event-based abstractions that
 * do not implement ValueRetriever triggers.
 * 
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import com.eventswarm.events.Event;
import org.apache.log4j.*;
import java.util.*;

public class TimeSeriesAbstraction 
        implements NumericValueAddAction, NumericValueRemoveAction, 
        Iterable<Map.Entry<Date,Number>>
{

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(TimeSeriesAbstraction.class);
    
    /** Hold the timeseries values in a sorted set */
    private SortedMap<Date,Number> timeSeries;

    /**
     * Create a new TimeSeriesAbstraction, initialising the underlying SortedMap.
     */
    public TimeSeriesAbstraction() {
        this.timeSeries = new TreeMap<Date,Number>();
    }
    
    /**
     * Add a new value to the time series.
     * 
     * This method inserts a new value into a SortedSet using the event 
     * event timestamp as key and the provided number as value.  If a value is
     * already recorded with the same timestamp, the previous value is replaced.
     * 
     * @param trigger The upstream NumericValueAddTrigger that called the method
     * @param event The upstream event that caused the new value to be added
     * @param number The new value
     */
    public void execute(NumericValueAddTrigger trigger, Event event, Number number) {
        this.timeSeries.put(event.getHeader().getTimestamp(), number);
    }

    /**
     * Remove a value from the time series
     * 
     * This method removes the value associated with the event timestamp from
     * the time series.  If a previous event has been removed with the same 
     * timestamp (and no subsequent add has occurred with the same timestamp) 
     * then there will be no time series value to remove.
     * 
     * @param trigger The upstream NumericValueRemoveTrigger that called the method
     * @param event The upstream event whose removal caused the value remove action
     * @param number The number to be removed
     */
    public void execute(NumericValueRemoveTrigger trigger, Event event, Number number) {
        this.timeSeries.remove(event.getHeader().getTimestamp());
    }

    public Iterator<Map.Entry<Date,Number>> iterator() {
        return this.timeSeries.entrySet().iterator();
    }
    
}
