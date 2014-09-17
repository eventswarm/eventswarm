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
package com.eventswarm.events.jdo;

import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Simple extension of a org.json.JSONObject to be an EventPart.
 *
 * TODO: get rid of EventPart, or at least, make it nicer to work with
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
@Deprecated
public class OrgJsonPart extends JSONObject implements EventPart {
    Event event = null;

    public OrgJsonPart(String source) throws JSONException {
        super(source);
    }

    public OrgJsonPart() {
    }

    public OrgJsonPart(JSONTokener x) throws JSONException {
        super(x);
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
