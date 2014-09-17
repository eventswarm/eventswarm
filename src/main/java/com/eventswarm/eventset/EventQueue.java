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

import com.eventswarm.Clear;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class implementing a queue for processing events in a thread, with subclasses specialising for
 * different actions (e.g. AddEventAction)
 *
 * This class uses a sorted set for events, meaning that an event can only be queued once. An thread provided by
 * an ExecutorService is used to empty the queue. Emptying is a 'run to completion' task, that is, all available
 * events are processed. Locks are released after each event is processed so that new events can be added safely
 * during this process.
 *
 * There is potential for this class to take a thread and keep it. We might consider a snapshot rather than a
 * 'run to completion' approach in future.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public abstract class EventQueue implements Runnable, Clear {
    protected NavigableSet<Event> queue;
    protected Lock lock;
    private ExecutorService executor;
    private boolean ownExecutor;

    private static Logger logger = Logger.getLogger(EventQueue.class);

    /**
     * Create an event queue that uses threads owned by the supplied ExecutorService to process events
     *
     * @param executor
     */
    protected EventQueue(ExecutorService executor) {
        this.executor = executor;
        this.lock = new ReentrantLock();
        this.queue = new TreeSet<Event>();
        this.ownExecutor = false;
    }

    /**
     * Create an event queue that uses a single thread executor to process events
     *
     * @param
     */
    protected EventQueue() {
        this(Executors.newSingleThreadExecutor());
        this.ownExecutor = true;
    }

    /**
     * Concrete implementations need to do something with an event (e.g. call an AddEventAction) with the event
     *
     * @param event
     */
    protected abstract void process(Event event);


    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Change the executor used by this queue, stopping the previous executor if it was 'owned' by this instance
     *
     * @param executor
     */
    public void setExecutor(ExecutorService executor) {
        this.stop();
        this.executor = executor;
        this.ownExecutor = false;
    }

    /**
     * Add an event to the queue and submit a job to the ExecutorService if the queue was previously empty
     *
     * Uses lock to ensure that only one thread can possibly submit the job to the executor
     */
    protected void add(Event event) {
        lock.lock();
        try {
            queue.add(event);
            if (queue.size() == 1) {
                logger.debug("Submitting job to clear queue");
                executor.submit(this);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * If a new executor has been created by this instance, shut it down
     *
     * This method does nothing if an external executor has been provided
     */
    public void stop() {
        if (ownExecutor) {
            executor.shutdown();
        }
    }

    public boolean isStopped() {
        return executor.isShutdown();
    }

    public void clear() {
        // empty the queue without processing
        lock.lock();
        try {
            queue.clear();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Iterate over all events in the queue, calling the process method for each but allowing new events to be
     * added while iterating
     *
     * A lock is used to avoid race conditions with adds, but the lock is released after each event is processed
     * to allow other events to be added with minimal wait.
     *
     * Postcondition: queue will be empty
     *
     */
    @Override
    public void run() {
        boolean more = true;
        int count = 0;
        Event event;
        logger.debug(Integer.toString(queue.size()) + " events in queue");
        while (more) {
            lock.lock();
            try {
                event = queue.pollFirst();
                if (event != null) {
                    process(event);
                    count++;
                } else {
                    more=false;
                }
            } finally {
                lock.unlock();
            }
        }
        logger.debug("Cleared " + Integer.toString(count) + " events from the queue");
    }

    @Override
    protected void finalize() throws Throwable {
        this.stop();
        super.finalize();
    }
}
