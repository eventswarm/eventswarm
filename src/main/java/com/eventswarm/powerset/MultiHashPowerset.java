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
 * This class implements a Powerset using a hashtable of key/eventset pairs with multiple keys per event.
 *
 * Upon arrival, new events are pushed into one or more sets based on the
 * extraction of keys from the event.  If the key extraction fails then
 * the event is discarded, meaning the key extraction function can be used as
 * a filter if desired.  This extraction is performed by an EventKeys
 * implementation provided by the creator.  The creator of a MultiHashPowerset MUST
 * provide an implementation of the EventKeys interface to extract keys of the
 * chosen key type from each event, otherwise no events will ever be added.
 *
 * If no set exists for an extracted key, a MutablePassthru factory is called
 * to create a new subset.  The creator can provide a factory implementation
 * for creating EventSets, although a default implementation is provided.
 * This provides two control mechanisms: firstly, returning a null EventSet
 * indicates that this event should not result in a new subset (and will be
 * discarded); and secondly, allowing the use of time or size windows to
 * optimise memory usage and provide adequate retention of events for processing
 * purposes.  The default factory (internal) creates a basic EventSet.
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
 * Copyright 2012 Ensift Pty Ltd
 * 
 * @author andyb
 */

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.schedules.TickAction;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiHashPowerset<Keytype>
        extends HashMap<Keytype,EventSet>
        implements Powerset<Keytype>
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
     * Event key generator
     */
    private EventKeys<Keytype> keyGenerator;

    // Maintain a set of registered NewSetActions and RemoveSetActions
    private Set<NewSetAction<Keytype>> newSetActions = new HashSet<NewSetAction<Keytype>>();
    private Set<RemoveSetAction<Keytype>> removeSetActions = new HashSet<RemoveSetAction<Keytype>>();

    // Maintain sets of actions for add/remove event
    private Set<PowersetAddEventAction> addEventActions = new HashSet<PowersetAddEventAction>();
    private Set<PowersetRemoveEventAction> removeEventActions = new HashSet<PowersetRemoveEventAction>();

    // Maintain a set of registered EventTickActions
    private Set<TickAction> eventTickActions = new HashSet<TickAction>();

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(MultiHashPowerset.class);


    /**
     * Create a HashPowerset with the identified eventset factory and key
     * extractor.
     *
     * @param esFactory
     * @param keyGenerator
     */
    public MultiHashPowerset(EventSetFactory<Keytype> esFactory, EventKeys<Keytype> keyGenerator) {
        super();
        this.esFactory = esFactory;
        this.keyGenerator = keyGenerator;
        log.debug("In constructor");
    }

    /**
     * Create a HashPowerset with the identified key extractor and a default
     * EventSet factory.
     *
     * The default EventSet factory creates an EventSet limited to 1 event.
     *
     * @param keyGenerator
     */
    public MultiHashPowerset(EventKeys<Keytype> keyGenerator) {
        super();
        this.keyGenerator = keyGenerator;
        this.esFactory = new EventSetFactory<Keytype> () {
            public EventSet createEventSet (Powerset<Keytype> pset, Keytype key) {
                // ignore the key and return a basic eventset
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
    public MultiHashPowerset(int initialCapacity, float loadFactor, EventSetFactory<Keytype> esFactory, EventKeys<Keytype> keyGenerator) {
        super(initialCapacity, loadFactor);
        this.esFactory = esFactory;
        this.keyGenerator = keyGenerator;
    }

    /**
     * Create a HashPowerset with the identified eventset factory, key
     * generator and initial capacity.
     *
     * @param initialCapacity
     * @param esFactory
     * @param keyGenerator
     */
    public MultiHashPowerset(int initialCapacity, EventSetFactory<Keytype> esFactory, EventKeys<Keytype> keyGenerator) {
        super(initialCapacity);
        this.esFactory = esFactory;
        this.keyGenerator = keyGenerator;
    }

    /**
     * Get the value of keyGenerator
     *
     * @return the value of keyGenerator
     */
    public EventKeys<Keytype> getKeyGenerator() {
        return keyGenerator;
    }

    /**
     * Set the value of keyGenerator
     *
     * @param keyGenerator new value of keyGenerator
     */
    public void setKeyGenerator(EventKeys<Keytype> keyGenerator) {
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
            Keytype[] keys = this.keyGenerator.getKeys(event);
            if (keys != null) {
                log.debug("Event has " + Integer.toString(keys.length) + " keys");
                EventSet es;
                for (int i=0; i<keys.length; i++) {
                    // Get an eventset for this event
                    if (this.containsKey(keys[i])) {
                        log.debug("Powerset already contains key: '" + keys[i] + "'");
                        // if we already have an eventset, grab it and add our event
                        es = this.get(keys[i]);
                        this.addEvent(trigger, es, event);
                    } else {
                        log.debug("Creating new eventset for key: '" + keys[i] + "'");
                        // if not, try to create a new one
                        es = this.esFactory.createEventSet(this, keys[i]);
                        if (es != null) {
                            // add it to our powerset
                            this.put(keys[i], es);
                            // if it supports the TickAction, register it against us
                            if (TickAction.class.isInstance(es)) {
                                this.registerAction((TickAction) es);
                            }
                            // call the newSetActions
                            for (NewSetAction<Keytype> action : this.newSetActions) {
                                action.execute(this, es, keys[i]);
                            }
                            // add the event
                            this.addEvent(trigger, es, event);
                        } else {
                            log.info("No eventset created for key: '" + keys[i] + "'");
                        }
                    }
                    for (PowersetAddEventAction action : addEventActions) {
                        action.execute(this, es, event);
                    }
                }
            } else {
                log.info("No keys generated for event: " + event.toString());
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
            Keytype keys[] = this.keyGenerator.getKeys(event);
            if (keys != null) {
                EventSet es;
                for (int i=0; i<keys.length; i++) {
                    // Get an eventset for this event
                    if (this.containsKey(keys[i])) {
                        // if we have an eventset, pass on the trigger
                        es = this.get(keys[i]);
                        for (PowersetRemoveEventAction action : removeEventActions) {
                            action.execute(this, es, event);
                        }
                        es.execute(trigger, event);
                        if (this.prune && es.size() == 0) {
                            // prune the subset if it is now empty
                            log.debug("Removing powerset for key: " + keys[i].toString());
                            this.remove(keys[i]);
                            for (RemoveSetAction<Keytype> action : this.removeSetActions) {
                                action.execute(this, es, keys[i]);
                            }
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
     * Helper function to add an event to one EventSet then call 
     * EventTickActions on other subsets that support this action.
     * 
     * @param es
     * @param event
     */
    private void addEvent(AddEventTrigger trigger, EventSet es, Event event) {
        // Add the event to the identified EventSet
        es.execute(trigger, event);

        // then call any others registered for EventTickActions
        log.debug("Sending event clock tick to downstream eventsets");
        for (TickAction action: eventTickActions) {
            if (action != es) {
                action.execute(this, event.getHeader().getTimestamp());
            }
        }
    }
}
