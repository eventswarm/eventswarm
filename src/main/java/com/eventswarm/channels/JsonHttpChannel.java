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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.HttpEventPart;
import com.eventswarm.events.jdo.FromJsonHttp;
import com.eventswarm.events.jdo.JdoHttpEventPart;
import com.eventswarm.util.TriggerDelegate;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Channel implementation for receiving JSON objects via HTTP.
 *
 * This class implements the HttpHandler interface of the com.sun.net.httpserver package, allowing it to receive
 * HTTP requests containing one or more JSON objects. For each JSON object, this class creates an event and delivers
 * it to any registered AddEventActions.
 *
 * TODO: refactor to separate HTTP handling from object construction
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JsonHttpChannel implements HttpHandler, AddEventTrigger {

    public static String TYPE_ATTR = "typeId";

    private Map<String, FromJsonHttp> constructors  = new HashMap<String,FromJsonHttp>();
    private FromJsonHttp defaultConstructor;
    private TriggerDelegate<AddEventAction> trigger = new TriggerDelegate<AddEventAction>();
    private long count;
    private long errorCount;

    private static Logger logger = Logger.getLogger(JsonHttpChannel.class);

    /**
     * Create a JsonHttpChannel instance using a built-in default constructor.
     */
    public JsonHttpChannel() {
        this.defaultConstructor = new JsonHttpEventFactory();
    }

    /**
     * Create a JsonHttpChannel instance using the supplied default constructor.
     *
     * The supplied default constructor should be capable of creating an Event from any valid JSON object received
     * via the HttpHandler interface of this channel.
     *
     */
    public JsonHttpChannel(FromJsonHttp defaultConstructor) {
        this.defaultConstructor = defaultConstructor;
    }

    /**
     * Register a constructor for JSON objects with the specified typeId.
     *
     * If a constructor is already registered for the specified typeId, it will be replaced.
     *
     * @param typeId
     * @param constructor
     */
    public void registerConstructor(String typeId, FromJsonHttp constructor) {
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
    public void unregisterConstructor(String typeId, FromJsonHttp constructor) throws NoSuchConstructorException {
        if (constructor.equals(constructors.get(typeId))) {
            constructors.remove(typeId);
        }
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        JSONTokener tokener = new JSONTokener(httpExchange.getRequestBody());
        JSONObject json;
        Event event;
        try {
            for (next(tokener); tokener.more(); next(tokener)) {
                json = new JSONObject(tokener);
                HttpEventPart http = new JdoHttpEventPart(httpExchange);
                if (json.has(TYPE_ATTR) && constructors.containsKey(json.getString(TYPE_ATTR))) {
                    logger.debug("Creating event using factory for " + json.getString(TYPE_ATTR));
                    event = constructors.get(json.getString(TYPE_ATTR)).fromJsonHttp(json, http);
                    count++;
                } else {
                    logger.debug("Creating event using default factory");
                    event = defaultConstructor.fromJsonHttp(json, http);
                    count++;
                }
                for (AddEventAction action : trigger) {
                    action.execute(this, event);
                }
            }
            httpExchange.sendResponseHeaders(getSuccessCode(httpExchange.getRequestMethod()),0);
            httpExchange.close();
        } catch (Exception exc) {
            logger.error(exc);
            errorCount++;
            httpExchange.sendResponseHeaders(500,0);
            OutputStream out = httpExchange.getResponseBody();
            exc.printStackTrace(new PrintStream(out));
            out.close();
            httpExchange.close();
        }
    }

    /**
     * Return the correct success http code for the requested method
     *
     * POST = 201
     * GET = 200
     * PUT = 200
     * DELETE = 200
     *
     * @param method
     * @return
     */
    private int getSuccessCode(String method) {
        if ("POST".equals(method)) {
            return 201;
        } else {
            return 200;
        }
    }

    /**
     * Skip over whitespace to the beginning of the next non-whitespace character or the end of the input
     *
     * @param tokener
     */
    private void next(JSONTokener tokener) {
        char result = tokener.nextClean();
        // step back one if we haven't reached the end of the input
        if (result != 0) tokener.back();
    }

    public void registerAction(AddEventAction action) {
        trigger.registerAction(action);
    }

    public void unregisterAction(AddEventAction action) {
        trigger.unregisterAction(action);
    }

    public long getCount() {
        return count;
    }

    public long getErrorCount() {
        return errorCount;
    }
}
