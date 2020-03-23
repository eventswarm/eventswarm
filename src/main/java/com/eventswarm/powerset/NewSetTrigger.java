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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.powerset;

import com.eventswarm.Trigger;

/**
 * Trigger that fires when a new subset is created in a powerset.
 *
 * @author andyb
 */
public interface NewSetTrigger<Keytype> extends Trigger {

    // Note that the generic class will be returned.  Narrowing does not
    // (as far as I know) result in a new class.
    public static Class<?> action = NewSetAction.class;

    /**
     * Register an action against this trigger.
     *
     * Repeated registrations should be igored.
     *
     * @param action Action to be executed when trigger fires.
     */
    public void registerAction(NewSetAction<Keytype> action);

    /**
     * Remove registration of the identified action against this trigger.
     *
     * Ignores attempts to remove an action that is not registered.
     *
     * @param action Action to be removed from registered list.
     */
    public void unregisterAction(NewSetAction<Keytype> action);

}
