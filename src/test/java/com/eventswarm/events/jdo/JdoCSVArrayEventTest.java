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

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.CSVEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCSVArrayEventTest {
    private Map<String,Integer> fieldMap;
    private JdoCSVArrayEvent instance;
    private JdoHeader header;
    private static JdoSource SOURCE = new JdoSource("JdoCSVArrayEventTest");

    @Before
    public void setup() throws Exception {
        header = new JdoHeader(new Date(), SOURCE);
        fieldMap = new HashMap<String,Integer>();
        fieldMap.put("Zero", 0);
        fieldMap.put("One", 1);
        fieldMap.put("Two", 2);
    }

    @Test
    public void testConstruct() throws Exception {
        String[] values = {"zero", "one", "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        assertNotNull(instance);
    }


    @Test
    public void testGet() throws Exception {
        String[] values = {"zero", "one", "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        assertEquals("zero", instance.get("Zero"));
        assertEquals("one", instance.get("One"));
        assertEquals("two", instance.get("Two"));
    }

    @Test
    public void testLessValues() throws Exception {
        String[] values = {"zero", "one"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        assertEquals("zero", instance.get("Zero"));
        assertEquals("one", instance.get("One"));
        assertNull(instance.get("Two"));
    }

    @Test
    public void testMoreValues() throws Exception {
        String[] values = {"zero", "one", "two", "three"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        assertEquals("zero", instance.get("Zero"));
        assertEquals("one", instance.get("One"));
        assertEquals("two", instance.get("Two"));
    }

    @Test
    public void testGetCsvMap() throws Exception {
        String[] values = {"zero", "one", "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        Map<String,String> map = instance.getCsvMap();
        assertEquals("zero", map.get("Zero"));
        assertEquals("one", map.get("One"));
        assertEquals("two", map.get("Two"));
    }

    @Test
    public void testGetCsvMapCompact() throws Exception {
        String[] values = {"zero", null, "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        Map<String,String> map = instance.getCsvMap();
        assertFalse(map.containsKey("One"));
        assertEquals("zero", map.get("Zero"));
        assertEquals("two", map.get("Two"));
    }

    @Test
    public void testGetCsvMapNotCompact() throws Exception {
        String[] values = {"zero", null, "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        instance.setCompact(false);
        Map<String,String> map = instance.getCsvMap();
        assertTrue(map.containsKey("One"));
        assertNull(map.get("One"));
    }

    @Test
    public void testStringRetriever() throws Exception {
        ValueRetriever<String> retriever = new CSVEvent.CSVRetriever("One");
        String[] values = {"zero", "one", "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        assertEquals("one", retriever.getValue(instance));
    }

    @Test
    public void testLongRetriever() throws Exception {
        ValueRetriever<Number> retriever = new CSVEvent.CSVLongRetriever("One");
        String[] values = {"zero", "1", "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        assertEquals(1L, retriever.getValue(instance));
    }

    @Test
    public void testDoubleRetriever() throws Exception {
        ValueRetriever<Number> retriever = new CSVEvent.CSVDoubleRetriever("One");
        String[] values = {"zero", "1", "two"};
        instance = new JdoCSVArrayEvent(header, fieldMap, values);
        assertEquals(1.0, retriever.getValue(instance));
    }
}
