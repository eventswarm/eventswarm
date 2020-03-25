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
 * EventSet.java
 *
 * Created on 30 April 2007, 00:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.eventset;

import com.eventswarm.abstractions.DuplicateAbstractionException;
import com.eventswarm.abstractions.Abstraction;
import com.eventswarm.abstractions.IncrementalAbstraction;
import com.eventswarm.abstractions.MutableAbstraction;
import com.eventswarm.*;
import com.eventswarm.events.*;
import java.util.*;
import java.util.concurrent.locks.*;

import org.apache.log4j.Logger;

/**
 * An EventSet is the abstraction we use for managing events and persistence.
 *
 * The EventSet provides a basis for manipulating sets of events, defining
 * abstractions over the set, processing the events in the set and evaluating 
 * expressions against the set.
 *
 * @author andyb
 */
public class EventSet implements MutablePassThru, Iterable<Event>, Clear {
    
    // default logger
    private static Logger log = Logger.getLogger(EventSet.class);
    
    private Long id = null;
    protected TreeSet<Event> eventSet;
    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    // time bomb
    //private static long expiry = new GregorianCalendar(2014, 06, 30).getTime().getTime();

    /**
     * Set of all abstractions.  Use Map so we can retrieve equivalent
     * abstractions that have already been created. 
     */
    private HashMap<Abstraction,Abstraction> abstractions;
    
    /** Set of static (not incremental) abstractions */
    private Set<Abstraction> staticAbstractions;
    
    /** Set of singleton abstractions */
    private HashMap<Class<?>,Abstraction> singletonAbstractions;
    
    /** Set of AddEventTrigger listeners (includes incremental abstractions) */
    private Set<AddEventAction> addActions;
    
    /** Set of RemoveEventTrigger listeners */
    private Set<RemoveEventAction> removeActions;
    
    /** Creates a new instance of EventSet */
    public EventSet() {
        this.eventSet = new TreeSet<Event>();
        initialise();
    }
    
    
    /** Creates a new instance of EventSet with the specified events 
     * 
     * Note that the provided set is copied (not the elements, just the set) so
     * we can apply an internal structure. 
     */
    public EventSet(Set<Event> eventSet) {
        this.eventSet = new TreeSet<Event>(eventSet);
        initialise();
    }

    private void initialise() {
        this.abstractions = new HashMap<Abstraction,Abstraction>();
        this.staticAbstractions = new HashSet<Abstraction>();
        this.singletonAbstractions = new HashMap<Class<?>,Abstraction>();
        this.addActions = new HashSet<AddEventAction>();
        this.removeActions = new HashSet<RemoveEventAction>();
    }

    /** Get private identifier for persistence */
    private Long getId() {return this.id;}
    
    /** Set private identifier for persistence */    
    private void setId(Long id) {this.id = id;}
    
    /** Return a java.util.Set representation of this EventSet 
     *
     * This method creates a new set to avoid concurrency issues, so can be 
     * somewhat expensive.  Use other methods if you can.
     */
    public NavigableSet<Event> getEventSet() {
        NavigableSet<Event> set;
        lock.readLock().lock();
        try {
            set = new TreeSet<Event>(this.eventSet);
        } finally {
            lock.readLock().unlock();
        }
        return set;
    }
    
    /** Method to initialise the event set for load from persistent store */
    private void setEventSet (Set<Event> eventSet) {
        lock.writeLock().lock();
        try {
            this.eventSet = new TreeSet<Event>(eventSet);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /** 
     * Add an event to the event set, updating registered abstractions 
     * and notifying downstream EventActions.
     * 
     * This method is deprecated in favour of the AddEventAction.execute method, 
     * which allows an EventSet to be easily chained with channels and other 
     * event producing/consuming components.
     */
    @Deprecated
    public void add(Event event) {
        this.addEvent(event);
    }
    
    /**
     * Protected method to allow child classes to directly add events to the set.
     * 
     * We would prefer to have called this just "add" but the deprecated public
     * method makes it impossible to do so without breaking old stuff.
     * 
     * @param event
     */
    protected void addEvent(Event event) {
//        if (System.currentTimeMillis() > expiry) {
//            throw new RuntimeException(JdoHeader.BAD_LICENSE);
//        }
        log.debug("Locking for add");
        lock.writeLock().lock();
        try {
            // need to check for event existence before updating registered
            // abstractions
            if (!this.eventSet.contains(event)) {
                this.eventSet.add(event);
                // invalidate all of the static abstractions (we build on-demand)
                for (Abstraction abs : this.staticAbstractions) {
                    abs.setCurrent(false);
                }
                staticAbstractions.clear();

                // update all of the incremental abstractions, need to use iterator because of
                // possibility of re-entering
                Iterator<AddEventAction> iter = this.addActions.iterator();
                AddEventAction action;
                while (iter.hasNext()) {
                    action = iter.next();
                    log.debug("Executing registered action:" + action.toString());
                    action.execute(this, event);
                }
            } else {
                log.debug("Duplicate detected, ignoring");
            }
        } finally {
            lock.writeLock().unlock();
            log.debug("Unlocked after add");
        }
    }
    
    /** Method for removing events from the set
     *
     * Removal of events from an eventset has performance and scalability 
     * implications for persistent eventsets, so should be used carefully, 
     * hence this is a protected method.  For upstream event sources, 
     * the public RemoveEventAction.execute() method should be used instead 
     * of this method.
     * 
     * Event consumers (abstractions etc) that implement the RemoveEventAction 
     * will be notified of this event removal.  Note that abstractions that 
     * do not implement a RemoveEventAction risk invalidating their state when 
     * events are removed from the underlying EventSet.
     * 
     * @param event Event to remove. If not in the set, no action is taken.
     */
    protected void remove(Event event) {
        // ignore events that aren't in the set
        if (this.contains(event)) {
            log.debug("Locking for remove");
            lock.writeLock().lock();
            try {
                this.eventSet.remove(event);
            } finally {
                lock.writeLock().unlock();
                log.debug("Unlocked after remove");
            }
            // TODO: for consistency, should we put these actions inside the write lock?
            // notify any RemoveEventTrigger listeners, need to use iterator because of possibility of re-entering
            Iterator<RemoveEventAction> iter = this.removeActions.iterator();
            RemoveEventAction action;
            while (iter.hasNext()) {
                action = iter.next();
                action.execute(this, event);
            }
        }
    }
    
    public boolean contains(Event event) {
        boolean contains;
        lock.readLock().lock();
        try {
            contains = this.eventSet.contains(event);
        } finally {
            lock.readLock().unlock();
        }
        return contains;
    }
    
    public int size() {
        int size;
        log.debug("Locking to read size");
        lock.readLock().lock();
        try {
            size = this.eventSet.size();
        } finally {
            lock.readLock().unlock();
            log.debug("Unlocked after reading size");
        }
        return size;
    }
    
    /**
     * Retrieve an abstraction having the nominated abstraction class.
     *
     * If an abstraction of the nominated class already exists and is shareable, 
     * then that abstraction will be returned.  Otherwise, the EventSet will create
     * and initialise a new instance of the nominated abstraction class.  
     * 
     * Note that the nominated class must have a default (no argument) 
     * constructor.  Abstractions that do not have such a constructor should be 
     * created by the caller and registered using the registerAbstraction method.
     *
     * The returned abstraction will be up-to-date
     *
     * @return up-to-date abstraction of the nominated class
     * @throws ClassCastException if the class is not an abstraction
     * @throws InstantiationException if the class does not have a default constructor
     * @throws IllegalAccessException if the default constructor is not accessible
     */
    public Abstraction getAbstraction(Class<?> clazz) 
        throws ClassCastException, InstantiationException, IllegalAccessException 
    {
        Abstraction abs;
        // TODO: review this read -> write locking strategy, not sure if it's kosher
        // check for an existing, sharable singleton abstraction
        lock.readLock().lock();
        if (this.singletonAbstractions.containsKey(clazz)) {
            abs = singletonAbstractions.get(clazz);
            if (!abs.isCurrent()) {
                abs.buildAbstraction(this);
            }
            lock.readLock().unlock();
        } else {
            lock.readLock().unlock();
            // Don't have one we can use, so create one
            abs = (Abstraction) clazz.newInstance();
            lock.writeLock().lock();
            try {
                abstractions.put(abs, abs);
                addToSet(abs);
                if (abs.shareable()) {
                    // if shareable and not parameterised, then this is a singleton
                    singletonAbstractions.put(clazz, abs);
                }
            } finally {
                lock.writeLock().unlock();
            }
            
            // get the abstraction up-to-date
            lock.readLock().lock();
            try {
                abs.buildAbstraction(this);
            } finally {
                lock.readLock().unlock();
            }
        }
        return abs;
    }
    
    /** Register an abstraction.
     * 
     * @param newAbs Abstraction object initialised with any parameters
     * @return An up-to-date abstraction.  The returned abstraction 
     * will be either a built version of <code>abs</code>, or an equivalent 
     * abstraction that was previously registered.  Callers should <em>not</em>
     * assume that the <code>abs</abs> parameter will contain a valid 
     * abstraction on return.
     */
    public Abstraction registerAbstraction (Abstraction newAbs) 
            throws DuplicateAbstractionException 
    {
        // Check whether we already have an equivalent abstraction that is shareable
        // Note that the abstractions hash uses the 'hashCode' method to determine
        // equivalence
        // TODO: reconsider this read -> write locking strategy and perhaps just use a write lock
        lock.readLock().lock();
        Abstraction abs = abstractions.get(newAbs);
        if (abs != null && abs.shareable()) {
            // have an equivalent, shareable one, so use it
            log.debug("Re-using existing abstraction");
            lock.readLock().unlock();
        } else {
            lock.readLock().unlock();
            if (newAbs.equals(abs)) {
                throw new DuplicateAbstractionException();
            }
            log.debug("Registering new abstraction");
            // no equivalent, so register the supplied abstraction
            abs = newAbs;
            lock.writeLock().lock();
            try {
                abstractions.put(abs,abs);
                addToSet(abs);
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        // make sure it's up to date before returning
        lock.readLock().lock();
        try {
            if (!abs.isCurrent()) {
                abs.buildAbstraction(this);
            }
        } finally {
            lock.readLock().unlock();
        }
        return abs;
    }
    
    /** Return an iterator ordered by the natural order of events (compareTo). 
     * 
     * This method takes a copy of the underlying set of events before creating
     * the iterator (i.e. a snapshot).  As such, it will be expensive for large
     * EventSets and should be used with care.  Building the required state or
     * condition using an Abstraction is preferred.  
     */
    public Iterator<Event> iterator() {
        Iterator<Event> iter;
        log.debug("Locking to clone for iteration");
        lock.readLock().lock();
        try {
            iter = (new TreeSet<Event>(this.eventSet)).iterator();
        } finally {
            lock.readLock().unlock();
            log.debug("Unlocked after clone for iteration");
        }
        return iter;
    }

    /** Add abstraction to right set (incremental or static) */
    private void addToSet(Abstraction abs) {
        // test using a class cast
        try {
            // if this is incremental, register the fact so we can update it
            IncrementalAbstraction incr = (IncrementalAbstraction) abs;
            this.addActions.add(incr);
        } catch (ClassCastException ex) {
            // not incremental, so must be static
            this.staticAbstractions.add(abs);
        }
        
        // if this abstraction is mutable, then add the remove action
        if (MutableAbstraction.class.isInstance(abs)) {
            this.removeActions.add((RemoveEventAction) abs);
        }
    }

    /**
     * Empty all events from this eventset, notifying downstream listeners for
     * each event removed.
     * 
     * For large eventsets, this method can have significant performance and
     * other impacts, so should be used with some care. In particular, the
     * eventset is locked for the entire duration, including notifications
     * to downstream listeners.
     */
    public void clear() {
        Iterator<Event> iter = this.eventSet.iterator();
        lock.writeLock().lock();
        try {
            while (iter.hasNext()) {
                Event event = iter.next();
                iter.remove();
                for (RemoveEventAction action : this.removeActions) {
                    action.execute(this, event);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Reset this eventset so it can be re-used by another component
     *
     * This method assumes that the EventSet is finished with, so doesn't take any locks and doesn't notify
     * downstream components. All registrations and abstractions are removed by this method.
     *
     * @return
     */
    public void reset() {
        this.abstractions.clear();
        this.staticAbstractions.clear();
        this.singletonAbstractions.clear();
        this.addActions.clear();
        this.removeActions.clear();
        this.eventSet.clear();
    }

    public Event first() {
        return this.eventSet.first();
    }

    public Event last() {
        return this.eventSet.last();
    }

    public boolean isEmpty() {
        return this.eventSet.isEmpty();
    }

    public void execute(AddEventTrigger trigger, Event event) {
        this.addEvent(event);
    }

    public void execute(RemoveEventTrigger trigger, Event event) {
        this.remove(event);
    }

    public void registerAction(AddEventAction action) {
        log.debug("Registering action: " + action.toString() + " with hashcode " + Integer.toString(action.hashCode()));
        try {
            lock.writeLock().lock();
            if (!this.addActions.add(action)) {
                log.warn("Action already registered");
            }
            log.debug("Now have " + Integer.toString(addActions.size()) + " registered actions");
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unregisterAction(AddEventAction action) {
        try {
            lock.writeLock().lock();
            this.addActions.remove(action);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void registerAction(RemoveEventAction action) {
        try {
            lock.writeLock().lock();
            this.removeActions.add(action);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void unregisterAction(RemoveEventAction action) {
        try {
            lock.writeLock().lock();
            this.removeActions.remove(action);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
