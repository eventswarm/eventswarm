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
import com.eventswarm.powerset.ActionExecutor;
import com.eventswarm.powerset.ThreadedActionExecutor;
import com.eventswarm.util.TriggerDelegate;
import com.eventswarm.util.actions.QueuedAdd;
import com.eventswarm.util.actions.QueuedRemove;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Use a ThreadedActionExecutor to execute actions registered against add and remove triggers in threads,
 * serialising by target (i.e. the target of the action, for example, a downstream expression).
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ThreadingPassThru implements MutablePassThru {
    ThreadedActionExecutor executor;
    ExecutorService executorService;
    TriggerDelegate<AddEventAction> adds;
    TriggerDelegate<RemoveEventAction> removes;
    int threads;

    private static Logger logger = Logger.getLogger(ThreadingPassThru.class);

    /**
     * Create a ThreadingPassThru utilising the specified number of threads, creating a ThreadedActionExecutor with
     * that number of threads for processing actions.
     *
     * @param threads
     */
    public ThreadingPassThru(int threads) {
        this.threads = threads;
        this.adds = new TriggerDelegate<AddEventAction>();
        this.removes = new TriggerDelegate<RemoveEventAction>();
        this.executorService = Executors.newFixedThreadPool(threads);
        this.executor = new ThreadedActionExecutor(executorService);
    }

    /**
     * Create a ThreadingPassThru utilising the specified ThreadedActionExecutor
     *
     * The ThreadedActionExecutor can be used across multiple instances of the ThreadingPassThru, allowing threaded,
     * serialised actions on components that consume multiple incoming streams.
     *
     * @param executor ActionExecutor to use for threading
     */
    public ThreadingPassThru(ThreadedActionExecutor executor) {
        this.adds = new TriggerDelegate<AddEventAction>();
        this.removes = new TriggerDelegate<RemoveEventAction>();
        this.executor = executor;
    }

    /**
     * Stop the executor service associated with this ThreadingPassThru
     *
     * Use with care, since this will cause any further adds and removes to be discarded and will also ignore
     * tasks queued with the executor
     */
    public void stop() {
        logger.info("Stopping executor");
        executor.stop();
    }

    /**
     * When this class is garbage collected, shut down the executor
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        this.stop();
        super.finalize();
    }

    /**
     * When an add is received, queue adds for each registered add action
     *
     * Adds will be ignored if the underlying executor has been stopped
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        if (!executor.isStopped()) {
            for(AddEventAction action : adds) {
                executor.add(new QueuedAdd(action, trigger, event));
            }
        }
    }


    /**
     * When a remove is received, queue removes for each registered remove action
     *
     * Removes will be will be ignored if the underlying executor has been stopped
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (!executor.isStopped()) {
            for(RemoveEventAction action : removes) {
                executor.add(new QueuedRemove(action, trigger, event));
            }
        }
    }

    @Override
    public void registerAction(AddEventAction action) {
        adds.registerAction(action);
    }

    @Override
    public void unregisterAction(AddEventAction action) {
        adds.unregisterAction(action);
    }

    @Override
    public void registerAction(RemoveEventAction action) {
        removes.registerAction(action);
    }

    @Override
    public void unregisterAction(RemoveEventAction action) {
        removes.unregisterAction(action);
    }
}
