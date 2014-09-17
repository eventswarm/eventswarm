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
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import com.eventswarm.events.serialization.OrgJsonSerializer;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class HttpSenderTest {

    HttpHandler successHandler, failHandler, badHandler, longHandler;
    Serializer serializer;
    int failCode;
    HttpServer server;
    HttpContext context;
    URL url;
    List<Event> errors;
    AddEventAction errorAction;

    static String HOSTNAME = "localhost";
    static String PORT = "3334";
    static String URL_HOST = HOSTNAME + ':' + PORT;
    static String CONTEXT = "/";
    static String URL_STRING = "http://" + URL_HOST  + CONTEXT;
    static String CONTENT_TYPE = "text/plain";

    @Before
    public void setUp() throws Exception {
        successHandler = new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                System.out.println("Received request containing:");
                printRequestContent(httpExchange);
                httpExchange.sendResponseHeaders(201, 0);
                httpExchange.close();
            }
        };
        failHandler = new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                System.out.println("Received request containing:");
                printRequestContent(httpExchange);
                httpExchange.sendResponseHeaders(failCode,0);
                httpExchange.close();
            }
        };
        badHandler = new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                throw(new RuntimeException("ha ha, I'm bad"));
            }
        };
        longHandler = new HttpHandler() {
            public void handle(HttpExchange httpExchange) throws IOException {
                try {
                    Thread.sleep(1000000);
                } catch (Exception exc) {
                    System.err.println("Timeout exception: " + exc.toString());
                }
            }
        };
        serializer = new OrgJsonSerializer();
        errors = new ArrayList<Event>();
        errorAction = new AddEventAction() { public void execute(AddEventTrigger trigger, Event event) { errors.add(event); } };
        url = new URL(URL_STRING);
        // Setup the server
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(url.getHost()), url.getPort());
        server = HttpServer.create(address, 0);
        System.out.println("Starting server");
        context = server.createContext(CONTEXT);
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop(0);
    }

    @Test
    public void testConstruct() throws Exception {
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        assertNotNull(instance);
        assertEquals(url, instance.getUrl());
        assertEquals(CONTENT_TYPE, instance.getContentType());
        assertEquals(serializer, instance.getSerializer());
    }

    @Test
    public void testFirstSend() throws Exception {
        context.setHandler(successHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(0, errors.size());
    }

    @Test
    public void testSecondSend() throws Exception {
        context.setHandler(successHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        System.out.println("First event");
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        System.out.println("Second event");
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{b:2}")));
        assertEquals(0, errors.size());
    }

    @Test
    public void testFirstFails() throws Exception {
        failCode = 500;
        context.setHandler(failHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        instance.registerAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(1, errors.size());
    }

    @Test
    public void testSecondFails() throws Exception {
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        System.out.println("First event");
        context.setHandler(successHandler);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        server.removeContext(CONTEXT);
        System.out.println("Second event");
        server.createContext(CONTEXT, failHandler);
        failCode = 500;
        instance.registerAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{b:2}")));
        assertEquals(1, errors.size());
    }

    @Test
    public void testBothFail() throws Exception {
        failCode = 500;
        context.setHandler(failHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        instance.registerAction(errorAction);
        System.out.println("First event");
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        System.out.println("Second event");
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{b:2}")));
        assertEquals(2, errors.size());
    }


    @Test
    public void testSubContextFail() throws Exception {
        HttpContext subcontext = server.createContext("/blah");
        failCode = 500;
        subcontext.setHandler(failHandler);
        context.setHandler(successHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, new URL(URL_STRING + "blah"));
        instance.registerAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(1, errors.size());
    }

    @Test
    public void testWithRetriever() throws Exception {
        ValueRetriever<URL> retriever = new ValueRetriever<URL>() {
            @Override
            public URL getValue(Event event) {
                try {
                    return new URL(URL_STRING + "blah");
                } catch (Exception exc) {
                    System.out.println(exc.getMessage());
                    return null;
                }
            }
        };
        HttpContext subcontext = server.createContext("/blah");
        subcontext.setHandler(successHandler);
        context.setHandler(failHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, retriever);
        instance.registerAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(0, errors.size());
    }

    @Test
    public void testBadUrl() throws Exception {
        context.setHandler(successHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url = new URL("http://localhost:3333"));
        instance.registerAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(1, errors.size());
    }

    @Test
    public void testServerFails() throws Exception {
        context.setHandler(badHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        instance.registerAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(1, errors.size());
    }

    @Test
    public void testServerTimeout() throws Exception {
        context.setHandler(longHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        instance.registerAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(1, errors.size());
    }


    @Test
    public void testUnregisterAction() throws Exception {
        context.setHandler(badHandler);
        HttpSender instance = new HttpSender(serializer, CONTENT_TYPE, url);
        instance.registerAction(errorAction);
        instance.unregisterAction(errorAction);
        instance.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{a:1}")));
        assertEquals(0, errors.size());
    }

    public void printRequestContent (HttpExchange exchange) throws IOException {
        byte[] data = new byte[exchange.getRequestBody().available()];
        exchange.getRequestBody().read(data);
        System.out.write(data);
        System.out.println();
    }
}
