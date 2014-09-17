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
import com.eventswarm.events.HttpRequestEvent;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.*;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JsonHttpChannelTest implements AddEventAction, FromJsonHttp {
    HttpServer server;
    HttpContext context;
    URL url;
    static String HOSTNAME = "localhost";
    static String PORT = "3334";
    static String URL_HOST = HOSTNAME + ':' + PORT;
    static String CONTEXT = "/";
    static String URL_STRING = "http://" + URL_HOST  + CONTEXT;
    static String TEST_SOURCE = "JsonHttpChannelTest";
    static String TEST_TYPE_ID = "JsonHttpChannelTest";
    List<Event> events = new LinkedList<Event>();
    JdoSource source = new JdoSource(TEST_SOURCE);

    @Before
    public void setUp() throws Exception {
        // configure our target URL
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
    public void handleSingle() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        int code = send("POST", "{a:1, b:2}", null);
        assertEquals(201, code);
        assertEquals(1, events.size());
        assertTrue(JsonEvent.class.isInstance(events.get(0)));
        assertTrue(HttpRequestEvent.class.isInstance(events.get(0)));
        OrgJsonHttpEvent event = (OrgJsonHttpEvent) events.get(0);
        assertEquals(1, event.getInt("a"));
        assertEquals(2, event.getInt("b"));
    }

    @Test
    public void precedingWhitespace() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        int code = send("POST", " \n {a:1, b:2}", null);
        assertEquals(201, code);
        assertEquals(1, events.size());
        assertTrue(JsonEvent.class.isInstance(events.get(0)));
        assertTrue(HttpRequestEvent.class.isInstance(events.get(0)));
        OrgJsonHttpEvent event = (OrgJsonHttpEvent) events.get(0);
        assertEquals(1, event.getInt("a"));
        assertEquals(2, event.getInt("b"));
    }

    @Test
    public void trailingWhitespace() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        int code = send("POST", "{a:1, b:2} \n ", null);
        assertEquals(201, code);
        assertEquals(1, events.size());
        assertTrue(JsonEvent.class.isInstance(events.get(0)));
        assertTrue(HttpRequestEvent.class.isInstance(events.get(0)));
        OrgJsonHttpEvent event = (OrgJsonHttpEvent) events.get(0);
        assertEquals(1, event.getInt("a"));
        assertEquals(2, event.getInt("b"));
    }

    @Test
    public void handleMultiple() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        int code = send("POST", "{a:1, b:2}{a:2, b:1}", null);
        assertEquals(201, code);
        assertEquals(2, events.size());
        assertTrue(JsonEvent.class.isInstance(events.get(1)));
        assertTrue(HttpRequestEvent.class.isInstance(events.get(1)));
        OrgJsonHttpEvent event = (OrgJsonHttpEvent) events.get(1);
        assertEquals(2, event.getInt("a"));
        assertEquals(1, event.getInt("b"));
    }

    @Test
    public void intermediateWhitespace() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        int code = send("POST", "{a:1, b:2} \n {a:2, b:1}", null);
        assertEquals(201, code);
        assertEquals(2, events.size());
        assertTrue(JsonEvent.class.isInstance(events.get(1)));
        assertTrue(HttpRequestEvent.class.isInstance(events.get(1)));
        OrgJsonHttpEvent event = (OrgJsonHttpEvent) events.get(1);
        assertEquals(2, event.getInt("a"));
        assertEquals(1, event.getInt("b"));
    }

    @Test
    public void handleEmpty() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        int code = send("POST", "", null);
        assertEquals(201, code);
        assertEquals(0, events.size());
    }

    @Test
    public void handleError() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        int code = send("POST", "{a: 1, b:", null);
        assertEquals(500, code);
        assertEquals(0, events.size());
    }

    @Test
    public void registerConstructor() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        instance.registerConstructor(TEST_TYPE_ID, this);
        int code = send("POST", "{typeId: 'JsonHttpChannelTest', a:1, b:2}", null);
        assertEquals(201, code);
        assertEquals(TEST_SOURCE, events.get(0).getHeader().getSource().getSourceId());
    }

    @Test
    public void testUnregisterConstructor() throws Exception {
        JsonHttpChannel instance = new JsonHttpChannel();
        context.setHandler(instance);
        instance.registerAction(this);
        instance.registerConstructor(TEST_TYPE_ID, this);
        int code = send("POST", "{typeId: 'JsonHttpChannelTest', a:1, b:2}", null);
        instance.unregisterConstructor(TEST_TYPE_ID, this);
        code = send("POST", "{typeId: 'JsonHttpChannelTest', a:2, b:1}", null);
        assertEquals(201, code);
        assertEquals(TEST_SOURCE, events.get(0).getHeader().getSource().getSourceId());
        assertFalse(TEST_SOURCE.equals(events.get(1).getHeader().getSource().getSourceId()));
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        events.add(event);
        System.out.println("Event received from channel and saved");
    }

    @Override
    public Event fromJsonHttp(JSONObject json, HttpEventPart http) {
        JdoHeader header = new JdoHeader(new Date(), source);
        return new OrgJsonHttpEvent(header, json, http);
    };

    public int send(String method, String content, Map<String,String> headers) throws Exception {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod(method);
        if (headers != null) {
            for (String key : headers.keySet()) {
                con.setRequestProperty(key, headers.get(key));
            }
        }
        System.out.println("Sending HTTP request");
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(content);
        out.close();
        if (con.getResponseCode() >= 500) {
            InputStream in = con.getErrorStream();
            byte[] result = new byte[in.available()];;
            in.read(result);
            System.out.println(new String(result));
        } else {
            InputStream in = con.getInputStream();
            in.close();
        }
        System.out.println("Received HTTP response with code " + Integer.toString(con.getResponseCode()));
        return con.getResponseCode();
    }
}
