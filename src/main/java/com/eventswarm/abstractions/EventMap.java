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
package com.eventswarm.abstractions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Maintain a hash of map keyed by the value returned by a keyRetriever.
 *
 * For example, if you have a want to maintain a 'current' value for an attribute or database tuple, you
 * can add create update map to a hash keyed by the database table key (assuming both create and update
 * map carry the full set of column values).
 *
 * Note that this class can be paired with an EventRemover to find and remove events that need to be
 * removed in response to a trigger.
 *
 * Note that keyRetrievers should be deterministic (i.e. always return the same value for an event), otherwise
 * the RemoveEventAction will behave unreliable because it might not find the event.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventMap<T> extends MutableAbstractionImpl implements MutableAbstraction {
    private ValueRetriever<T> keyRetriever;
    private HashMap<T,Event> map;

    private static Logger logger = Logger.getLogger(EventMap.class);

    /**
     * Create a new event map that uses the supplied keyRetriever to extract a hash key from each event
     *
     * @param keyRetriever
     */
    public EventMap(ValueRetriever<T> keyRetriever) {
        super();
        this.keyRetriever = keyRetriever;
        this.map = new HashMap<T,Event>();
    }

    /**
     * Update the hash to reflect the new event
     *
     * The key returned by the keyRetriever is used to add the new event, overwriting a previous value if present.
     * Null key values are ignored (i.e. if the keyRetriever returns null, the event is not added to the hash).
     *
     * @param trigger Upstream trigger producing the event
     * @param event Event to be added
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        T key = keyRetriever.getValue(event);
        if (key != null) {
            map.put(key, event);
            // tell our listeners, if any
            super.execute(trigger, event);
        }
    }

    /**
     * Remove this event from the hash, if found using the keyRetriever
     *
     * This method retrieves an event from the map using the key extracted by keyRetriever, and if it matches
     * the supplied event, removes that entry from the hash.
     *
     * Note that removes unless the keyRetriever is deterministic (i.e. always returns the same value for a given event)
     * this method might not always remove the event.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        T key = keyRetriever.getValue(event);
        if (key != null) {
            if (map.get(key) == event) {
                map.remove(key);
            } else {
                logger.debug ("Not removing: a different event has this key");
            }
            // tell our listeners, if any
            super.execute(trigger, event);
        }
    }

    /**
     * Return the map maintained by this abstraction
     *
     * @return
     */
    public HashMap<T, Event> getMap() {
        return map;
    }

    /**
     * Private setter because we don't want others replacing the map
     * @param map
     */
    private void setMap(HashMap<T, Event> map) {
        this.map = map;
    }

    /**
     * Clear the hash of map held in this abstraction
     */
    @Override
    public void clear() {
        this.map.clear();
    }
}
