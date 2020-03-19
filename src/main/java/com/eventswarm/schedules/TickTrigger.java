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

package com.eventswarm.schedules;

import com.eventswarm.Trigger;
import com.eventswarm.schedules.TickAction;

/**
 * Trigger that fires when an event that changes the "virtual" event time
 * clock has been received.
 *
 * This trigger is typically provided by a filtering abstraction or powerset so
 * that downstream processing abstractions can adjust discrete time windows or
 * other event time abstractions to reflect the time of the new event without
 * processing the event.
 * 
 * @author andyb
 */


public interface TickTrigger extends Trigger {

    public static Class<?> action = TickAction.class;
    
    /**
     * Register an action against this trigger.
     * 
     * Repeated registrations should be igored.
     * 
     * @param action Action to be executed when trigger fires.
     */
    public void registerAction(TickAction action);

    /**
     * Remove registration of the identified action against this trigger.
     * 
     * Ignores attempts to remove an action that is not registered.
     * 
     * @param action Action to be removed from registered list.
     */
    public void unregisterAction(TickAction action);

}
