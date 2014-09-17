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

import com.eventswarm.Action;
import com.eventswarm.eventset.EventSet;

/**
 * Action to be called by a Powerset when a NewSetTrigger fires.
 *
 * Powersets may remove subsets, typically if the subset is empty. Those
 * that wish to take some action when a subset is removed should register
 * aa RemoveSetAction against the associated trigger.
 *
 * @author andyb
 */

public interface RemoveSetAction<Keytype> extends Action {

    public static Class trigger = RemoveSetTrigger.class;

    /**
     * Action to execute when a new EventSet is added to the PowerSet.
     *
     * @param trigger Identifies the source of the trigger
     * @param es The EventSet that has been created
     * @param key The key associated with the EventSet
     */
    public void execute(RemoveSetTrigger<Keytype> trigger, EventSet es, Keytype key);

}
