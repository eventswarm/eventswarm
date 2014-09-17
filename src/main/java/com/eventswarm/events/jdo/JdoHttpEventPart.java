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
import com.eventswarm.events.HttpEventPart;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JdoHttpEventPart implementation constructing the EventPart
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoHttpEventPart implements HttpEventPart {
    private Event event = null;
    private Map<String,List<String>> headers;
    private InetSocketAddress remoteAddress;
    private String requestMethod;
    private URI requestUri;
    private transient Date requestDate = null;

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyy HH:mm:ss zzzz");
    public static String DATE_HEADER = "Date";

    /**
     * Hide default constructor, only required for persistence
     */
    private JdoHttpEventPart() {
    }

    /**
     * Convenience constructor for creating from an HttpExchange object
     *
     * @param httpExchange
     */
    public JdoHttpEventPart(HttpExchange httpExchange) {
        this(httpExchange.getRequestHeaders(), httpExchange.getRemoteAddress(),
             httpExchange.getRequestMethod(), httpExchange.getRequestURI());
    }

    public JdoHttpEventPart(Map<String, List<String>> headers, InetSocketAddress remoteAddress, String requestMethod, URI requestUri) {
        this.headers = headers;
        this.remoteAddress = remoteAddress;
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    private void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String getRequestMethod() {
        return requestMethod;
    }

    private void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    @Override
    public URI getRequestUri() {
        return requestUri;
    }

    @Override
    public Date getRequestDate() {
        if (this.requestDate == null) {
            try {
                this.requestDate = dateFormat.parse(headers.get(DATE_HEADER).get(0));
            } catch (Exception exc) {
                // if no parse-able date (should never happen) use the current time
                this.requestDate = new Date();
            }
        }
        return requestDate;
    }

    private void setRequestUri(URI requestUri) {
        this.requestUri = requestUri;
    }

    @Override
    public Map<String,List<String>> getHeaders() {
        return headers;
    }

    private void setHeaders(Headers headers) {
        this.headers = headers;
    }

    @Override
    public List<String> getHttpHeader(String name) {
        return headers.get(name);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getFirstHeaderValue(String name) {
        return headers.get(name).get(0);
    }
}
