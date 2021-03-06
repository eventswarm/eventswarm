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
package com.eventswarm.util.actions;

import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;

/**
 * Class for a queued RemoveEventAction
 * 
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class QueuedRemove implements QueuedAction {
    private RemoveEventAction action;
    private RemoveEventTrigger trigger;
    private Event event;

    /**
     * Constructor for queued RemoveEventAction instances
     *
     * @param action
     * @param trigger
     * @param event
     */
    public QueuedRemove(RemoveEventAction action, RemoveEventTrigger trigger, Event event) {
        this.action = action;
        this.trigger = trigger;
        this.event = event;
    }

    /**
     * @return the object acted on by this remove action
     */
    public Object getTarget() {
        return action;
    }

    public void run() {
        action.execute(trigger, event);
    }
}
