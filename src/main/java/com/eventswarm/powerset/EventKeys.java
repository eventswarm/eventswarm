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

package com.eventswarm.powerset;

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import java.util.Set;

/**
 * Interface for classes providing the ability to extract a set (array) keys from an event.
 *
 * This interface is used by the MultiHashPowerset and possibly other classes.  The
 * class is parameterised with a <Keytype> to allow strongly typed implementation.
 *
 * @author andyb
 */
public interface EventKeys<Keytype> {

    /**
     * Return an array of keys that determines which EventSets a particular Event
     * should be added to.
     *
     * @param event
     * @return A set of keys associated with the event
     */
     public <Keytype> Keytype [] getKeys(Event event);

    /**
     * Simple wrapper class that allows us to use a ValueRetriever that returns an array as an EventKeys instance
     *
     * TODO: replace the EventKey class with the ValueRetriever class, since they're largely duplicating functionality
     *
     * @param <Type>
     */
    public static class EventKeysRetriever<Type> implements EventKeys<Type> {
        private ValueRetriever<Type[]> retriever;

        public EventKeysRetriever(ValueRetriever<Type[]> retriever) {
            this.retriever = retriever;
        }

        @Override
        public Type[] getKeys(Event event) {
            return retriever.getValue(event);
        }
    }
}
