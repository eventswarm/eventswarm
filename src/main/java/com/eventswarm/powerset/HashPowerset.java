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

/**
 * This class implements a Powerset using a hashtable of key/eventset pairs.
 *
 * Upon arrival, new events are pushed into a single set based on the
 * extraction of a key value from the event.  If the key extraction fails then
 * the event is discarded, meaning the key extraction function can be used as
 * a filter if desired.  This extraction is performed by an EventKey
 * implementation provided by the creator.  The creator of a HashPowerset MUST
 * provide an implementation of the EventKey interface to extract keys of the
 * chosen key type from each event, otherwise no events will ever be added.
 *
 * If no set exists for the specified key, a MutablePassthru factory is called
 * to create a new subset.  The creator can provide a factory implementation
 * for creating EventSets, although a default implementation is provided.
 * This provides two control mechanisms: firstly, returning a null EventSet
 * indicates that this event should not result in a new subset (and will be
 * discarded); and secondly, allowing the use of time or size windows to
 * optimise memory usage and provide adequate retention of events for processing
 * purposes.  The default factory (internal) creates a regular eventset.
 *
 * A NewSetTrigger is provided so that a class can register a pipeline or
 * other Event-based action against each EventSet when it is created.  This
 * trigger is fired before any events are added to the new subset.
 *
 * A RemoveSetTrigger is provided so that a class can register an action to
 * execute when a subset is pruned from the powerset (i.e. because it is empty).
 * Pruning behaviour can be controlled using the 'prune' attribute. By default,
 * pruning of empty subsets is enabled.
 *
 * The Keytype associated with the powerset is parameterised, allowing creators
 * to strongly type the key value.
 *
 * Note that this implementation will only put an event into one subset
 * managed by the PowerSet (i.e. all of the subsets are disjoint). 
 *
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import com.eventswarm.schedules.TickAction;
import com.eventswarm.*;
import com.eventswarm.events.Event;
import com.eventswarm.util.actions.QueuedAdd;
import com.eventswarm.util.actions.QueuedPowersetAdd;
import com.eventswarm.util.actions.QueuedPowersetRemove;
import com.eventswarm.util.actions.QueuedRemove;
import org.apache.log4j.*;
import java.util.*;
import com.eventswarm.eventset.EventSet;
//import com.eventswarm.eventset.LastNWindow;

public class HashPowerset<Keytype>
        extends java.util.HashMap<Keytype,EventSet>
        implements Powerset<Keytype>, Clear
{

    /** Default Size of each LastNWindow in the powerset */
    //private final static int SIZE=1;

    private static final long serialVersionUID = 1L;

    /**
     * Eventset creation factory
     */
    private EventSetFactory<Keytype> esFactory;

    /**
     * If true, prune subsets when they become empty
     */
    private boolean prune = true;

    /**
     * If the EventSetFactory recycles, then remember
     */
    private transient EventSetRecycler recycler = null;


    /**
     * Default ActionExecutor for synchronous execution
     */
    private ActionExecutor executor = new SimpleActionExecutor();

    /**
     * Event key generator
     */
    private EventKey<Keytype> keyGenerator;

    // Maintain a set of registered NewSetActions
    private Set<NewSetAction<Keytype>> newSetActions = new HashSet<NewSetAction<Keytype>>();
    private Set<RemoveSetAction<Keytype>> removeSetActions = new HashSet<RemoveSetAction<Keytype>>();

    // Maintain sets of actions for add/remove event
    private Set<PowersetAddEventAction> addEventActions = new HashSet<PowersetAddEventAction>();
    private Set<PowersetRemoveEventAction> removeEventActions = new HashSet<PowersetRemoveEventAction>();

    // Maintain a set of registered EventTickActions
    private Set<TickAction> eventTickActions = new HashSet<TickAction>();

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(HashPowerset.class);


    /**
     * Create a HashPowerset with the identified eventset factory and key
     * generator.
     *
     * @param esFactory
     * @param keyGenerator
     */
    public HashPowerset(EventSetFactory<Keytype> esFactory, EventKey<Keytype> keyGenerator) {
        super();
        this.esFactory = esFactory;
        this.keyGenerator = keyGenerator;
        if (EventSetRecycler.class.isInstance(esFactory)) {
            this.recycler = (EventSetRecycler) esFactory;
        }
    }

    /**
     * Create a HashPowerset with the identified key generator and a default
     * EventSet factory.
     * 
     * The default EventSet factory creates a basic EventSet
     *
     * @param keyGenerator
     */
    public HashPowerset(EventKey<Keytype> keyGenerator) {
        super();
        this.keyGenerator = keyGenerator;
        this.esFactory = new EventSetFactory<Keytype> () {
            public EventSet createEventSet (Powerset<Keytype> pset, Keytype key) {
                // just create a regular eventset
                return new EventSet();
            }
        };
    }

    /**
     * Create a HashPowerset with the identified eventset factory, key
     * generator, initial capacity and load actor.
     *
     * @param initialCapacity
     * @param loadFactor
     * @param esFactory
     * @param keyGenerator
     */
    public HashPowerset(int initialCapacity, float loadFactor, EventSetFactory<Keytype> esFactory, EventKey<Keytype> keyGenerator) {
        super(initialCapacity, loadFactor);
        this.esFactory = esFactory;
        this.keyGenerator = keyGenerator;
        if (EventSetRecycler.class.isInstance(esFactory)) {
            this.recycler = (EventSetRecycler) esFactory;
        }
    }

    /**
     * Create a HashPowerset with the identified eventset factory, key
     * generator and initial capacity.
     *
     * @param initialCapacity
     * @param esFactory
     * @param keyGenerator
     */
    public HashPowerset(int initialCapacity, EventSetFactory<Keytype> esFactory, EventKey<Keytype> keyGenerator) {
        super(initialCapacity);
        this.esFactory = esFactory;
        this.keyGenerator = keyGenerator;
        if (EventSetRecycler.class.isInstance(esFactory)) {
            this.recycler = (EventSetRecycler) esFactory;
        }
    }


    /**
     * Get the value of keyGenerator
     *
     * @return the value of keyGenerator
     */
    public EventKey<Keytype> getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * Set the value of keyGenerator
     *
     * @param keyGenerator new value of keyGenerator
     */
    public void setKeyGenerator(EventKey<Keytype> keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    /**
     * return the factory used for creating EventSets.
     */
    public EventSetFactory<Keytype> getFactory() {
        return esFactory;
    }

    /**
     * Set the factory to be used for creating EventSets.
     *
     * If not set, a default factory will be used.
     * 
     * @param factory
     */
    public void setFactory (EventSetFactory<Keytype> factory) {
        this.esFactory = factory;
    }

    /**
     *
     * @return true if Powerset is pruning empty subsets
     */
    public boolean isPrune() {
        return prune;
    }

    /**
     * Set the prune flag to determine whether or not empty sets are pruned. Note that any new
     * value for this flag only affects sets that become empty after the flag is set. The
     * default value is true.
     *
     * @param prune if true, subsets will be pruned when they become empty. Otherwise they linger.
     */
    public void setPrune(boolean prune) {
        this.prune = prune;
    }

    /**
     * Add a new event to the Powerset, placing it in the appropriate EventSet
     * and calling any calling any NewSetActions if a new subset is created.
     * 
     * Note that the supplied trigger is passed through to the EventSet.
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event)
    {
        // ignore events with no key generator or key
        if (this.keyGenerator != null) {
            Keytype key = this.keyGenerator.getKey(event);
            if (key != null) {
                EventSet es;
                // Get an eventset for this event
                if (this.containsKey(key)) {
                    log.debug("Powerset already contains key: '" + key + "'");
                    // if we already have an eventset, grab it and add our event
                    es = this.get(key);
                } else {
                    log.debug("Creating new eventset for key: '" + key + "'");
                    // if not, try to create a new one
                    es = this.esFactory.createEventSet(this, key);
                    if (es != null) {
                        // add it to our powerset
                        this.put(key, es);
                        // if it supports the TickAction, register it against us
                        if (TickAction.class.isInstance(es)) {
                            this.registerAction((TickAction) es);
                        }
                        // call the newSetActions
                        for (NewSetAction<Keytype> action : this.newSetActions) {
                            log.debug("Calling new set action " + action.toString() + " for key: " + key);
                            action.execute(this, es, key);
                        }
                    } else {
                        log.info("No eventset created for key: '" + key + "'");
                    }
                }
                if (es != null) {
                    executor.add(new QueuedAdd(es, trigger, event));
                    for (PowersetAddEventAction action : addEventActions) {
                        executor.add(new QueuedPowersetAdd(action, this, es, event));
                    }
                }
                // fire tick actions after all of the above actions are complete
                // TODO: work out whether we really need tick actions from a powerset
                fireTickActions(event);
            } else {
                log.debug("No key generated for event: " + event.toString());
            }
        } else {
            log.warn("No key generator for powerset instance");
        }
    }

    
    /** 
     * Remove an event from the Powerset by passing the trigger through to the
     * EventSet associated with the EventKey.
     * 
     * @param trigger
     * @param event
     */
    public void execute(RemoveEventTrigger trigger, Event event) {
        // ignore events with no key generator or key
        if (this.keyGenerator != null) {
            Keytype key = this.keyGenerator.getKey(event);
            if (key != null) {
                EventSet es;
                // Get an eventset for this event
                if (this.containsKey(key)) {
                    // if we have an eventset, process any associated remove actions then pass on the trigger to the set
                    es = this.get(key);
                    for (PowersetRemoveEventAction action : removeEventActions) {
                        executor.add(new QueuedPowersetRemove(action, this, es, event));
                    }
                    executor.add(new QueuedRemove(es, trigger, event));
                    if (this.prune && es.size() == 0) {
                        // prune the subset if it is now empty
                        log.debug("Pruning powerset for key: " + key.toString());
                        this.remove(key);
                        for (RemoveSetAction<Keytype> action : this.removeSetActions) {
                            action.execute(this, es, key);
                        }
                        if (recycler != null) {
                            log.debug("Recycling pruned eventset");
                            recycler.recycle(es);
                        }
                    }
                }
            }
        }
    }

    public void registerAction(PowersetAddEventAction action) {
        addEventActions.add(action);
    }

    public void unregisterAction(PowersetAddEventAction action) {
        addEventActions.remove(action);
    }

    public void registerAction(PowersetRemoveEventAction action) {
        removeEventActions.add(action);
    }

    public void unregisterAction(PowersetRemoveEventAction action) {
        removeEventActions.remove(action);
    }

    /**
     * Register a NewSetAction.
     *
     * @param action
     */
    public void registerAction(NewSetAction<Keytype> action) {
        log.debug("Adding NewSetAction: " + action.toString());
        this.newSetActions.add(action);
    }

    /**
     * Unregister a NewSetAction.
     * 
     * @param action
     */
    public void unregisterAction(NewSetAction<Keytype> action) {
        this.newSetActions.remove(action);
    }

    /**
     * Register a RemoveSetAction.
     *
     * @param action
     */
    public void registerAction(RemoveSetAction<Keytype> action) {
        this.removeSetActions.add(action);
    }

    /**
     * Unregister a RemoveSetAction.
     *
     * @param action
     */
    public void unregisterAction(RemoveSetAction<Keytype> action) {
        this.removeSetActions.remove(action);
    }

    /**
     * Register an TickAction
     * 
     * @param action
     */
    public void registerAction(TickAction action) {
        this.eventTickActions.add(action);
    }


    /**
     * Unregister an TickAction
     *
     * @param action
     */
    public void unregisterAction(TickAction action) {
        this.eventTickActions.remove(action);
    }

    /**
     * Need to override the default 'equals' method so that powersets can be distinguished even if they contain the
     * same keys and values.
     *
     * This is particularly an issue for us because we 'register' actions against upstream event sources and store them
     * in a set. Typically, the Powerset is empty when registering, so the default equals() method says that it's the
     * same object as some other empty powerset, thus the registration fails.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        // Use object identity for now
        return (this == o);
    }

    /**
     * Override the default hashcode method for consistency with equals()
     */
    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    /**
     * Helper function to call EventTickActions on other subsets that support this action.
     * 
     * @param event
     */
    private void fireTickActions(Event event) {
        // then call any others registered for EventTickActions
        log.debug("Sending event clock tick to downstream eventsets");
        for (TickAction action: eventTickActions) {
            action.execute(this, event.getHeader().getTimestamp());
        }
    }
}
