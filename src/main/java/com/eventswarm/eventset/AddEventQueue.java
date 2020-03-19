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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.PassThru;
import com.eventswarm.events.Event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

/**
 * Simple queue that queues event adds for subsequent processing in a thread provided by an ExecutorService
 *
 * Triggers are not maintained, so the trigger chain is broken by this queue.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class AddEventQueue extends EventQueue implements PassThru {
    private Set<AddEventAction> actions;

    /**
     * Create an AddEventQueue with the supplied ExecutorService
     *
     * @see EventQueue
     *
     * @param executor
     */
    public AddEventQueue(ExecutorService executor) {
        super(executor);
        setup();
    }

    /**
     * Create an AddEventQueue with a new single thread ExecutorService
     *
     * @see EventQueue
     */
    public AddEventQueue() {
        super();
        setup();
    }

    private void setup() {
        actions = new CopyOnWriteArraySet<AddEventAction>();
    }

    /**
     * New events get added to the queue
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        add(event);
    }

    /**
     * For each event in the queue, call all registered actions
     *
     * @param event
     */
    @Override
    protected void process(Event event) {
        for (AddEventAction action: actions) {
            action.execute(this, event);
        }
    }

    public void unregisterAction(AddEventAction action) {
        actions.remove(action);
    }

    public void registerAction(AddEventAction action) {
        actions.add(action);
    }

    public void reset() {
        this.clear();
        this.actions.clear();
    }
}
