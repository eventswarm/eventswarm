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

/**
 * Interface for classes providing the ability to extract a unique key from
 * an Event.
 *
 * This interface is used by the HashPowerset and possibly other classes.  The
 * class is paramterised with a <Keytype> to allow strongly typed implementation.
 *
 * @author andyb
 */
public interface EventKey<Keytype> {

    /**
     * Return a key value that determines which EventSet a particular Event
     * should be added to.
     *
     * @param event
     * @return A key value or null if the event should be discarded
     */
     public Keytype getKey(Event event);

    /**
     * Simple wrapper class that allows us to use a ValueRetriever in the EventKey class of a powerset.
     *
     * TODO: replace the EventKey class with the ValueRetriever class, since they're largely duplicating functionality
     *
     * @param <Type>
     */
    public static class EventKeyRetriever<Type> implements EventKey<Type> {
        private ValueRetriever<Type> retriever;
        public EventKeyRetriever(ValueRetriever<Type> retriever) {
            this.retriever = retriever;
        }

        @Override
        public Type getKey(Event event) {
            return retriever.getValue(event);
        }
    }
}
