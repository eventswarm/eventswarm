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

import com.eventswarm.events.HttpErrorEvent;
import com.eventswarm.events.Source;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Implementation of an the HttpErrorEvent interface for errors returned by a UrlConnection object.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class UrlConnectionErrorEvent extends JdoErrorEvent implements HttpErrorEvent {
    Map<String,List<String>> responseHeaders;
    URL url;

    private static Logger logger = Logger.getLogger(UrlConnectionErrorEvent.class);
    private static Source source;

    /**
     * Error code used if the constructor fails due to an IO error
     */
    public static int CONSTRUCTOR_IO_ERROR = -1;

    /**
     * Static initializer for event source: use IP address
     */


    /**
     * Create an HttpErrorEvent by reading the error response from the supplied connection.
     *
     * Note that this method does the reading of the the errorMessage and retrieval of responseHeaders from
     * the supplied connection, so should be considered brittle. An IOException is passed back if connection
     * errors occur, and callers are responsible for reporting that error if required.
     *
     * @param connection
     */
    public UrlConnectionErrorEvent(HttpURLConnection connection) throws IOException {
        super();
        this.url = connection.getURL();
        this.responseHeaders = connection.getHeaderFields();
        this.errorCode = connection.getResponseCode();
        InputStream in = connection.getErrorStream();
        byte[] result = new byte[in.available()];;
        in.read(result);
        in.close();
        this.errorMessage = new String(result);
        logger.debug("HTTP error response was: " + this.errorMessage);
    }


    /**
     * Create an HttpErrorEvent using the supplied parameters and the current time local time
     *
     * This constructor is intended for test stub purposes, or to allow alternate implementations for
     * retrieval of error code, message and headers
     *
     * @param errorCode
     * @param errorMessage
     * @param responseHeaders
     */
    public UrlConnectionErrorEvent(Integer errorCode, String errorMessage, Map<String, List<String>> responseHeaders, URL url) {
        super(errorCode, errorMessage);
        this.responseHeaders = responseHeaders;
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    // Getters and setters for persistence, with setters private

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    private void setResponseHeaders(Map<String, List<String>> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
}
