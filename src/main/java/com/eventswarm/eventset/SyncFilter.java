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

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.AddSyncAction;
import com.eventswarm.AddSyncTrigger;
import java.util.HashSet;
import java.util.Set;

/**
 * MutablePassThru that provides an AddSyncTrigger so that events from other
 * sources can be synchronised with events flowing through this object.
 *
 * @author andyb
 */
public class SyncFilter extends MutablePassThruImpl
                           implements AddSyncTrigger
{
    private Set<AddSyncAction> actions = new HashSet<AddSyncAction>();

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        for (AddSyncAction action : this.actions) {
            action.execute(this, event);
        }
        super.execute(trigger, event);
    }

    public void registerAction(AddSyncAction action) {
        this.actions.add(action);
    }

    public void unregisterAction(AddSyncAction action) {
        this.actions.remove(action);
    }

    public void reset() {
        this.actions.clear();
    }
}
