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

import com.eventswarm.events.Header;
import com.eventswarm.events.HttpEventPart;
import com.eventswarm.events.JsonEvent;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class OrgJsonHttpEventTest {

    Header header;
    HttpEventPart http;

    @Before
    public void setup() {
        header = new JdoHeader(new Date(), new JdoSource("localhost"));
        // TODO: add tests for HTTP methods
        http = null; // new JdoHttpEventPart(Map<String, List<String>> headers, InetSocketAddress remoteAddress, String requestMethod, URI requestUri) {
    }

    @Test
    public void getInt() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: 2}"), http);
        assertEquals(1, instance.getInt("a"));
    }

    @Test
    public void getIntViaPath() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:{c:1}, b: 2}"), http);
        assertEquals(1, instance.getInt("a/c"));
    }

    @Test
    public void getBoolean() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: true}"), http);
        assertEquals(true, instance.getBoolean("b"));
    }

    @Test
    public void getBooleanViaPath() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b:{c: true}}"), http);
        assertEquals(true, instance.getBoolean("b/c"));
    }

    @Test
    public void getDouble() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1.15, b: 2}"), http);
        assertEquals(1.15, instance.getDouble("a"), 0.0);
    }

    @Test
    public void getDoubleViaPath() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:{c:1.15}, b: 2}"), http);
        assertEquals(1.15, instance.getDouble("a/c"), 0.0);
    }

    @Test
    public void getLong() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1.15, b: 1038401717746111341}"), http);
        assertEquals(1038401717746111341L, instance.getLong("b"));
    }

    @Test
    public void getLongViaPath() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1.15, b: {c:1038401717746111341}}"), http);
        assertEquals(1038401717746111341L, instance.getLong("b/c"));
    }

    @Test
    public void getString() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:'howzat', b: 2}"), http);
        assertEquals("howzat", instance.getString("a"));
    }

    @Test
    public void getStringViaPath() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:{c:'howzat'}, b: 2}"), http);
        assertEquals("howzat", instance.getString("a/c"));
    }

    @Test
    public void jsonObject() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: {c:2}}"), http);
        JSONObject result = instance.getJsonObject("b");
        assertNotNull(result);
        assertEquals(2, result.getInt("c"));
    }

    @Test
    public void jsonObjectViaPath() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: {c:{c:2}}}"), http);
        JSONObject result = instance.getJsonObject("b/c");
        assertNotNull(result);
        assertEquals(2, result.getInt("c"));
    }

    @Test
    public void jsonArray() throws Exception {
        OrgJsonHttpEvent instance = new OrgJsonHttpEvent(header, new JSONObject("{a:[1, 2]}"), http);
        JSONArray result = instance.getJSONArray("a");
        assertNotNull(result);
        assertEquals(2, result.getInt(1));
    }

    @Test
    public void jsonArrayViaPath() throws Exception {
        OrgJsonHttpEvent instance  = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: {c:[1,2]}}"), http);
        JSONArray result = instance.getJSONArray("b/c");
        assertNotNull(result);
        assertEquals(2, result.getInt(1));
    }

    @Test
    public void jsonObjectFromArrayViaPath() throws Exception {
        OrgJsonHttpEvent instance  = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: {c:[{e:3},2]}}"), http);
        JSONArray result = instance.getJSONArray("b/c");
        assertNotNull(result);
        assertEquals(3, result.getJSONObject(0).getInt("e"));
    }

    @Test
    public void integerRetriever() throws Exception {
        JsonEvent.IntegerRetriever retriever = new JsonEvent.IntegerRetriever("a");
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: 2}"), http);
        assertEquals(1, (int) retriever.getValue(instance));
    }

    @Test
    public void longRetriever() throws Exception {
        JsonEvent.LongRetriever retriever = new JsonEvent.LongRetriever("a");
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1038401717746111341, b: 2}"), http);
        assertEquals(1038401717746111341L, (long) retriever.getValue(instance));
    }

    @Test
    public void doubleRetriever() throws Exception {
        JsonEvent.DoubleRetriever retriever = new JsonEvent.DoubleRetriever("b");
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: 3.14159}"), http);
        assertEquals(3.14159, (double) retriever.getValue(instance), 0.0);
    }

    @Test
    public void stringRetriever() throws Exception {
        JsonEvent.StringRetriever retriever = new JsonEvent.StringRetriever("b");
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: 'howzat'}"), http);
        assertEquals("howzat", retriever.getValue(instance));
    }


    @Test
    public void downcaseStringRetriever() throws Exception {
        JsonEvent.DowncaseStringRetriever retriever = new JsonEvent.DowncaseStringRetriever("b");
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: 'Howzat'}"), http);
        assertEquals("howzat", retriever.getValue(instance));
    }

    @Test
    public void booleanRetriever() throws Exception {
        JsonEvent.BooleanRetriever retriever = new JsonEvent.BooleanRetriever("a");
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:true, b: 'howzat'}"), http);
        assertEquals(true, retriever.getValue(instance));
    }

    @Test
    public void jsonToString() throws Exception {
        JsonEvent.DowncaseStringRetriever retriever = new JsonEvent.DowncaseStringRetriever("b");
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:1, b: 'Howzat'}"), http);
        JSONObject result = new JSONObject(instance.getJsonString());
        assertEquals(2, result.keySet().size());
        assertEquals(1, result.getInt("a"));
        assertEquals("Howzat", result.getString("b"));
    }

    @Test
    public void getPathWithLeadingSeparator() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:{c:1}, b: 2}"), http);
        assertEquals(1, instance.getInt("/a/c"));
    }

    @Test
    public void getPathWithMultipleSeparators() throws Exception {
        JsonEvent<JSONObject> instance = new OrgJsonHttpEvent(header, new JSONObject("{a:{c:1,d:{e:5}}, b: 2}"), http);
        assertEquals(5, instance.getInt("a/d/e"));
    }
}
