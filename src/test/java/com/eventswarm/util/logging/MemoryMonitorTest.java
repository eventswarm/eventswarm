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
package com.eventswarm.util.logging;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.LogEvent;
import com.eventswarm.schedules.TickTrigger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;
import static com.eventswarm.events.jdo.TestEvents.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MemoryMonitorTest implements AddEventAction {
    private Log4JChannel channel;
    private ArrayList<Event> events;

    @Before
    public void setUp() throws Exception {
        // set up to receive log events via our Log4JChannel class
        channel = new Log4JChannel();
        Logger logger = Logger.getLogger(MemoryMonitor.class);
        logger.addAppender(channel);
        events = new ArrayList<Event>();
        channel.registerAction(this);
    }

    @Test
    public void percentage_zero() throws Exception {
        int thresholds[] = {50};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        int result = instance.getPercentage(new MemoryUsage(0L, 0L, 300000000L, 300000000L));
        assertEquals(0, result);
    }

    @Test
    public void percentage_100() throws Exception {
        int thresholds[] = {50};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        int result = instance.getPercentage(new MemoryUsage(0L, 300000000L, 300000000L, 300000000L));
        assertEquals(100, result);
    }

    @Test
    public void percentage_50() throws Exception {
        int thresholds[] = {50};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        int result = instance.getPercentage(new MemoryUsage(0L, 150000000L, 300000000L, 300000000L));
        assertEquals(50, result);
    }


    @Test
    public void logMemoryUsage_info() throws Exception {
        int thresholds[] = {50};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 140000000L, 300000000L, 300000000L));
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void logMemoryUsage_first_warn() throws Exception {
        int thresholds[] = {50, 70, 90};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 155000000L, 300000000L, 300000000L));
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void logMemoryUsage_info_then_first_warn() throws Exception {
        int thresholds[] = {50, 70, 90};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 140000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 155000000L, 300000000L, 300000000L));
        assertEquals(2, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(1)).getLevel());
    }

    @Test
    public void logMemoryUsage_second_warn() throws Exception {
        int thresholds[] = {50, 70, 90};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 220000000L, 300000000L, 300000000L));
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void logMemoryUsage_info_then_first_then_second_warn() throws Exception {
        int thresholds[] = {50, 70, 90};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 140000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 155000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 220000000L, 300000000L, 300000000L));
        assertEquals(3, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(1)).getLevel());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(2)).getLevel());
    }

    @Test
    public void logMemoryUsage_error() throws Exception {
        int thresholds[] = {50, 70, 90};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 280000000L, 300000000L, 300000000L));
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void logMemoryUsage_info_then_warn_then_error() throws Exception {
        int thresholds[] = {50, 70, 90};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 140000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 220000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 280000000L, 300000000L, 300000000L));
        assertEquals(3, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(1)).getLevel());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(2)).getLevel());
    }

    @Test
    public void logMemoryUsage_thresholds_null() throws Exception {
        MemoryMonitor instance = new MemoryMonitor(null);
        instance.logMemoryUsage(new MemoryUsage(0L, 140000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 220000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 280000000L, 300000000L, 300000000L));
        assertEquals(3, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(1)).getLevel());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(2)).getLevel());
    }

    @Test
    public void logMemoryUsage_thresholds_empty() throws Exception {
        int thresholds[] = {};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 140000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 220000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 280000000L, 300000000L, 300000000L));
        assertEquals(3, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(1)).getLevel());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(2)).getLevel());
    }

    @Test
    public void logMemoryUsage_thresholds_default() throws Exception {
        int thresholds[] = {};
        MemoryMonitor instance = new MemoryMonitor(thresholds);
        instance.logMemoryUsage(new MemoryUsage(0L, 140000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 220000000L, 300000000L, 300000000L));
        instance.logMemoryUsage(new MemoryUsage(0L, 280000000L, 300000000L, 300000000L));
        assertEquals(3, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(1)).getLevel());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(2)).getLevel());
    }

    @Test
    public void testAddAction() throws Exception {
        MemoryMonitor instance = new MemoryMonitor();
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void testTickAction() throws Exception {
        MemoryMonitor instance = new MemoryMonitor();
        instance.execute((TickTrigger) null, new Date());
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        events.add(event);
    }
}
