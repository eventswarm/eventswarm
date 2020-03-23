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

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * TODO: remove all the HttpServer stuff from here and use the simplified constructor
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoHttpEventPartTest implements HttpHandler {
    HttpExchange httpExchange;
    HttpContext context;
    HttpServer server;
    URL url;
    static String HOSTNAME = "localhost";
    static String PORT = "3333";
    static String URL_HOST = HOSTNAME + ':' + PORT;
    static String CONTEXT = "/";
    static String URL_STRING = "http://" + URL_HOST  + CONTEXT;

    @Before
    public void setUp() throws Exception {
        // get an HttpServer instance
        url = new URL(URL_STRING);
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(url.getHost()), url.getPort());
        server = HttpServer.create(address, 0);
        server.createContext(CONTEXT, this);
        System.out.println("Starting server");
        server.start();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Stopping server");
        server.stop(0);
    }

    @Test
    public void connect() throws Exception {
        JdoHttpEventPart instance;
        send("POST", "{a: 1, b: 2}", null);
        instance = new JdoHttpEventPart(httpExchange);
        assertNotNull(instance);
    }

    @Test
    public void remoteAddress() throws Exception {
        JdoHttpEventPart instance;
        send("POST", "{a: 1, b: 2}", null);
        instance = new JdoHttpEventPart(httpExchange);
        assertEquals(HOSTNAME, instance.getRemoteAddress().getHostName());
    }

    @Test
    public void requestMethod() throws Exception {
        JdoHttpEventPart instance;
        send("POST", "{a: 1, b: 2}", null);
        instance = new JdoHttpEventPart(httpExchange);
        assertEquals("POST", instance.getRequestMethod());
    }

    @Test
    public void requestUri() throws Exception {
        JdoHttpEventPart instance;
        send("POST", "{a: 1, b: 2}", null);
        instance = new JdoHttpEventPart(httpExchange);
        assertEquals(CONTEXT, instance.getRequestUri().toString());
    }

    @Test
    public void getRequestDate() throws Exception {
        JdoHttpEventPart instance;
        Map<String,String> headers = new HashMap<String,String>();
        headers.put(JdoHttpEventPart.DATE_HEADER, "Fri, 16 Aug 2013 00:30:00 GMT");
        send("POST", "{a: 1, b: 2}", headers);
        instance = new JdoHttpEventPart(httpExchange);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.set(2013, 7, 16, 0, 30, 0);
        assertEquals(calendar.getTimeInMillis()/1000L, instance.getRequestDate().getTime()/1000L);
    }

    @Test
    public void getHeaders() throws Exception {
        JdoHttpEventPart instance;
        send("POST", "{a: 1, b: 2}", null);
        instance = new JdoHttpEventPart(httpExchange);
        assertNotNull(instance.getHeaders());
        Map<String,List<String>> headers = instance.getHeaders();
        for (String key : headers.keySet()) {
            System.out.print(key + ": ");
            for (String value : headers.get(key)) {
                System.out.print(value + "; ");
            }
            System.out.println();
        }
        assertTrue(headers.containsKey("Host"));
        assertTrue(headers.containsKey("Content-length"));
        assertTrue(headers.containsKey("Accept"));
        assertTrue(headers.containsKey("User-agent"));
    }

    @Test
    public void getHttpHeader() throws Exception {
        JdoHttpEventPart instance;
        send("POST", "{a: 1, b: 2}", null);
        instance = new JdoHttpEventPart(httpExchange);
        List<String> list = instance.getHttpHeader("Host");
        assertNotNull(list);
        assertEquals(URL_HOST, list.get(0));
    }

    @Test
    public void firstHeaderValue() throws Exception {
        JdoHttpEventPart instance;
        send("POST", "{a: 1, b: 2}", null);
        instance = new JdoHttpEventPart(httpExchange);
        String host = instance.getFirstHeaderValue("Host");
        assertNotNull(host);
        assertEquals(URL_HOST, host);
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Received request from " + httpExchange.getRemoteAddress().toString());
        this.httpExchange = httpExchange;
        System.out.println("Sending response");
        httpExchange.sendResponseHeaders(200,0);
        httpExchange.close();
    }

    public void send(String method, String content, Map<String,String> headers) throws Exception {
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
        InputStream in = con.getInputStream();
        in.close();
        System.out.println("Received HTTP response");
    }
}
