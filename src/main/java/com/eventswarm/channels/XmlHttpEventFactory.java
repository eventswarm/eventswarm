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
package com.eventswarm.channels;

import com.eventswarm.events.Event;
import com.eventswarm.events.Header;
import com.eventswarm.events.HttpEventPart;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.*;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  XmlHttp event factory that implements the FromXmlHttp method.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class XmlHttpEventFactory implements FromXmlHttp {
    public static Map<String,Source> sources = new HashMap<String,Source>();
    public static String FROM_HEADER = "From";

    private static Logger logger = Logger.getLogger(XmlHttpEventFactory.class);

    public XmlHttpEventFactory() {
        super();
    }

    /**
     * Create a new Xml + Http Event from the provided input using the specified HttpExchange instance
     *
     * @param xml
     * @return
     */
    public Event fromXmlHttp(Node xml, HttpEventPart http) {
        String source = getSourceString(xml, http);
        Header header = new JdoHeader(http.getRequestDate(), getSource(source));
        return new XmlHttpEvent(header, xml, http);
    }

    /**
     * Create a new Source object if we haven't seen this source already, otherwise return an already-created source object
     *
     * @param source
     * @return
     */
    private Source getSource(String source) {
        Source eventSource;
        if (sources.containsKey(source)) {
            eventSource = sources.get(source);
        } else {
            eventSource = new JdoSource(source);
            sources.put(source, eventSource);
        }
        return eventSource;
    }

    /**
     * Extract the source string from a XmlObject if present
     *
     * @param xml
     * @return source string or null if no source string is provided
     */
    private String getSourceString(Node xml) {
        // TODO: extract a source name using an xpath expression
        return null;
    }

    /**
     * Extract a suitable source string from an XmlObject and HttpExchange object
     *
     * Precedence is as follows:
     * - if the Xml object has a source string, use it
     * - if a From header is present, use it
     * - elsif use hostname from the remote address
     *
     * @param http
     * @return
     */
    private String getSourceString(Node Xml, HttpEventPart http) {
        String source = getSourceString(Xml);
        if (source != null) return source;
        Map<String,List<String>> headers = http.getHeaders();
        if (headers.containsKey(FROM_HEADER)) {
            return http.getFirstHeaderValue(FROM_HEADER);
        } else {
            return http.getRemoteAddress().getHostName();
        }
    }
}
