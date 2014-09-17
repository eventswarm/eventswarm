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

import com.eventswarm.events.*;
import com.eventswarm.events.jdo.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Default JSON event factory that implements the FromJson method.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JsonEventFactory implements FromJson {
    public static Map<String,Source> sources = new HashMap<String,Source>();
    public static String DEFAULT_SOURCE = "UnnamedJsonSource";

    private String defaultSource;

    private static Logger logger = Logger.getLogger(JsonEventFactory.class);

    public JsonEventFactory() {
        super();
        try {
            defaultSource = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException exc) {
            logger.warn("Host has no name");
            defaultSource = DEFAULT_SOURCE;
        }
    }

    public JsonEventFactory(String defaultSource) {
        super();
        this.defaultSource = defaultSource;
    }

    /**
     * Create a new Json Event from the provided input and source name, using the current date as the event date
     *
     * @param json
     * @return
     */
    @Override
    public Event fromJson(JSONObject json) {
        Header header = new JdoHeader(new Date(), getSource(getSourceString(json)));
        return new OrgJsonEvent(header, json);
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
     * Extract the source string from a JsonObject if present or return the default source string
     *
     * @param json
     * @return source string or null if no source string is provided
     */
    private String getSourceString(JSONObject json) {
        if (json.has(FromJson.SOURCE_ATTRIBUTE_NAME)) {
            return json.getString(FromJson.SOURCE_ATTRIBUTE_NAME);
        } else {
            return defaultSource;
        }
    }
}
