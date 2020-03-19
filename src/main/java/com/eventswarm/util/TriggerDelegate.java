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
 * Simple implementation of trigger methods suitable for use as a delegate. 
 * 
 * Classes that implement triggers can delegate their implementations of the 
 * registration and unregistration methods to one or more instances of this 
 * class.
 * 
 * The class is parameterised by the action type <A>. 
 * 
 * @author andyb
 */
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class TriggerDelegate<A> implements Iterable<A>{

    private Set<A> actions;

    /**
     * Create a new TriggerDelegate instance.
     */
    public TriggerDelegate() {
        this.actions = new HashSet<A>();
    }

    /**
     * Register an action against the trigger.
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
     * Return an iterator for the set of registered actions
     * 
     * This iterator should be used by the caller with knowledge of the type <A>.
     * It would have been nicer to actually call the 'execute' methods of all
     * actions, but this would have required reflection because we can't do
     * it using generics.  Trying to avoid reflection because of performance
     * overheads.
     * 
     * @return iterator of type <A>
     */
    @Deprecated
    public Iterator<A> getActionIterator() {
        return(actions.iterator());
    }

    public Iterator<A> iterator() {
        return(actions.iterator());
    }
}
