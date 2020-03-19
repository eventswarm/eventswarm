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
package com.eventswarm.util;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;

import java.util.ArrayList;

/**
 * Simple wrapper around ArrayList that adds events to a list when received via the AddEventAction.execute method
 *
 * This is intended as a utility for testing and other applications where arrival order is significant and duplicates
 * can be tolerated.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventList extends ArrayList<Event> implements AddEventAction {
    private static final long serialVersionUID = 1L;

    public void execute(AddEventTrigger trigger, Event event) {
        this.add(event);
    }
}
