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
 * AbstractFilter.java
 *
 * Created on 2 May 2007, 16:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.eventset;
import com.eventswarm.AbstractionAddAction;
import com.eventswarm.abstractions.IncrementalAbstraction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.*;
import java.util.*;

/** Abstract class implementing a basic EventSet filter.  
 * 
 * A filter is an EventSet containing a subset of the EventSet over which it 
 * abstracts.  Concrete classes should implement the <code>include</code> method 
 * to determine which events to include in the abstraction.
 *
 * Other abstractions can be registered against the filter in the same way as
 * they are registered against an EventSet.
 *
 * @author andyb
 */
public abstract class AbstractFilter extends EventSet implements IncrementalAbstraction {
    
    protected static final boolean SHAREABLE = true;
    
    protected boolean current;

    protected Set<AbstractionAddAction> actions;

    /** Creates a new instance of AbstractFilter */
    public AbstractFilter() {
        super();
        this.actions = new HashSet<AbstractionAddAction>();
    }
    
    /** Hide the ability to create one with an existing set of events */
    private AbstractFilter (Set<Event> events) {
        super(events);
        this.current = true;
        this.actions = new HashSet<AbstractionAddAction>();
    }
    
    /** 
     * Include a new event if it satisfies the include method 
     * 
     * This method is deprecated in favour of the execute method associated with
     * the Trigger/Action pattern.  We need to implement it to ensure
     * backward compatibility for existing applications.
     */
    @Deprecated
    public void handleEvent(Event event) {
        this.execute((AddEventTrigger) null, event);
    }
    
    /** 
     * Private method for processing events, including new events if they 
     * satisfy the (abstract) include method.
     */
    private void process(Event event) {
    }
    
    /** Build the filter incrementally */
    public void buildAbstraction (EventSet set) {
        Iterator<Event> iter = set.iterator();
        while (iter.hasNext()) {
            this.process(iter.next());
        }
        this.current = true;
    }
    
    /**
     * Abstract method defining a boolean function to determine if an event
     * should be included by the filter (if true, the event is added to the 
     * filter EventSet).
     */
    protected abstract boolean include(Event event);

    /**
     * Subclasses should provide a method to reset to an initial state so that this instance can be re-used.
     */
    public void reset() {
        this.actions.clear();
        super.reset();
    }
    
    /** Ignore this method call: incremental abstractions are always current 
     * except at startup.
     */
    public void setCurrent(boolean current) {
    
    }

    /** Default to shareable */
    public boolean shareable() {
        return SHAREABLE;
    }

    public boolean isCurrent() {
        return this.current;
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        this.lock.writeLock().lock();
        try {
            if (this.include(event)) {
                this.addEvent(event);
                // window has changed, so AbstractionAddTrigger listeners should be notified
                // TODO: decide whether the 'fire' should be inside the locked block
                this.fire(event);
            }
        } finally {
            this.lock.writeLock().unlock();
        }
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

    /**
     * Fire AbstractionAddAction actions
     *
     * @param event
     */
    private void fire(Event event) {
        for (AbstractionAddAction action : actions) {
            action.execute(this, event);
        }
    }
}
