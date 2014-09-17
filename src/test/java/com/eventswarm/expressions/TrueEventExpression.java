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

package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;

/**
 * Event expression that always matches
 * 
 * @author andyb
 */
public class TrueEventExpression extends AbstractEventExpression {

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // add the event to the set and fire the trigger
        if (event != null) {
            this.matches.add(event);
        }
        this.fire(event);
    }

    @Override
    public boolean hasMatched(Event event) {
        return this.matches.contains(event);
    }
}
