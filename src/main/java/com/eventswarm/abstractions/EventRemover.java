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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that listens for events that indicate another event should be removed from an EventSet or
 * other processing graph.
 *
 * For example, if a twitter 'cancel' event is received for a tweet, we need to tell all of the downstream
 * processing components to remove that tweet.
 *
 * This class needs a mapper that allows us to identify the original event based on a key or identifier,
 * because the RemoveEventTrigger used to remove events from a graph requires the original event as a parameter.
 * The mapping is provided by a map instance provided to the constructor.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventRemover<T> implements AddEventAction, RemoveEventTrigger {
    private Map<T,Event> map;
    private ValueRetriever<T> keyRetriever;
    private Set<RemoveEventAction> actions;

    private static Logger logger = Logger.getLogger(EventRemover.class);

    /**
     * Create an EventRemover, using the supplied map to map from keys returned by keyRetriever to the
     * event targeted for removal.
     *
     * Note that the supplied Map is referenced rather than cloned or copied, so external changes to
     * the map change the behaviour of this component, thus making it possible to use the map 'owned'
     * by an EventMap instance.
     *
     * @param map
     */
    public EventRemover(Map<T, Event> map, ValueRetriever<T> keyRetriever) {
        this.map = map;
        this.keyRetriever = keyRetriever;
        this.actions = new HashSet<RemoveEventAction>();
    }

    public void execute(AddEventTrigger trigger, Event event) {
        T key = keyRetriever.getValue(event);
        if (key != null) {
            logger.debug("Removing event with key: " + key);
            Event target = map.get(key);
            if (target != null) {
                for (RemoveEventAction action: actions) {
                    logger.debug("Event found, removing target event from " + action.toString());
                    action.execute(this, target);
                }
            } else {
                logger.debug("No event found for key " + key);
            }
        } else {
            logger.debug("No key retrieved from event");
        }
    }

    public void registerAction(RemoveEventAction action) {
        this.actions.add(action);
    }

    public void unregisterAction(RemoveEventAction action) {
        this.actions.remove(action);
    }
}
