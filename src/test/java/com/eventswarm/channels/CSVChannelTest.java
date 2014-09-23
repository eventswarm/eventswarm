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
import com.eventswarm.events.CSVEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoCSVEvent;
import com.eventswarm.events.jdo.JdoCSVEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class CSVChannelTest {
    private InputStream stream;
    private CSVChannel instance;
    private List<Event> events;
    private AddEventAction addEvent;

    @Before
    public void openFile() throws Exception {
        stream = CSVChannelTest.class.getClassLoader().getResourceAsStream("fixtures/SGB.csv");
        events = new ArrayList<Event>();
        addEvent = new AddEventAction(){
            public void execute(AddEventTrigger trigger, Event event) {
                events.add(event);
            }
        };
    }

    @Test
    public void testConstructStreamOnly() throws Exception {
        instance = new CSVChannel(stream);
        assertNotNull(instance);
    }

    @Test
    public void testSetupStreamOnly() throws Exception {
        instance = new CSVChannel(stream);
        instance.setup();
        assertEquals(130, instance.getFieldNames().length);
        assertEquals(130, instance.getFieldMap().size());
        assertNotNull(instance.getFieldMap().get("Currency"));
    }

    @Test
    public void testSetupStreamWithFieldnames() throws Exception {
        String[] names = new String[130];
        for (int i=0; i<names.length; i++) {
            names[i] = Integer.toString(i);
        }
        instance = new CSVChannel(stream, names);
        instance.setup();
        assertEquals(130, instance.getFieldNames().length);
        assertEquals(130, instance.getFieldMap().size());
        assertNull(instance.getFieldMap().get("Currency"));
        assertEquals("120", instance.getFieldNames()[120]);
        assertNotNull(instance.getFieldMap().get("120"));
    }

    @Test
    public void testSetupStreamWithFieldMap() throws Exception {
        Map<String,Integer> fieldMap = new HashMap<String,Integer>();
        fieldMap.put("#RIC", 0);
        fieldMap.put("Date[L]", 1);
        fieldMap.put("Type", 2);
        instance = new CSVChannel(stream, fieldMap);
        instance.setup();
        assertEquals(130, instance.getFieldNames().length);
        assertEquals(3, instance.getFieldMap().size());
        assertFalse(instance.isDefaultMap());
        assertNull(instance.getFieldMap().get("Currency"));
        assertEquals(0, instance.getFieldMap().get("#RIC"));
        assertEquals(1, instance.getFieldMap().get("Date[L]"));
        assertEquals(2, instance.getFieldMap().get("Type"));
    }

    @Test
    public void testSetupStreamWithFieldnamesAndMap() throws Exception {
        String[] names = new String[130];
        Map<String,Integer> fieldMap = new HashMap<String,Integer>();
        for (int i=0; i<names.length; i++) {
            names[i] = "Column" + Integer.toString(i);
            fieldMap.put(names[i], i);
        }
        instance = new CSVChannel(stream, names, fieldMap);
        instance.setup();
        assertEquals(130, instance.getFieldNames().length);
        assertEquals(130, instance.getFieldMap().size());
        assertFalse(instance.isDefaultMap());
        assertNull(instance.getFieldMap().get("Currency"));
        assertEquals("Column120", instance.getFieldNames()[120]);
        assertEquals(120, instance.getFieldMap().get("Column120"));
    }

    @Test
    public void testCreateEventDefaultNamesAndMap() throws Exception {
        instance = new CSVChannel(stream);
        instance.setup();
        String[] values = new String[130];
        for(int i=0; i<values.length; i++) { values[i] = Integer.toString(i); }
        CSVEvent event = instance.createEvent(values);
        assertEquals("0", event.get("#RIC"));
        assertEquals("129", event.get("Data Source"));
    }

    @Test
    public void testCreateEventDefaultNamesRestrictedMap() throws Exception {
        Map<String,Integer> fieldMap = new HashMap<String,Integer>();
        fieldMap.put("#RIC", 0);
        fieldMap.put("Date[L]", 1);
        fieldMap.put("Type", 2);
        instance = new CSVChannel(stream, fieldMap);
        instance.setup();
        String[] values = new String[130];
        for(int i=0; i<values.length; i++) { values[i] = Integer.toString(i); }
        CSVEvent event = instance.createEvent(values);
        assertEquals("0", event.get("#RIC"));
        assertEquals("3", event.get("Type"));
        assertNull(event.get("Time"));
    }

    @Test
    public void testNextWithCompact() throws Exception {
        instance = new CSVChannel(stream);
        instance.setup();
        CSVEvent event = (CSVEvent) instance.next();
        System.out.println("Compact doesn't work because opencsv does not create nulls");
        //assertEquals("SGB.AX", event.get("#RIC"));
        //assertNull(event.get("Time[L]"));
        //assertFalse(event.getCsvMap().containsKey("Time[L]"));
    }

    @Test
    public void testNextWithoutCompact() throws Exception {
        instance = new CSVChannel(stream);
        instance.setup();
        JdoCSVEvent event = (JdoCSVEvent) instance.next();
        event.setCompact(false);
        System.out.println("Compact doesn't work because opencsv does not create nulls");
        //assertNull(event.get("Time[L]"));
        //assertTrue(event.getCsvMap().containsKey("Time[L]"));
        //assertNull(event.getCsvMap().get("Time[L]"));
    }

    @Test
    public void testNext() throws Exception {
        Map<String,Integer> fieldMap = new HashMap<String,Integer>();
        fieldMap.put("#RIC", 0);
        fieldMap.put("Date[L]", 1);
        fieldMap.put("Type", 2);
        instance = new CSVChannel(stream, fieldMap);
        instance.setup();
        CSVEvent event = (CSVEvent) instance.next();
        assertEquals("SGB.AX", event.get("#RIC"));
        assertEquals("End Of Day", event.get("Type"));
        assertNull(event.get("Time"));
    }

    @Test
    public void testProcessWithWhitelist() throws Exception {
        Map<String,Integer> fieldMap = new HashMap<String,Integer>();
        fieldMap.put("#RIC", 0);
        fieldMap.put("Date[L]", 1);
        fieldMap.put("Type", 2);
        instance = new CSVChannel(stream, fieldMap);
        instance.registerAction(addEvent);
        instance.process();
        assertEquals(764L, instance.getCount());
        assertEquals(764, events.size());
    }

    @Test
    public void testSetFieldNames() throws Exception {
        String[] names = new String[130];
        for (int i=0; i<names.length; i++) {
            names[i] = Integer.toString(i);
        }
        instance = new CSVChannel(stream).setFieldNames(names);
        instance.setup();
        assertEquals(130, instance.getFieldNames().length);
        assertEquals(130, instance.getFieldMap().size());
        assertNull(instance.getFieldMap().get("Currency"));
        assertEquals("120", instance.getFieldNames()[120]);
        assertNotNull(instance.getFieldMap().get("120"));
    }

    @Test
    public void testSetFieldMap() throws Exception {
        Map<String,Integer> fieldMap = new HashMap<String,Integer>();
        fieldMap.put("#RIC", 0);
        fieldMap.put("Date[L]", 1);
        fieldMap.put("Type", 2);
        instance = new CSVChannel(stream).setFieldMap(fieldMap);
        instance.setup();
        assertEquals(130, instance.getFieldNames().length);
        assertEquals(3, instance.getFieldMap().size());
        assertFalse(instance.isDefaultMap());
        assertNull(instance.getFieldMap().get("Currency"));
        assertEquals(0, instance.getFieldMap().get("#RIC"));
        assertEquals(1, instance.getFieldMap().get("Date[L]"));
        assertEquals(2, instance.getFieldMap().get("Type"));

    }

    @Test
    public void testSetFieldNamesAndMap() throws Exception {
        String[] names = new String[130];
        Map<String,Integer> fieldMap = new HashMap<String,Integer>();
        for (int i=0; i<names.length; i++) {
            names[i] = "Column" + Integer.toString(i);
            fieldMap.put(names[i], i);
        }
        instance = new CSVChannel(stream).setFieldMap(fieldMap).setFieldNames(names);
        instance.setup();
        assertEquals(130, instance.getFieldNames().length);
        assertEquals(130, instance.getFieldMap().size());
        assertFalse(instance.isDefaultMap());
        assertNull(instance.getFieldMap().get("Currency"));
        assertEquals("Column120", instance.getFieldNames()[120]);
        assertEquals(120, instance.getFieldMap().get("Column120"));
    }

    @Test
    public void testSetSourceName() throws Exception {
        instance = new CSVChannel(stream).setSourceName("CSVChannelTest");
        instance.setup();
        String[] values = new String[130];
        for(int i=0; i<values.length; i++) { values[i] = Integer.toString(i); }
        CSVEvent event = instance.createEvent(values);
        assertEquals("CSVChannelTest", event.getHeader().getSource().getSourceId());
    }

    @Test
    public void testSetSourceField() throws Exception {
        instance = new CSVChannel(stream).setSourceField("#RIC");
        instance.setup();
        String[] values = new String[130];
        for(int i=0; i<values.length; i++) { values[i] = Integer.toString(i); }
        CSVEvent event = instance.createEvent(values);
        assertEquals("0", event.getHeader().getSource().getSourceId());
    }

    @Test
    public void testSetTimestampField() throws Exception {
        instance = new CSVChannel(stream).setTimestampField("Date[L]", new SimpleDateFormat("d-MMM-yy"));
        instance.setup();
        String[] values = new String[130];
        Event event = instance.next();
        Date timestamp = event.getHeader().getTimestamp();
        assertEquals(1, timestamp.getDate());
        assertEquals(5, timestamp.getMonth());
        assertEquals(107, timestamp.getYear());
        assertEquals(0, timestamp.getHours());
        assertEquals(0, timestamp.getMinutes());
    }
}
