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
 * Filter to replay "old" events as if they were a real time event source by
 * adjusting their clocks to be consistent with another clock source and 
 * delivering them in an order that is consistent with that source.
 *
 * This filter requires an AddSyncTrigger that delivers a stream of events with
 * "correct" clocks that we can synchronise delivery against. The filter does
 * not support event removal, so needs to be attached to a source that does not
 * require event removal (e.g. a channel).
 *
 * The real time source and filter source must run in separate threads for this
 * to work, since the replay filter blocks events until they can be correctly 
 * delivered.
 *
 * It is possible that this class could be adapted to adjust event streams for
 * latency effects, for example, to delay local events until earlier remote events
 * are delivered. The rewriting of event headers would have to be removed or
 * disabled for this to work. 
 *
 * This class only works for Event objects whose header is based on JdoHeader
 * since we need to modify the header date field and the Event interface
 * doesn't allow this.
 * 
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import com.eventswarm.events.Event;
import org.apache.log4j.*;
import com.eventswarm.*;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.AddSyncAction;
import com.eventswarm.AddSyncTrigger;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

public class ReplayFilter extends PassThruImpl implements AddSyncAction
{

    public static int BUFFER_SIZE = 100;

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(ReplayFilter.class);  

    /** clock source */
    private AddSyncTrigger source;
    private Event firstSync = null;
    private TreeSet<Event> buffer;
    private long delta, now;
    private int buffer_size, count, total;
    private boolean finished = false;

    /**
     * Create a new ReplayFilter using the specified real-time AddSyncTrigger
     * source using default buffer size.
     * 
     * @param source
     */
    public ReplayFilter(AddSyncTrigger source) {
        super();
        setup(source, BUFFER_SIZE);
    }

    /**
     * Create a new ReplayFilter using the specified real-time AddSyncTrigger
     * source using specified buffer size.
     *
     * @param source
     */
    public ReplayFilter(AddSyncTrigger source, int buffer_size) {
        super();
        setup(source, buffer_size);
    }

    /**
     * Initialisation used for constructors
     */
    private void setup(AddSyncTrigger source, int buffer_size) {
        source.registerAction(this);
        this.total = 0;
        this.source = source;
        this.buffer_size = buffer_size;
        this.buffer = new TreeSet<Event>();
    }

    /**
     * When each replay event is delivered, modify it's clock and delay it until an
     * event with a larger clock is received from the sync source.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        synchronized(this) {
            // if our buffer is full, wait for the sync trigger
            if (buffer.size() >= this.buffer_size) {
                try {
                    this.wait();
                } catch (InterruptedException exc) {}
            }

            // add this event to the buffer, adjusting its clock if possible
            this.add(event);

            // if we've had our first sync event, clear as many events as we can from the buffer
            if (this.firstSync != null) {
                Event replay;
                while (!buffer.isEmpty() && getTime(buffer.first()) < this.now) {
                    replay = buffer.pollFirst();
                    super.execute(trigger, replay);
                    this.count++;
                    this.total++;
                    if (this.total%10000 == 0) {
                        log.info("Replay event timestamp for event " + Integer.toString(this.total) +
                                " is " + replay.getHeader().getTimestamp().toString());
                    }
                }

                // if not empty then notify the sync trigger so its event can
                // be released
                if (!this.buffer.isEmpty()) {
                    this.notify();
                }
            }
        }
    }

    /**
     *
     * @param trigger
     * @param event
     */
    public void execute(AddSyncTrigger trigger, Event event) {
        synchronized(this) {
            this.now = getTime(event);
            this.count = 0;

            if (this.firstSync == null) {
                // first sync event, calculate the delta and adjust timestamps
                // on buffered events
                this.firstSync = event;
                if (this.buffer.isEmpty()) {
                    try {
                        this.wait();
                    } catch (InterruptedException exc) {}
                }
                this.delta = this.now - getTime(buffer.first()) - 1;
                log.debug("First sync trigger");
                log.debug("First replay event time was " + buffer.first().getHeader().getTimestamp().toString());
                log.debug("Delta to now is " + Long.toString(delta));
                for (Event replayed : buffer) {
                    adjust(replayed, this.delta);
                }
            }

            // Wait until we've cleared all earlier events from the replay source
            log.debug("Replaying trade events before this tweet");
            while (!finished && (this.buffer.isEmpty() || this.now > getTime(this.buffer.first()))) {
                this.notify();
                try {
                    this.wait();
                } catch (InterruptedException exc) {}
            }
            log.debug("Replayed " + Integer.toString(this.count) + " events before this tweet");
        }
    }

    /**
     * Call this method to indicate that the replay has finished and allow
     * the sync trigger to exit if blocked, also unregistering from the source
     */
    public void finish() {
        synchronized(this) {
            this.finished = true;
            this.source.unregisterAction(this);
            this.notifyAll();
        }
    }

    private void add(Event event) {
        if (this.firstSync != null) {
            // if we have a sync event, adjust our timestamp using the delta
            adjust(event, this.delta);
        }
        this.buffer.add(event);
    }

    private void adjust(Event event, long delta) {
        JdoHeader toFix = (JdoHeader) event.getHeader();
        long newStamp = toFix.getTimestamp().getTime() + delta;
        toFix.setTimestamp(new Date(newStamp));
    }

    private long getTime(Event event) {
        return event.getHeader().getTimestamp().getTime();
    }
}
