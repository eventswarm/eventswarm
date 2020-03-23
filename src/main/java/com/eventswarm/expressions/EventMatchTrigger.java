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

package com.eventswarm.expressions;

import com.eventswarm.*;

/**
 * Trigger that fires when a new event causes an expression to be matched (true)
 *
 * For expressions matching on a single event, the matched event should be passed in actions as the catalyst.
 * For complex expressions potentially involving multiple events (e.g. sequence), the last (in time) event
 * in the set should be passed in actions as the catalyst. Where two events have equal time, the last event received
 * should be passed.
 *
 * @author andyb
 */


public interface EventMatchTrigger extends Trigger {

    public static Class<?> action = EventMatchAction.class;
    
    /**
     * Register an action against this trigger.
     * 
     * Repeated registrations should be igored.
     * 
     * @param action Action to be executed when trigger fires.
     */
    public void registerAction(EventMatchAction action);

    /**
     * Remove registration of the identified action against this trigger.
     * 
     * Ignores attempts to remove an action that is not registered.
     * 
     * @param action Action to be removed from registered list.
     */
    public void unregisterAction(EventMatchAction action);

}
