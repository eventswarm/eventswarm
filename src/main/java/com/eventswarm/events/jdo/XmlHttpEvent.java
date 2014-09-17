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
import org.w3c.dom.Node;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class XmlHttpEvent extends XmlEventImpl implements Event, HttpRequestEvent, XmlEvent {
    // maintain local pointers to parts for convenience
    protected transient HttpEventPart http;

    public XmlHttpEvent() {
        super();
    }

    /**
     * Construct an Event from the various parts required
     *
     * @param header
     * @param xml
     * @param http
     */
    public XmlHttpEvent(Header header, Node xml, HttpEventPart http) {
        super(header, xml);
        this.http = http;
        eventParts.put(HttpEventPart.HTTP_PART_NAME, http);
    }

    public XmlHttpEvent(Header header, Map<String, EventPart> eventParts) {
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
