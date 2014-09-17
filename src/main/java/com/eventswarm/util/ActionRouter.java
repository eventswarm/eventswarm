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

import com.eventswarm.*;
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;

import java.util.HashMap;

/**
 * Class that routes events to add/remove actions using the value returned by a retriever as a hash key
 *
 * Unmatched events are discarded.
 * Since this class does not actually implement any triggers, we don't offer them.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ActionRouter<T> extends HashMap<T,MutableTarget> implements MutableTarget {
    ValueRetriever<T> retriever;

    public ActionRouter(ValueRetriever retriever) {
        super();
        this.retriever = retriever;
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        AddEventAction action = get(retriever.getValue(event));
        if (action == null) {
            // if no matching action and a default action (with null key) exists, use it
            action = get(null);
        }
        if (action != null) {
            action.execute(trigger, event);
        }
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        RemoveEventAction action = get(retriever.getValue(event));
        if (action == null) {
            // if no matching action and a default action (with null key) exists, use it
            action = get(null);
        }
        if (action != null) {
            action.execute(trigger, event);
        }
    }
}
