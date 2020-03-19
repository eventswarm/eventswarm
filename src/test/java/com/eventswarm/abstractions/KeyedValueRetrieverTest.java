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
import com.eventswarm.events.jdo.JdoHeader;
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
public class KeyedValueRetrieverTest {
    static String SOURCE1 = "source1",
                  SOURCE2 = "source2",
                  VALUE1 = "value1",
                  VALUE2 = "value2";
    Map<String, ValueRetriever<Integer>> map;

    @Before
    public void setUp() throws Exception {
        map = new HashMap<String, ValueRetriever<Integer>>();
        map.put(SOURCE1, new JsonEvent.IntegerRetriever(VALUE1));
        map.put(SOURCE2, new JsonEvent.IntegerRetriever(VALUE2));
    }

    @Test
    public void testGetValueFirst() throws Exception {
        KeyedValueRetriever<String,Integer> instance =
                new KeyedValueRetriever<String,Integer>(new Event.SourceRetriever(), map);
        Integer result = instance.getValue(makeEvent(SOURCE1, 1, 2));
        assertEquals(1, (int) result);
    }

    @Test
    public void testGetValueSecond() throws Exception {
        KeyedValueRetriever<String,Integer> instance =
                new KeyedValueRetriever<String,Integer>(new Event.SourceRetriever(), map);
        Integer result = instance.getValue(makeEvent(SOURCE2, 1, 2));
        assertEquals(2, (int) result);
    }

    @Test
    public void testGetValueNoRetriever() throws Exception {
        KeyedValueRetriever<String,Integer> instance =
                new KeyedValueRetriever<String,Integer>(new Event.SourceRetriever(), map);
        Integer result = instance.getValue(makeEvent("othersource", 1, 2));
        assertNull(result);
    }

    @Test
    public void testGetValueNoValue() throws Exception {
        KeyedValueRetriever<String,Integer> instance =
                new KeyedValueRetriever<String,Integer>(new Event.SourceRetriever(), map);
        Integer result = instance.getValue(makeEvent(SOURCE2, 1, null));
        assertNull(result);
    }

    private Event makeEvent(String source, Integer value1, Integer value2) {
        String val1, json;
        val1 = "'" + VALUE1 + "':" + value1.toString();
        if (value2 != null)
            json = "{" + val1 + ", '" + VALUE2 + "':" + value2.toString() + "}";
        else
            json = "{" + val1 + "}";

        return new OrgJsonEvent(new JdoHeader(0L, source), new JSONObject(json));
    }

}
