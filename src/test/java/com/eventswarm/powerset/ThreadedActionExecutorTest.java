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
package com.eventswarm.powerset;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.MutableTarget;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import static com.eventswarm.events.jdo.TestEvents.*;

import com.eventswarm.eventset.EventSet;
import com.eventswarm.util.actions.QueuedAdd;
import com.eventswarm.util.actions.QueuedPowersetAdd;
import com.eventswarm.util.actions.QueuedPowersetRemove;
import com.eventswarm.util.actions.QueuedRemove;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ThreadedActionExecutorTest  {

    private ArrayList<Event> added, removed, padded, premoved;
    private Target target;

    @Before
    public void setUp() throws Exception {
        added = new ArrayList<Event>();
        removed = new ArrayList<Event>();
        padded = new ArrayList<Event>();
        premoved = new ArrayList<Event>();
        target = new Target();
    }

    @Test
    public void addEvent_one() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        QueuedAdd action = new QueuedAdd(target, (AddEventTrigger) null, jdoEvent);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action);
        synchronized(added) {
            if (target.count < 1) added.wait();
        }
        assertEquals(jdoEvent, added.get(0));
        assertEquals(1, added.size());
    }

    @Test
    public void addEvent_two_single_thread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        QueuedAdd action1 = new QueuedAdd(target, (AddEventTrigger) null, jdoEvent);
        QueuedAdd action2 = new QueuedAdd(target, (AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(added) {
            while (target.count < 2) added.wait();
        }
        assertEquals(jdoEvent, added.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, added.get(1));
        assertEquals(2, added.size());
    }

    @Test
    public void addEvent_two_multi_thread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        QueuedAdd action1 = new QueuedAdd(target, (AddEventTrigger) null, jdoEvent);
        QueuedAdd action2 = new QueuedAdd(target, (AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(added) {
            while (target.count < 2) added.wait();
        }
        assertEquals(jdoEvent, added.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, added.get(1));
        assertEquals(2, added.size());
    }

    @Test
    public void removeEvent_one() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        QueuedRemove action = new QueuedRemove(target, (RemoveEventTrigger) null, jdoEvent);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action);
        synchronized(removed) {
            if (target.count < 1) removed.wait();
        }
        assertEquals(jdoEvent, removed.get(0));
        assertEquals(1, removed.size());
    }

    @Test
    public void removeEvent_two_single_thread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        QueuedRemove action1 = new QueuedRemove(target, (RemoveEventTrigger) null, jdoEvent);
        QueuedRemove action2 = new QueuedRemove(target, (RemoveEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(removed) {
            while (target.count < 2) removed.wait();
        }
        assertEquals(jdoEvent, removed.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, removed.get(1));
        assertEquals(2, removed.size());
    }

    @Test
    public void removeEvent_two_multi_thread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        QueuedRemove action1 = new QueuedRemove(target, (RemoveEventTrigger) null, jdoEvent);
        QueuedRemove action2 = new QueuedRemove(target, (RemoveEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(removed) {
            while (target.count < 2) removed.wait();
        }
        assertEquals(jdoEvent, removed.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, removed.get(1));
        assertEquals(2, removed.size());
    }

    @Test
    public void powersetRemoveEvent_one() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        EventSet es = new EventSet();
        QueuedPowersetRemove action = new QueuedPowersetRemove(target, (PowersetRemoveEventTrigger) null, es, jdoEvent);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action);
        synchronized(premoved) {
            if (target.count < 1) premoved.wait();
        }
        assertEquals(jdoEvent, premoved.get(0));
        assertEquals(1, premoved.size());
    }

    @Test
    public void powersetRemoveEvent_two_single_thread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        EventSet es = new EventSet();
        QueuedPowersetRemove action1 = new QueuedPowersetRemove(target, (PowersetRemoveEventTrigger) null, es, jdoEvent);
        QueuedPowersetRemove action2 = new QueuedPowersetRemove(target, (PowersetRemoveEventTrigger) null, es, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(premoved) {
            while (target.count < 2) premoved.wait();
        }
        assertEquals(jdoEvent, premoved.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, premoved.get(1));
        assertEquals(2, premoved.size());
    }

    @Test
    public void powersetRemoveEvent_two_multi_thread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        EventSet es = new EventSet();
        QueuedPowersetRemove action1 = new QueuedPowersetRemove(target, (PowersetRemoveEventTrigger) null, es, jdoEvent);
        QueuedPowersetRemove action2 = new QueuedPowersetRemove(target, (PowersetRemoveEventTrigger) null, es, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(premoved) {
            while (target.count < 2) premoved.wait();
        }
        assertEquals(jdoEvent, premoved.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, premoved.get(1));
        assertEquals(2, premoved.size());
    }

    @Test
    public void powersetAddEvent_one() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        EventSet es = new EventSet();
        QueuedPowersetAdd action = new QueuedPowersetAdd(target, (PowersetAddEventTrigger) null, es, jdoEvent);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action);
        synchronized(padded) {
            if (target.count < 1) padded.wait();
        }
        assertEquals(jdoEvent, padded.get(0));
        assertEquals(1, padded.size());
    }

    @Test
    public void powersetAddEvent_two_single_thread() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        EventSet es = new EventSet();
        QueuedPowersetAdd action1 = new QueuedPowersetAdd(target, (PowersetAddEventTrigger) null, es, jdoEvent);
        QueuedPowersetAdd action2 = new QueuedPowersetAdd(target, (PowersetAddEventTrigger) null, es, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(padded) {
            while (target.count < 2) padded.wait();
        }
        assertEquals(jdoEvent, padded.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, padded.get(1));
        assertEquals(2, padded.size());
    }

    @Test
    public void powersetAddEvent_two_multi_thread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        EventSet es = new EventSet();
        QueuedPowersetAdd action1 = new QueuedPowersetAdd(target, (PowersetAddEventTrigger) null, es, jdoEvent);
        QueuedPowersetAdd action2 = new QueuedPowersetAdd(target, (PowersetAddEventTrigger) null, es, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        synchronized(padded) {
            while (target.count < 2) padded.wait();
        }
        assertEquals(jdoEvent, padded.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, padded.get(1));
        assertEquals(2, padded.size());
    }


    /**
     * Test to see if same target prevents concurrent execution
     *
     * @throws Exception
     */
    @Test
    public void addEvent_same_slow_target() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        SlowTarget target = new SlowTarget();
        QueuedAdd action1 = new QueuedAdd(target, (AddEventTrigger) null, jdoEvent);
        QueuedAdd action2 = new QueuedAdd(target, (AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        Thread.sleep(5);
        synchronized (target) {
            target.notify();
        }
        synchronized(added) {
            while (target.count < 2) added.wait();
        }
        assertEquals(jdoEvent, added.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, added.get(1));
        assertEquals(2, added.size());
    }


    /**
     * Test to see if different targets allow concurrent execution
     *
     * @throws Exception
     */
    @Test
    public void addEvent_different_slow_target() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        SlowTarget target1 = new SlowTarget();
        QueuedAdd action1 = new QueuedAdd(target1, (AddEventTrigger) null, jdoEvent);
        QueuedAdd action2 = new QueuedAdd(target, (AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        ThreadedActionExecutor instance = new ThreadedActionExecutor(executor);
        instance.add(action1);
        instance.add(action2);
        // action1 is blocked, wait for action2 to complete
        synchronized(added) {
            if (target.count < 1) added.wait();
        }
        // now unblock action1
        synchronized (target1) {
            target1.notify();
        }
        synchronized(added) {
            if (target1.count < 1) added.wait();
        }
        assertEquals(jdoEventAfterDiffSrcAfterSeq, added.get(0));
        assertEquals(jdoEvent, added.get(1));
        assertEquals(2, added.size());
    }

    //
    // Interface implementations so we can receive events from the executor
    //

    private class Target implements MutableTarget, PowersetAddEventAction, PowersetRemoveEventAction {
        int count = 0;

        public void execute(AddEventTrigger trigger, Event event) {
            added.add(event);
            count++;
            synchronized(added) {
                added.notify();
            }
        }

        public void execute(PowersetAddEventTrigger trigger, EventSet set, Event event) {
            padded.add(event);
            count++;
            synchronized(padded) {
                padded.notify();
            }
        }

        public void execute(PowersetRemoveEventTrigger trigger, EventSet set, Event event) {
            premoved.add(event);
            count++;
            synchronized(premoved) {
                premoved.notify();
            }
        }

        public void execute(RemoveEventTrigger trigger, Event event) {
            removed.add(event);
            count++;
            synchronized(removed) {
                removed.notify();
            }
        }
    }

    private class SlowTarget extends Target {
        @Override
        public void execute(AddEventTrigger trigger, Event event) {
            // block execution of first event until notified
            if (event == jdoEvent) {
                synchronized(this) {
                    System.out.println("Waiting in slow target");
                    try {
                        this.wait();
                        System.out.println("Have been notified, continuing ...");
                    } catch (Exception exc) {
                        System.err.println("Received exception while waiting: " + exc.getMessage());
                    }
                }
            }
            added.add(event);
            count++;
            synchronized (added) {
                added.notify();
            }
        }
    }
}
