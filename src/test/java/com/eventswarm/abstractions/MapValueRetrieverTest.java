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
package com.eventswarm.abstractions;

import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MapValueRetrieverTest {
    ValueRetriever<Integer> retriever;
    Map<Integer,String> map;
    Source source = new JdoSource();

    @Before
    public void setUp() throws Exception {
        retriever = new JsonEvent.IntegerRetriever("value");
        map = new HashMap<Integer,String>();
        map.put(1, "one");
        map.put(2, "two");
        map.put(3, "three");
    }

    @Test
    public void testGetValueFirst() throws Exception {
        MapValueRetriever<Integer,String> instance = new MapValueRetriever<Integer, String>(map, retriever);
        String result = instance.getValue(makeEvent(1));
        assertEquals("one", result);
    }

    @Test
    public void testGetValueLast() throws Exception {
        MapValueRetriever<Integer,String> instance = new MapValueRetriever<Integer, String>(map, retriever);
        String result = instance.getValue(makeEvent(3));
        assertEquals("three", result);
    }

    @Test
    public void testGetValueNone() throws Exception {
        MapValueRetriever<Integer,String> instance = new MapValueRetriever<Integer, String>(map, retriever);
        String result = instance.getValue(makeEvent(4));
        assertNull(result);
    }

    private Event makeEvent(Integer value) {
        return new OrgJsonEvent(new JdoHeader(0L, "MapValueRetrieverTest"), new JSONObject("{'value':" + value.toString() + "}"));
    }
}
