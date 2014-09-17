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

import com.eventswarm.events.Action;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.powerset.PowersetAddEventAction;
import com.eventswarm.powerset.PowersetAddEventTrigger;
import com.eventswarm.events.Event;

/**
 * Class for a queued PowersetAddEventAction
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class QueuedPowersetAdd implements QueuedAction {
    private PowersetAddEventAction action;
    private PowersetAddEventTrigger trigger;
    private EventSet target;
    private Event event;

    /**
     * Constructor for queued PowersetAddEventAction instances
     *
     * @param action
     * @param trigger
     * @param event
     */
    public QueuedPowersetAdd(PowersetAddEventAction action, PowersetAddEventTrigger trigger, EventSet target, Event event) {
        this.action = action;
        this.trigger = trigger;
        this.target = target;
        this.event = event;
    }

    /**
     * For a PowersetAdd method, the target is the eventset that has received the event.
     *
     * @return the eventset that has received the event
     */
    @Override
    public Object getTarget() {
        return target;
    }

    public void run() {
        action.execute(trigger, target, event);
    }
}
