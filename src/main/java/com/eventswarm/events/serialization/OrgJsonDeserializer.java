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
package com.eventswarm.events.serialization;

import com.eventswarm.channels.Deserializer;
import com.eventswarm.events.Event;
import com.eventswarm.events.Header;
import com.eventswarm.events.Sources;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Deserializer implementation that parses the supplied bytes/string into a JSON object
 * and constructs an EventSwarm object using the header fields in the supplied json bytes/string.
 * <p/>
 * The deserializer chooses a class constructor based on a source -> class mapping. If no mapping is
 * provided for a nominated event source, then the default OrgJsonEvent constructor is used.
 * <p/>
 * This deserializer will fail if the EventSwarm header object is not present in the JSON
 */
public class OrgJsonDeserializer implements Deserializer {
    private Map<String, Class<? extends Event>> targetClasses;
    private Map<String, Constructor<? extends Event>> constructors = new HashMap<String, Constructor<? extends Event>>();
    private Constructor<OrgJsonEvent> defaultCons;
    private OrgJsonEventFactory factory;
    
    private static Logger logger = Logger.getLogger(OrgJsonDeserializer.class);

    /**
     * Constructor that accepts an externally defined factory for creating events
     */
    public OrgJsonDeserializer(OrgJsonEventFactory factory) {
        this.targetClasses = null;
        this.factory = factory;
    }

    /**
     * Constructor that supports the specification of event source -> target class mappings and creates a default
     * factory from this mapping.
     * <p/>
     * If no usable mapping is provided for an event source, an OrgJsonEvent will be created.
     *
     * @param targetClasses
     */
    public OrgJsonDeserializer(Map<String, Class<? extends Event>> targetClasses) {
        this.targetClasses = targetClasses;
        this.factory = defaultFactory(targetClasses);
    }

    /**
     * The default constructor can be used if all events can be created as OrgJsonEvents.
     */
    public OrgJsonDeserializer() {
        this.targetClasses = null;
        this.factory = defaultFactory(targetClasses);
    }

    private OrgJsonEventFactory defaultFactory(Map<String, Class<? extends Event>> targetClasses) {
        setDefaultCons();
        if (targetClasses != null) {
            for (String source : targetClasses.keySet()) {
                try {
                    logger.debug("Using class " + targetClasses.get(source).getName() + " for events from " + source);
                    constructors.put(source, targetClasses.get(source).getConstructor(Header.class, JSONObject.class));
                } catch (NoSuchMethodException exc) {
                    logger.error("No suitable JSONObject constructor for class " + targetClasses.get(source).getName());
                }
            }
        }
        return new OrgJsonEventFactory() {
            public Event create(Header header, JSONObject json) throws DeserializeException {
                try {
                    return getConstructor(header).newInstance(header, json);
                } catch (Exception exc) {
                    throw new DeserializeException(exc);
                }
            }
        };
    }

    private void setDefaultCons() {
        try {
            defaultCons = OrgJsonEvent.class.getConstructor(Header.class, JSONObject.class);
        } catch (NoSuchMethodException exc) {
            logger.fatal("What the? OrgJsonEvent doesn't have a (Header,JSONObject) constructor");
        }
    }

    protected Constructor<? extends Event> getConstructor(Header header) {
        String source = header.getSource().getSourceId();
        Constructor<? extends Event> cons = constructors.get(source);
        if (cons == null) {
            logger.debug ("Using default OrgJsonEvent constructor");
            return defaultCons;
        } else {
            logger.debug ("Using specific constructor for " + source);
            return cons;
        }
    }

    public Event fromBytes(byte[] bytes) throws DeserializeException {
        JSONObject json;
        try {
            json = new JSONObject(new JSONTokener(new ByteArrayInputStream(bytes)));
        } catch (JSONException ex) {
            throw new DeserializeException("Error parsing json", ex);
        }
        return construct(json);
    }

    public Event fromString(String string) throws DeserializeException {
        JSONObject json;
        try {
            json = new JSONObject(string);
        } catch (JSONException ex) {
            throw new DeserializeException("Error parsing json", ex);
        }
        return construct(json);
    }

    /**
     * Additional deserializer that takes a native JSONObject, for circumstances where the JSON is pre-parsed
     * (e.g. when calling from ruby with elasticsearch results or as a subevent)
     *
     * @param json An already-constructed JSON object
     * @return
     * @throws DeserializeException
     */
    public Event fromJsonObject(JSONObject json) throws DeserializeException {
        return construct(json);
    }

   protected Event construct(JSONObject json) throws DeserializeException {
        Header header = getEventSwarmHeader(json);
        json.remove(OrgJsonEvent.JSON_HEADER_OBJECT);
        return factory.create(header, json);
    }

    public Header getEventSwarmHeader(JSONObject json) throws DeserializeException {
        try {
            JSONObject jsonHdr = json.getJSONObject(OrgJsonEvent.JSON_HEADER_OBJECT);
            if (jsonHdr == null) {
                throw new DeserializeException("Not an EventSwarm object: no header fields");
            } else {
                return new JdoHeader(new Date(jsonHdr.getLong(OrgJsonEvent.JSON_TIMESTAMP)),
                        jsonHdr.getInt(OrgJsonEvent.JSON_SEQUENCE_NO),
                        Sources.cache.getSourceByName(jsonHdr.getString(OrgJsonEvent.JSON_SOURCE_ID)),
                        null, null, null,
                        jsonHdr.getString(OrgJsonEvent.JSON_EVENT_ID));

            }
        } catch (Exception exc) {
            throw new DeserializeException("Error extracting header", exc);
        }
    }
}