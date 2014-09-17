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
package com.eventswarm.eventset;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.MutablePassThru;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;

/**
 * A Wall is a MutablePassThru that discards everything (i.e. never lets anything through)
 *
 * Note that this class is not stateless, because downstream components might need to 'think' that
 * they're connected to a real passthru, thus registrations need to be maintained.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class Wall extends MutablePassThruImpl implements MutablePassThru {

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        // ignore everything
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // ignore everything
    }
}
