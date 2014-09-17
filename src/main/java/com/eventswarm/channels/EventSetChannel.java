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
package com.eventswarm.channels;

import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;

import java.util.Iterator;

/**
 * Class to replay events from an EventSet as if it were a channel.
 *
 * This replay uses the iterator provided by the EventSet and should be thread safe, but no guarantees.
 * By using an EventSet, we effectively guarantee that the events are delivered in chronological order.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventSetChannel extends AbstractChannel {
    private EventSet events;
    private transient Iterator<Event> iterator;

    public EventSetChannel(EventSet events) {
        this.events = events;
    }

    @Override
    public void setup() throws Exception {
        Iterator<Event> iterator = events.iterator();
    }

    @Override
    public void teardown() throws Exception {
        iterator = null;
    }

    @Override
    public Event next() throws Exception {
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            stop();
            return null;
        }
    }
}
