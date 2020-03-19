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
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Event class for JSON events received via HTTP and deserialized using the org.json JSONObject class
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class OrgJsonEvent extends JdoEvent implements JsonEvent<JSONObject> {
    // maintain local pointers to parts for convenience
    protected transient JSONObject json;

    private static Logger logger = Logger.getLogger(OrgJsonEvent.class);

    // Default serializer and content type for JSON
    public static final String CONTENT_TYPE="application/json";
    public static final String CHARSET = "UTF-8";

    public OrgJsonEvent() {
        super();
    }

    /**
     * Construct an Event from a header and the supplied JSON Object
     *
     * @param header
     * @param json
     */
    public OrgJsonEvent(Header header, JSONObject json) {
        super();
        construct(header, json);
    }

    /**
     * Construct an event from a header and an eventparts map
     *
     * This constructor is intended for subclasses that have other parts
     *
     * @param header
     * @param eventParts
     */
    public OrgJsonEvent(Header header, Map<String, EventPart> eventParts) {
        super(header, eventParts);
        this.json = ((JdoPartWrapper<JSONObject>)eventParts.get(JSON_PART_NAME)).getWrapped();
    }

    protected void construct(Header header, JSONObject json) {
        this.json = json;
        this.header = header;
        this.eventParts = new HashMap<String, EventPart>();
        eventParts.put(JSON_PART_NAME, new JdoPartWrapper<JSONObject>(json));
    }

    public JSONObject getJsonObject(String path) {
        try {
            JSONObject last = navigatePath(path, json);
            return last == null ? null : last.optJSONObject(leafOf(path));
        } catch (JSONException exc) {
            logger.warn("Error retrieving path " + path, exc);
            return null;
        }
    }

    public JSONObject getJson() {
        return json;
    }

    private void setJson(JSONObject json) {
        this.json = json;
    }

    // delegate methods to the JSONObject instance
    public int length() {
        return json.length();
    }

    public Iterator<?> keys() {
        return json.keys();
    }

    public Set<?> keySet() {
        return json.keySet();
    }

    public int getInt(String path) throws JSONException {
        JSONObject last = navigatePath(path, json);
        return last.getInt(leafOf(path));
    }

    public boolean getBoolean(String path) throws JSONException {
        JSONObject last = navigatePath(path, json);
        return last.getBoolean(leafOf(path));
    }

    public JSONObject getJSONObject(String path) throws JSONException {
        JSONObject last = navigatePath(path, json);
        return last == null ? null : last.optJSONObject(leafOf(path));
    }

    public boolean isNull(String path) {
        JSONObject last = navigatePath(path, json);
        return last.isNull(leafOf(path));
    }

    public void setEvent(Event event) {
        ((JdoEventPart) eventParts.get(JSON_PART_NAME)).setEvent(event);
    }

    public boolean has(String path) {
        try {
            JSONObject last = navigatePath(path, json);
            return last != null && last.has(leafOf(path));
        } catch (JSONException exc) {
            return false;
        }
    }

    public double getDouble(String path) throws JSONException {
        JSONObject last = navigatePath(path, json);
        return last.getDouble(leafOf(path));
    }

    public JSONArray getJSONArray(String path) throws JSONException {
        JSONObject last = navigatePath(path, json);
        return last == null ? null : last.optJSONArray(leafOf(path));
    }

    public long getLong(String path) throws JSONException {
        JSONObject last = navigatePath(path, json);
        return last.getLong(leafOf(path));
    }

    public String getString(String path) throws JSONException {
        JSONObject last = navigatePath(path, json);
        return last == null ? null : last.optString(leafOf(path));
    }

    public String getJsonString() {
        return json.toString();
    }

    public Object get(String path) {
        JSONObject last = navigatePath(path, json);
        return last == null ? null : last.opt(leafOf(path));
    }

    private String leafOf(String path) {
        return path.substring(path.lastIndexOf(PATH_SEPARATOR)+1);
    }

    /**
     * Navigate to the last component of the path, returning the JSON object immediately preceding the
     * leaf element of the path.
     *
     * @param path
     * @return JSONObject immediately preceding leaf or null if path basename is not present or an object
     */
    public static JSONObject navigatePath(String path, JSONObject object) {
        int sep = path.indexOf(PATH_SEPARATOR);
        if (sep == -1) {
            return object;
        } else {
            JSONObject next = (sep == 0) ? object : object.optJSONObject(path.substring(0,sep));
            return next == null ? null : navigatePath(path.substring(sep+1), next);
        }
    }
}
