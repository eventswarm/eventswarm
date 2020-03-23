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

import com.eventswarm.events.*;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * SubEvent class for OrgJsonEvent instances, allowing us to split a received JSON object into a set of subevents
 * for further processing.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class OrgJsonSubEvent extends OrgJsonEvent implements SubEvent<JSONObject> {
    protected transient OrgJsonEvent parent;

    /**
     * Create a sub-event with the specified parent and subordinate JSON object.
     *
     * It's up to the caller to ensure that the subordinate JSON object actually belongs to the parent.
     * This constructor is intended for use where the subevent factory method is iterating through a set of
     * subordinate objects that are to become subevents.
     *
     * @param parent
     * @param json
     */
    public OrgJsonSubEvent(OrgJsonEvent parent, JSONObject json) {
        super(createSubHeader(parent, parent.getHeader().getTimestamp()), json);
        this.parent = parent;
        this.eventParts.put(PARENT_EVENTPART, new JdoNestedEvent(parent));
    }

    /**
     * Create a sub-event with the specified parent and subordinate JSON object and a distinguished timestamp for the
     * subordinate.
     *
     * It's up to the caller to ensure that the subordinate JSON object actually belongs to the parent.
     * This constructor is intended for use where the subevent factory method is iterating through a set of
     * subordinate objects that are to become subevents.
     *
     * @param parent
     * @param json
     */
    public OrgJsonSubEvent(OrgJsonEvent parent, JSONObject json, Date timestamp) {
        super(createSubHeader(parent, timestamp), json);
        this.parent = parent;
        this.eventParts.put(PARENT_EVENTPART, new JdoNestedEvent(parent));
    }

    /**
     * Create a sub-event with the specified parent and the subordinate object at the specified path.
     *
     * @param parent
     * @param path
     */
    public OrgJsonSubEvent(OrgJsonEvent parent, String path) {
        super(createSubHeader(parent, parent.getHeader().getTimestamp()), parent.getJSONObject(path));
        this.parent = parent;
        this.eventParts.put(PARENT_EVENTPART, new JdoNestedEvent(parent));
    }

    /**
     * Create an OrgJsonSubEvent with the specified parts.
     *
     * This method is primarily intended for subclasses that want to add other things to the SubEvent and should
     * not generally be used.
     *
     * @param header
     * @param eventParts
     */
    public OrgJsonSubEvent(Header header, Map<String, EventPart> eventParts) {
        super(header, eventParts);
        this.parent = (OrgJsonEvent) ((NestedEvent) eventParts.get(PARENT_EVENTPART)).getNestedEvent();
    }

    private static Header createSubHeader(OrgJsonEvent parent, Date timestamp) {
        return new JdoHeader(timestamp, parent.getHeader().getSource());
    }

    public OrgJsonEvent getParent() {
        return this.parent;
    }

    public JSONObject getSubordinate() {
        return this.json;
    }
}
