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

package com.eventswarm.util;

/**
 * Simple implementation of the AddEventTrigger interface suitable for use as a delegate. 
 * 
 * Classes that implement EventTriggers can delegate their implementations of the 
 * registration and unregistration methods to an instance of this class. 
 * 
 * Note that the method for firing the trigger is public, so any caller with a 
 * reference to the AddEventTrigger can fairly simply 
 * 
 * @author andyb
 */
import com.eventswarm.AddEventTrigger;
import com.eventswarm.AddEventAction;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;

public class EventTriggerDelegate<T extends AddEventTrigger, A extends AddEventAction>
{

    private Set<A> actions;
    private T delegator;
    private Logger logger = Logger.getLogger(EventTriggerDelegate.class);

    /**
     * Create a new TriggerDelegate instance with the identified object as the 
     * AddEventTrigger source (typically the delagating object).
     *  
     * @param delegator
     */
    public EventTriggerDelegate(T delegator) {
        this.actions = new HashSet<A>();
        this.delegator = delegator;
    }

    /**
     * Register an action against the AddEventTrigger.
     * 
     * @param action
     */
    public void registerAction(A action) {
        actions.add(action);
    }

    /** 
     * Unregister an action previously registered.
     * 
     * @param action
     */
    public void unregisterAction(A action) {
        actions.remove(action);
    }
    
    /**
     * Fire the trigger, calling all registered actions.
     */
    public void fire(Event event) {
        for (A action : actions) {
            logger.debug("Calling action " + action.toString());
            action.execute(this.delegator, event);
            logger.debug("Action completed");
        }
    }
}
