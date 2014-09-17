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

import com.eventswarm.MutableTarget;
import com.eventswarm.util.actions.QueuedAction;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that executes actions against targets in threads provided by an ExecutorService, ensuring that at most one
 * action per target is executing at any time.
 *
 * This implementation uses a single queue of actions but skips locked actions when choosing the next action to
 * execute.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ThreadedActionExecutor implements ActionExecutor, Runnable {
    private LinkedList<QueuedAction> queue;
    private HashSet<Object> locked;
    private ReadWriteLock lock;
    private ExecutorService executor;
    private boolean stopped=false;

    private static final Logger logger= Logger.getLogger(ThreadedActionExecutor.class);

    public ThreadedActionExecutor(ExecutorService executor) {
        this.executor = executor;
        this.queue = new LinkedList<QueuedAction>();
        this.locked = new HashSet<Object>();
        this.lock = new ReentrantReadWriteLock();
    }

    /**
     * Add a new action to the queue, adding another job to the executor if the action is not blocked
     *
     * @param action
     */
    public void add(QueuedAction action) {
        try {
            lock.writeLock().lock();
            put(action);
            // if the new action is not blocked, submit a new job to the executor
            if (!blocked(action)) {
                executor.submit(this);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Process the next non-blocked action in the queue
     *
     * This method takes the next non-blocked action from the queue and runs it. Upon completion, a new task is added
     * to the queue if the queue is not empty. This can potentially lead to jobs that do nothing, but ensures that
     * other actions for the same target are always executed. The alternative is to search the targets list for actions
     * on the same target, which is likely to be less efficient.
     *
     * If the queue is empty or all actions are blocked, no processing occurs.
     */
    public void run() {
        QueuedAction action;
        try {
            // get the next action from the queue
            lock.writeLock().lock();
            action = take();
        } finally {
            lock.writeLock().unlock();
        }
        if (!stopped && action != null) {
            action.run();
            try {
                lock.writeLock().lock();
                finish(action);
                // add another job entry to the queue if there are any unblocked jobs
                if (peek() >= 0) {
                    executor.submit(this);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    /**
     * Stop this ThreadedActionExecutor and shutdown the underlying executor service
     */
    public void stop() {
        logger.info("Stopping new jobs and shutting down executor service");
        this.stopped = true;
        if (!this.executor.isShutdown()) {
            this.executor.shutdown();
        }
        logger.info("Executor stopped");
    }

    public boolean isStopped() {
        return this.stopped;
    }

    /**
     * Add a new action for the given target to the queue
     *
     * This method does not lock: it assumes the caller might want to take other actions in the lock context.
     *
     * @param action
     */
    private void put(QueuedAction action) {
        queue.add(action);
    }

    /**
     * Take the next non-blocked action from the queue or return null if there are no available actions, locking the
     * target of the action if found.
     *
     * Actions are blocked if their target already has a blocked action. This method does not lock: it assumes
     * the caller might want to take other actions within the same lock context.
     *
     * @return next non-blocked action or null if empty or all blocked
     */
    private QueuedAction take() {
        int index = peek();
        if (index >= 0) {
            QueuedAction action = queue.remove(index);
            locked.add(action.getTarget());
            return action;
        } else {
            return null;
        }
    }

    /**
     * Return the index of the next non-blocked action in the queue or -1 if no unblocked actions exist
     *
     * This method does not lock: it assumes the caller might want to take other actions within the same lock context.
     *
     * @return the index of the next non-blocked action in the queue or -1 if no unblocked actions exist
     */
    private int peek() {
        for (int i=0; i < queue.size(); i++) {
            if (!blocked(queue.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determine if there are any actions running for the target of this action
     *
     * This method does not lock: it assumes the caller might want to take other actions in the lock context.
     *
     * @param action
     * @return true if there are no actions running for the target of this action
     */
    private boolean blocked(QueuedAction action) {
        return locked.contains(action.getTarget());
    }

    /**
     * Update status to indicate that an action has completed, including unblocking the target of the action.
     *
     * This method does not lock: it assumes the caller might want to take other actions within the same lock context.
     *
     * @param action
     */
    private void finish(QueuedAction action) {
        locked.remove(action.getTarget());
    }
}
