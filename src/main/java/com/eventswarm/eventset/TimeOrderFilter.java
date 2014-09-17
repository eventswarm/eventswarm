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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Applies a fixed delay to events, ordering them by timestamp before passing
 * them onwards to downstream abstractions.
 * 
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import com.eventswarm.events.Event;
import org.apache.log4j.*;
import com.eventswarm.*;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.HashSet;
import com.eventswarm.util.DelayedEvent;

public class TimeOrderFilter 
        implements PassThru, OutOfOrderTrigger, Runnable
{

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(TimeOrderFilter.class);  

    /** filtering delay in nanoseconds */
    private long delay;
    
    /** DelayQueue for holding events */
    private DelayQueue<DelayedEvent> queue;
    
    /** last event delivered */
    Event last;
    
    private Set<AddEventAction> addActions = new HashSet<AddEventAction>();
    private Set<OutOfOrderAction> orderActions = new HashSet<OutOfOrderAction>();

    /**
     * Create a new TimeOrderFilter using the specified delay to ensure the 
     * order of events.
     * 
     * @param delay
     */
    public TimeOrderFilter(TimeUnit units, long delay) {
        this.delay = units.convert(delay, TimeUnit.NANOSECONDS);
        queue = new DelayQueue<DelayedEvent>();
        last = null;
    }
    
    /**
     * When each event is delivered, add it to a queue ordered by event timestamp
     * so it can be passed on to downstream clients in order.
     * 
     * Any out-of-order events are passed immediately onwards via by triggering
     * registered OutOfOrderActions.  Out-of-order events are those that are 
     * older than the most recently delivered event.
     * 
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        long time = event.getHeader().getTimestamp().getTime();
        if (this.queue.isEmpty()) {
            // just add the event
            this.queue.add(new DelayedEvent(TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS), event));
        }
        throw new RuntimeException("Incomplete implementation");
    }

    public void registerAction(AddEventAction action) {
        this.addActions.add(action);
    }

    public void unregisterAction(AddEventAction action) {
        this.addActions.remove(action);
    }

    public void registerAction(OutOfOrderAction action) {
        this.orderActions.add(action);
    }

    public void unregisterAction(OutOfOrderAction action) {
        this.orderActions.remove(action);
    }

    public void reset() {
        this.addActions.clear();
        this.orderActions.clear();
        this.queue.clear();
        this.last = null;
    }

    public void run() {
        DelayedEvent delayedEvent;
        while (true) {
            try {
                delayedEvent = this.queue.take();
            } catch (InterruptedException exc) {
                log.error(exc);
            }
        }        
    }
    
}
