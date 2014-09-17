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
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;

/**
 * A filter used for ensuring that certain actions are called before downstream processing of an add or remove event
 *
 * EventSwarm triggers do not guarantee ordering, so this class is required in situations where you need to ensure
 * that a housekeeping or other high priority action is called before other actions.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class PreProcessFilter extends MutablePassThruImpl {
    AddEventAction beforeAdd;
    RemoveEventAction beforeRemove;

    /**
     * Create a new PreProcessFilter that executes the supplied add/remove actions before passing the trigger onwards.
     *
     * Null actions are acceptable: no action will be executed for that trigger
     *
     * @param beforeAdd AddEventAction to call before passing each add onwards
     * @param beforeRemove RemoveEventAction to call before passing each remove onwards
     */
    public PreProcessFilter(AddEventAction beforeAdd, RemoveEventAction beforeRemove) {
        this.beforeAdd = beforeAdd;
        this.beforeRemove = beforeRemove;
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        if (beforeAdd != null) {
            beforeAdd.execute (trigger, event);
        }
        super.execute(trigger, event);
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (beforeRemove != null) {
            beforeRemove.execute (trigger, event);
        }
        super.execute(trigger, event);
    }
}
