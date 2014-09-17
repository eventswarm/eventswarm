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

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Event interface for HTTP client errors
 *
 * Note that the HTTP return code should be returned by the EventError getErrorCode method. The response
 * should will be captured in the event header.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface HttpErrorEvent extends ErrorEvent {
    /**
     * @return the URL associated with the error response
     */
    public URL getUrl();

    /**
     * @return the HTTP headers from the error response
     */
    public Map<String,List<String>> getResponseHeaders();


    /**
     * Retriever class that returns the URL as a URL object
     */
    static class URLRetriever implements ValueRetriever<URL> {
        @Override
        public URL getValue(Event event) {
            if (HttpErrorEvent.class.isInstance(event)) {
                return ((HttpErrorEvent) event).getUrl();
            } else {
                return null;
            }
        }
    }

    /**
     * Retriever class that returns the URL as a string
     */
    static class URLStringRetriever implements ValueRetriever<String> {
        @Override
        public String getValue(Event event) {
            if (HttpErrorEvent.class.isInstance(event)) {
                return ((HttpErrorEvent) event).getUrl().toString();
            } else {
                return null;
            }
        }
    }

    /**
     * Retriever class that returns a specified header value as a string, selecting
     * the first value if there is more than one.
     */
    static class HeaderRetriever implements ValueRetriever<String> {
        private String headerName;

        public HeaderRetriever(String headerName) {
            this.headerName = headerName;
        }

        @Override
        public String getValue(Event event) {
            if (HttpErrorEvent.class.isInstance(event)) {
                return ((HttpErrorEvent) event).getResponseHeaders().get(headerName).get(0);
            } else {
                return null;
            }
        }
    }
}
