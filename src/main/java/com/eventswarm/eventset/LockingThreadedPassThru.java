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

import com.eventswarm.*;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A PassThru that runs each downstream action in a thread provided by an executor service, with locking to ensure
 * per-action serializability.
 *
 * An executor can be shared across instances, allowing control over the total number of
 * threads used. This is appropriate for compute-bound actions, since best performance
 * usually occurs when number of threads == number of cores. For IO-bound actions, having roughly one thread
 * per downstream action might be more appropriate.
 *
 * This class could be used with a single thread to ensure serialized access to a resource used by many targets
 * (e.g. a non-thread-safe resource).
 *
 * Be aware that actions blocked by serialization use a thread, so slow actions can cause starvation by tying up
 * all of the available threads.
 *
 * This PassThru uses a factory to create re-usable runnable objects but does not constrain the number of
 * runnable instances, which could lead to excessive memory consumption if a blockage occurs somewhere in
 * downstream actions.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class LockingThreadedPassThru implements MutablePassThru {
    Map<AddEventAction,Lock> adds = new HashMap<AddEventAction,Lock>();
    Map<RemoveEventAction,Lock> removes = new HashMap<RemoveEventAction,Lock>();
    private AddRunnerFactory addFactory = new AddRunnerFactory();
    private RemoveRunnerFactory removeFactory = new RemoveRunnerFactory();

    private ExecutorService executor;
    private int threads;

    private static final Logger logger = Logger.getLogger(LockingThreadedPassThru.class);

    public LockingThreadedPassThru(ExecutorService executor) {
        this.threads = 0;
        this.executor = executor;
    }

    public LockingThreadedPassThru(int threads) {
        this.threads = threads;
        createExecutor();
    }

    private void createExecutor() {
        if (this.threads < 1) {
            logger.error("Attempt to create an ExecutorServices with no threads");
        } else {
            this.executor = Executors.newFixedThreadPool(threads);
        }
    }

    protected Lock getLock(AddEventAction action) {
        return adds.get(action);
    }

    protected Lock getLock(RemoveEventAction action) {
        return removes.get(action);
    }

    /**
     * Create a new runnable instance to execute the action with a lock
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        if (!this.executor.isShutdown()) {
            for (AddEventAction action: adds.keySet()) {
                executor.submit(addFactory.getInstance(action, adds.get(action), trigger, event));
            }
        }
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (!this.executor.isShutdown()) {
            for (RemoveEventAction action: removes.keySet()) {
                executor.submit(removeFactory.getInstance(action, removes.get(action), trigger, event));
            }
        }
    }


    @Override
    public void registerAction(AddEventAction action) {
        if (!this.adds.containsKey(action)) {
            this.adds.put(action, new ReentrantLock());
        }
    }

    @Override
    public void unregisterAction(AddEventAction action) {
        this.adds.remove(action);
    }

    @Override
    public void registerAction(RemoveEventAction action) {
        if (!this.removes.containsKey(action)) {
            this.removes.put(action, new ReentrantLock());
        }
    }

    @Override
    public void unregisterAction(RemoveEventAction action) {
        this.removes.remove(action);
    }

    /**
     * Stop the executor services associated with this instance, if one has been created for it.
     *
     * Note that this will not stop an ExecutorService that was passed into the constructor.
     */
    public void stop() {
        if (this.threads > 0 && !this.executor.isShutdown()) {
            this.executor.shutdown();
        } else if (this.threads == 0) {
            logger.warn("Ignoring attempt to stop an executor service that was not created here");
        }
    }

    /**
     * Restart the executor services associated with this instance, if one has been created for it.
     *
     * Note that this will not stop an ExecutorService that was passed into the constructor.
     */
    public void restart() {
        if (this.threads > 0 && this.executor.isTerminated()) {
            createExecutor();
        } else if (this.threads == 0) {
            logger.warn("Ignoring attempt to restart with zero threads");
        }
    }

    public class AddRunnerFactory {
        private ArrayList<AddRunner> instances = new ArrayList<AddRunner>();
        private Lock lock = new ReentrantLock();

        public AddRunner getInstance(AddEventAction action, Lock lock, AddEventTrigger trigger, Event event) {
            lock.lock();
            try {
                int index = instances.size() - 1;
                if (index >= 0) {
                    return(instances.get(index).init(action,lock,trigger,event));
                } else {
                    return new AddRunner(action,lock,trigger,event);
                }
            } finally {
                lock.unlock();
            }
        }

        public void releaseInstance(AddRunner instance) {
            lock.lock();
            try {
                instances.add(instance);
            } finally {
                lock.unlock();
            }
        }
    }

    public class RemoveRunnerFactory {
        private ArrayList<RemoveRunner> instances = new ArrayList<RemoveRunner>();
        private Lock lock = new ReentrantLock();

        public RemoveRunner getInstance(RemoveEventAction action, Lock lock, RemoveEventTrigger trigger, Event event) {
            lock.lock();
            try {
                int index = instances.size() - 1;
                if (index >= 0) {
                    return(instances.get(index).init(action,lock,trigger,event));
                } else {
                    return new RemoveRunner(action,lock,trigger,event);
                }
            } finally {
                lock.unlock();
            }
        }

        public void releaseInstance(RemoveRunner instance) {
            lock.lock();
            try {
                instances.add(instance);
            } finally {
                lock.unlock();
            }
        }

    }
    /**
     * Threaded wrapper around an add action with a lock for serialization
     */
    public static class AddRunner implements Runnable {
        private AddEventAction action;
        private Lock lock;
        private AddEventTrigger trigger;
        private Event event;

        public AddRunner(AddEventAction action, Lock lock, AddEventTrigger trigger, Event event) {
            init(action,lock,trigger,event);
        }

        public AddRunner init(AddEventAction action, Lock lock, AddEventTrigger trigger, Event event) {
            this.action = action;
            this.lock = lock;
            this.trigger = trigger;
            this.event = event;
            return this;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                action.execute(trigger, event);
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Threaded wrapper around a remove action with a lock for serialization
     */
    public static class RemoveRunner implements Runnable {
        private RemoveEventAction action;
        private Lock lock;
        private RemoveEventTrigger trigger;
        private Event event;

        public RemoveRunner(RemoveEventAction action, Lock lock, RemoveEventTrigger trigger, Event event) {
            init(action,lock,trigger,event);
        }

        public RemoveRunner init(RemoveEventAction action, Lock lock, RemoveEventTrigger trigger, Event event) {
            this.action = action;
            this.lock = lock;
            this.trigger = trigger;
            this.event = event;
            return this;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                action.execute(trigger, event);
            } finally {
                lock.unlock();
            }
        }
    }
}
