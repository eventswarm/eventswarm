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
import com.eventswarm.events.jdo.FromJson;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JsonChannel extends AbstractChannel {
    private transient JSONTokener tokener;
    private Map<String, FromJson> constructors = new HashMap<String,FromJson>();
    private FromJson defaultConstructor;


    public JsonChannel(InputStream stream) {
        setTokener(stream);
        defaultConstructor = new JsonEventFactory();
    }

    public JsonChannel(InputStream stream, FromJson defaultConstructor) {
        setTokener(stream);
        this.defaultConstructor = defaultConstructor;
    }

    /**
     * Set or change the input stream used for reading JSON objects
     *
     * @param stream
     */
    public void setTokener(InputStream stream) {
        this.tokener = new JSONTokener(stream);
    }

    @Override
    public void setup() throws Exception {
        // nothing to do here
    }

    @Override
    public void teardown() throws Exception {
        // nothing to do here
    }

    @Override
    public Event next() throws Exception {
        if (tokener.more()) {
            JSONObject json = new JSONObject(tokener);
            return getConstructor(json).fromJson(json);
        } else {
            stop();
            return null;
        }
    }

    /**
     * Work out which constructor to use for the supplied JSON object
     *
     * If the json has a typeId attribute and a constructor is registered for this type, then use it.
     * Otherwise use the default.
     *
     * @param json
     * @return
     */
    private FromJson getConstructor(JSONObject json) {
        if (json.has(FromJson.TYPE_ATTRIBUTE_NAME) && constructors.containsKey(json.getString(FromJson.TYPE_ATTRIBUTE_NAME))) {
            return constructors.get(json.getString(FromJson.TYPE_ATTRIBUTE_NAME));
        } else {
            return defaultConstructor;
        }
    }

    /**
     * Register a constructor for JSON objects with the specified typeId.
     *
     * If a constructor is already registered for the specified typeId, it will be replaced.
     *
     * @param typeId
     * @param constructor
     */
    public void registerConstructor(String typeId, FromJson constructor) {
        constructors.put(typeId, constructor);
    }

    /**
     * Unregister the constructor for JSON objects with the specified type URI.
     *
     * If the specified URI and constructor pair is not registered, .
     *
     * @param typeId
     * @param constructor
     */
    public void unregisterConstructor(String typeId, FromJson constructor) throws NoSuchConstructorException {
        if (constructor.equals(constructors.get(typeId))) {
            constructors.remove(typeId);
        }
    }
}
