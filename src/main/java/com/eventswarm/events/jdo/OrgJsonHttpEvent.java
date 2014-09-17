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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;

/**
 * Event class for JSON events received via HTTP and deserialized using the org.json JSONObject class
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class OrgJsonHttpEvent extends OrgJsonEvent implements Event, HttpRequestEvent, JsonEvent<JSONObject> {
    // maintain local pointers to parts for convenience
    protected transient HttpEventPart http;

    public OrgJsonHttpEvent() {
        super();
    }

    /**
     * Construct an Event from the various parts required
     *
     * @param header
     * @param json
     * @param http
     */
    public OrgJsonHttpEvent(Header header, JSONObject json, HttpEventPart http) {
        super(header, json);
        this.http = http;
        eventParts.put(HttpEventPart.HTTP_PART_NAME, http);
    }

    public OrgJsonHttpEvent(Header header, Map<String, EventPart> eventParts) {
        super(header, eventParts);
        this.http = (HttpEventPart) eventParts.get(HTTP_PART_NAME);
    }

    // Just delegate all the methods to the respective parts

    @Override
    public List<String> getHttpHeader(String name) {
        return http.getHeaders().get(name);
    }

    @Override
    public String getFirstHeaderValue(String name) {
        return http.getFirstHeaderValue(name);
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return http.getHeaders();
    }

    public HttpEventPart getHttp() {
        return http;
    }

    private void setHttp(HttpEventPart http) {
        this.http = http;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return http.getRemoteAddress();
    }

    @Override
    public String getRequestMethod() {
        return http.getRequestMethod();
    }

    @Override
    public URI getRequestUri() {
        return http.getRequestUri();
    }

    @Override
    public Date getRequestDate() {
        return http.getRequestDate();
    }
}
