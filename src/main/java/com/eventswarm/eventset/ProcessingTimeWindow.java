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

import com.eventswarm.util.IntervalUnit;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import java.util.concurrent.DelayQueue;
import com.eventswarm.util.DelayedEvent;
import org.apache.log4j.*;
import java.util.Iterator;

/**
 *
 * @author zoki
 */
public class ProcessingTimeWindow extends AbstractTimeWindow implements Runnable {

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(ProcessingTimeWindow.class);

    /** thread for removing events as they go outside the processing window */
    private Thread remover;
    private DelayQueue<DelayedEvent> eventQueue;
    boolean filling;

    /**
     * Constructor for ProcessingTimeWindows with window size in seconds
     * 
     * @param WindowSize Size of window in seconds
     */
    public ProcessingTimeWindow(long windowSize) {
        super(windowSize);
    }

    /**
     * Constructor for WindowSize expressed using specific IntervaUnits
     * 
     * @param units Units for window size (i.e. SECONDS, MINUTES, HOURS, DAYS);
     * @param windowSize
     */
    public ProcessingTimeWindow(IntervalUnit units, long windowSize) {
        super(units, windowSize);
    }

    @Override
    protected void init() {
        this.eventQueue = new DelayQueue<DelayedEvent>();
        this.remover = new Thread(this);
        this.remover.start();
    }

    /**
     * Override the add event action to put the event into the DelayQueue so we
     * can remove events according to their expiry time.
     * 
     * The expiry time of an event is the current time plus the processing 
     * window size.  Millisecond accuracy is used for timers.
     * 
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        lock.writeLock().lock();
        try {
            // need to check for event existence before updating registered
            // abstractions
            if (!this.contains(event)) {
                this.eventQueue.add(new DelayedEvent(System.currentTimeMillis() + this.windowSize, event));
                super.execute(trigger, event);

                // execute any registered window change actions
                this.fire();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns true after the first event has been removed, indicating that the
     * time window is now full.  
     * 
     * @return
     */
    @Override
    public boolean isFilling() {
        return this.filling;
    }

    /**
     * Method to return the Delayed event at the head of DelayedQueue 
     * 
     * @return
     */
    public DelayedEvent peek() {
        return this.eventQueue.peek();
    }

    Iterator<DelayedEvent> queueIterator() {
        return this.eventQueue.iterator();
    }

    int queueSize() {
        return this.eventQueue.size();
    }

    boolean queueContains(DelayedEvent delayedEvent) {
        return this.eventQueue.contains(delayedEvent);
    }

    boolean queueContains(Event event) {
        boolean found = false;
        Iterator<DelayedEvent> iter = this.eventQueue.iterator();
        while (!found && iter.hasNext()) {
            found = iter.next().getEvent().equals(event);
        }
        return (found);
    }

    public void reset() {
        this.actions.clear();
        this.eventQueue.clear();
        super.reset();
    }
    /** 
     * Check consistency of queue and EventSet. This is primarily for testing 
     * purposes and is not thread safe
     * 
     * @return true if both collections contain the same events
     */
    boolean consistent() {
        if (this.size() != this.eventQueue.size()) {
            return false;
        }
        boolean consistent = true;
        Iterator<DelayedEvent> iter = this.eventQueue.iterator();
        while (consistent && iter.hasNext()) {
            consistent = this.contains(iter.next().getEvent());
        }
        return consistent;
    }

    /**
     * Thread method for removeing events as they fall out of wondow
     */
    public void run() {
        DelayedEvent delayedEvent;
        while (true) {
            try {
                delayedEvent = this.eventQueue.take();
                this.filling = false;
                this.remove(delayedEvent.getEvent());
                // call any registered WindowChangeActions
                this.fire();
            } catch (InterruptedException exc) {
                log.error(exc);
            }


        }
    }
}
