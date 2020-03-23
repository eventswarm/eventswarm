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
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.LogEvent;
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.eventswarm.events.jdo.TestEvents.*;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SizeMonitorTest implements AddEventAction {
    private Log4JChannel channel;
    private ArrayList<Event> events;
    private SizeMonitor instance;
    private EventSet es;

    @Before
    public void setUp() throws Exception {
        es = new EventSet();
        channel = new Log4JChannel();
        events = new ArrayList<Event>();
        Logger.getLogger(SizeMonitor.class).addAppender(channel);
        channel.registerAction(this);
    }

    @Test
    public void add_below_first() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest");
        es.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, es.size());
        assertEquals(0, events.size());
    }

    @Test
    public void add_at_first() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest");
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, es.size());
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
    }


    @Test
    public void add_below_second() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest");
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(3, es.size());
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void add_at_second() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest");
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        es.execute((AddEventTrigger) null, jdoEventAfterDiffSrcBeforeSeq);
        assertEquals(4, es.size());
        assertEquals(2, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(1)).getLevel());
    }

    @Test
    public void go_below_then_at_again() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest");
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((RemoveEventTrigger) null, jdoEventBeforeDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, es.size());
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void go_below_first_then_at_second() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest");
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((RemoveEventTrigger) null, jdoEventBeforeDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        es.execute((AddEventTrigger) null, jdoEventAfterDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(4, es.size());
        assertEquals(2, events.size());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.info, ((LogEvent) events.get(1)).getLevel());
    }

    @Test
    public void add_at_warn_threshold() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest", 2, 0);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, es.size());
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(0)).getLevel());
    }

    @Test
    public void add_at_error_threshold() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest", 0, 2);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, es.size());
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(0)).getLevel());
    }


    @Test
    public void add_at_warn_then_error_threshold() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest", 2, 4);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcBeforeSeq);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        es.execute((AddEventTrigger) null, jdoEventAfterDiffSrcBeforeSeq);
        assertEquals(4, es.size());
        assertEquals(2, events.size());
        assertEquals(LogEvent.Level.warn, ((LogEvent) events.get(0)).getLevel());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(1)).getLevel());
    }

    @Test
    public void same_warn_and_error_threshold() throws Exception {
        instance = new SizeMonitor(es, 2, "SizeMonitorTest", 2, 2);
        es.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        es.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, es.size());
        assertEquals(1, events.size());
        assertEquals(LogEvent.Level.error, ((LogEvent) events.get(0)).getLevel());
    }

    public void execute(AddEventTrigger trigger, Event event) {
        events.add(event);
    }
}
