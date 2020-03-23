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

import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.powerset.PowersetRemoveEventAction;
import com.eventswarm.powerset.PowersetRemoveEventTrigger;

/**
 * Class for a queued PowersetRemoveEventAction
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class QueuedPowersetRemove implements QueuedAction {
    private PowersetRemoveEventAction action;
    private PowersetRemoveEventTrigger trigger;
    private EventSet target;
    private Event event;

    /**
     * Constructor for queued PowersetRemoveEventAction instances
     *
     * @param action
     * @param trigger
     * @param event
     */
    public QueuedPowersetRemove(PowersetRemoveEventAction action, PowersetRemoveEventTrigger trigger, EventSet target, Event event) {
        this.action = action;
        this.trigger = trigger;
        this.target = target;
        this.event = event;
    }

    /**
     * For a powersetRemove action, the target is the eventset from which the event will be removed.
     *
     * @return the eventset from which the event will be removed
     */
    public Object getTarget() {
        return target;
    }

    public void run() {
        action.execute(trigger, target, event);
    }
}
