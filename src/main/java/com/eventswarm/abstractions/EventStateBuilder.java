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
import com.eventswarm.MutablePassThru;
import com.eventswarm.RemoveEventAction;

/**
 * Builds and connects together an EventMap and EventRemover to maintain a mutable state reflected
 * by a stream of events.
 *
 * We use an EventMap to maintain a mapping from event keys to specific events in the state. We use an
 * eventRemover to detect other events that require event removal, using the EventMap to identify the
 * event that should be removed and calling the state RemoveEventAction to remove the targeted event.
 *
 * This class could be used to maintain an in-memory copy of database table state, for example, if the
 * state was captured by a stream of insert/update trigger outputs, and the remover was linked to the
 * stream of delete triggers outputs.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventStateBuilder<T> {
    /** abstraction for the events that reflects state, typically a time window or other EventSet */
    private MutablePassThru state;
    /** key retriever for state events, e.g. extract database key */
    private ValueRetriever<T> stateKeyRetriever;
    /** input source for events that signal removal */
    private AddEventTrigger removeInput;
    /** key retriever for removal events, e.g. extract database key */
    private ValueRetriever<T> removeKeyRetriever;

    private EventMap<T> eventMap;
    private EventRemover<T> eventRemover;

    public EventStateBuilder(MutablePassThru state, ValueRetriever<T> stateKeyRetriever, AddEventTrigger removeInput, ValueRetriever<T> removeKeyRetriever) {
        this.state = state;
        this.stateKeyRetriever = stateKeyRetriever;
        this.removeInput = removeInput;
        this.removeKeyRetriever = removeKeyRetriever;
        connect();
    }

    private void connect() {
        eventMap = new EventMap<T>(stateKeyRetriever);
        eventRemover = new EventRemover<T>(eventMap.getMap(), removeKeyRetriever);
        // set up eventMap to capture add/remove events from the state
        state.registerAction((AddEventAction) eventMap);
        state.registerAction((RemoveEventAction) eventMap);
        // link event remover to state so it can remove events from the state (and also the eventMap)
        eventRemover.registerAction(state);
        // connect event remover to the removal input stream
        removeInput.registerAction(eventRemover);
    }

    /**
     * @return the abstraction capturing state, e.g. time window, filter or possibly just a passthru
     */
    public MutablePassThru getState() {
        return state;
    }

    private void setState(MutablePassThru state) {
        this.state = state;
    }

    /**
     * @return the map of key -> event pairs that captures the most recent event for the key
     */
    public EventMap<T> getEventMap() {
        return eventMap;
    }

    private void setEventMap(EventMap<T> eventMap) {
        this.eventMap = eventMap;
    }
}
