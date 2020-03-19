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
package com.eventswarm.events;

import com.eventswarm.abstractions.ValueRetriever;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for events created as a result of an HTTP request
 *
 * Note that the request body is not accessible through this since a single HTTP request might result in
 * multiple events derived from the request body. Thus the focus of this class is on header and request information.
 * This interface just extends the HttpEventPart interface since all the information we require is there.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface HttpRequestEvent extends HttpEventPart {

    /**
     * Retriever class to grab the first header value from a header in an HttpRequestEvent
     */
    public static class FirstHeaderRetriever implements ValueRetriever<String> {
        String key;

        public FirstHeaderRetriever(String key) {
            this.key = key;
        }

        public String getValue(Event event) {
            return (HttpRequestEvent.class.isInstance(event) ? ((HttpRequestEvent) event).getFirstHeaderValue(key) : null);
        }
    }

    /**
     * Retriever instance to grab the request method
     */
    public static ValueRetriever<String> REQUEST_METHOD_RETRIEVER = new ValueRetriever<String>() {
        public String getValue(Event event) {
            return (HttpRequestEvent.class.isInstance(event) ? ((HttpRequestEvent) event).getRequestMethod() : null);
        }
    };

    /**
     * Retriever instance to grab the source hostname
     */
    public static ValueRetriever<String> SOURCE_HOST_RETRIEVER = new ValueRetriever<String>() {
        public String getValue(Event event) {
            return (HttpRequestEvent.class.isInstance(event) ? ((HttpRequestEvent) event).getRemoteAddress().getHostName() : null);
        }
    };

    /**
     * Retriever instance to grab the source IP address
     */
    public static ValueRetriever<String> SOURCE_IP_RETRIEVER = new ValueRetriever<String>() {
        public String getValue(Event event) {
            return (HttpRequestEvent.class.isInstance(event) ? ((HttpRequestEvent) event).getRemoteAddress().getAddress().getHostAddress() : null);
        }
    };

    /**
     * Retriever instance to grab the request date
     */
    public static ValueRetriever<Date> REQUEST_DATE_RETRIEVER = new ValueRetriever<Date>() {
        public Date getValue(Event event) {
            return (HttpRequestEvent.class.isInstance(event) ? ((HttpRequestEvent) event).getRequestDate() : null);
        }
    };
}
