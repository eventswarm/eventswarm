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
 * IncrementalAbstractionImpl.java
 *
 * Created on 2 May 2007, 14:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.abstractions;
import com.eventswarm.AbstractionAddAction;
import com.eventswarm.AbstractionAddTrigger;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.*;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.util.EventTriggerDelegate;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Abstract class for the IncrementalAbstraction interface.
 * 
 * This abstract class contains default method implementations for the 
 * Abstraction interface methods that are implied by the fact that the 
 * abstraction is incremental.  In most cases, you should be able to create a
 * concrete class by overriding <code>handleEvent</code> and adding the 
 * methods necessary for your abstraction.
 *
 * The implementation assumes that it is built/updated using concurrency control 
 * managed by the hosting EventSet.
 *
 * @author andyb
 */
public abstract class IncrementalAbstractionImpl implements IncrementalAbstraction {
    
    /**
     * Unless specifically required otherwise, incremental abstractions should be shareable.
     */
    protected static final boolean SHAREABLE = true;

    protected Set<AbstractionAddAction> actions;

    protected boolean current;

    private static Logger logger = Logger.getLogger(IncrementalAbstractionImpl.class);

    /** Creates a new instance of IncrementalAbstractionImpl */
    public IncrementalAbstractionImpl() {
        super();
        this.current = false;
        this.actions = new HashSet<AbstractionAddAction>();
    }

    /** Build the abstraction by iteratively calling handleEvent 
     *
     * This method is usually called only once by the EventSet immediately after 
     * registration.  If registered with the EventSet, subsequent calls are 
     * unnecessary because it will be updated incrementally.  If not registered
     * with the event set, this abstraction is unlikely to be accurate, ever!
     *
     * This method uses the iterator provided by EventSet, processing events in 
     * natural (approximately chronological) order.   
     */
    public void buildAbstraction(EventSet set) {
        Iterator<Event> iter = set.iterator();
        while (iter.hasNext()) {
            this.handleEvent(iter.next());
        }
        this.current = true;
    }

    /** Ignore this method call: incremental abstractions are always current 
     * except at startup.
     */
    public void setCurrent(boolean current) {
    
    }

    public boolean shareable() {
        return SHAREABLE;
    }

    public boolean isCurrent() {
        return this.current;
    }

    /**
     * Method for adding an event to an abstraction in response to an upstream
     * trigger.
     *
     * This method just fires the AbstractionAddTrigger, which assumes that the abstraction has been updated
     * by the addition of the event. If this is not the case, then the child class should override.
     * 
     * @param trigger Upstream trigger producing the event
     * @param event Event to be added
     */
    public void execute(AddEventTrigger trigger, Event event) {
        fire(event);
    }

    /** 
     * Deprecated method for adding an event to an abstraction.
     * 
     * Use the EventAction.execute method instead, but this method still needs
     * an implementation for backward compatibility.  A default implementation
     * is provided that calls the execute method with a null trigger source. 
     * This should be overridden if the trigger source is needed for execute.
     */
    @Deprecated
    public void handleEvent(Event event) {
        this.execute(null, event);
    }



    /**
     * Register an action against the AbstractionAddAction.
     *
     * @param action
     */
    public void registerAction(AbstractionAddAction action) {
        actions.add(action);
    }

    /**
     * Unregister an action previously registered.
     *
     * @param action
     */
    public void unregisterAction(AbstractionAddAction action) {
        actions.remove(action);
    }

    public void reset() {
        this.actions.clear();
    }

    /**
     * Fire the trigger, calling all registered actions.
     */
    protected void fire(Event event) {
        for (AbstractionAddAction action : actions) {
            logger.debug("Calling action " + action.toString());
            action.execute(this, event);
            logger.debug("Action completed");
        }
    }

}
