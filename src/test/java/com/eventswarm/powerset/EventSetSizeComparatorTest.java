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
package com.eventswarm.powerset;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.eventset.EventSet;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventSetSizeComparatorTest {
    private static Source SOURCE = new JdoSource("EventSetSizeComparatorTest");
    private Map<String,EventSet> map = new HashMap<String,EventSet>();

    @Test
    public void compareSameSet() throws Exception {
        Map.Entry<String,EventSet> entry = new AbstractMap.SimpleEntry<String,EventSet>("set", new EventSet());
        EventSetSizeComparator<String> instance = new EventSetSizeComparator<String>();
        assertEquals(0, instance.compare(entry, entry));
    }

    @Test
    public void compareSameSizeDifferentSet() throws Exception {
        Map.Entry<String,EventSet> entry1 = new AbstractMap.SimpleEntry<String,EventSet>("set1", new EventSet());
        Map.Entry<String,EventSet> entry2 = new AbstractMap.SimpleEntry<String,EventSet>("set2", new EventSet());
        EventSetSizeComparator<String> instance = new EventSetSizeComparator<String>();
        assertNotSame(0, instance.compare(entry1, entry2));
    }

    @Test
    public void deterministicDistinguish() throws Exception {
        Map.Entry<String,EventSet> entry1 = new AbstractMap.SimpleEntry<String,EventSet>("set1", new EventSet());
        Map.Entry<String,EventSet> entry2 = new AbstractMap.SimpleEntry<String,EventSet>("set2", new EventSet());
        EventSetSizeComparator<String> instance = new EventSetSizeComparator<String>();
        int result1 = instance.compare(entry1, entry2);
        int result2 = instance.compare(entry1, entry2);
        assertSame(result1, result2);
    }

    @Test
    public void compareSmallerDescending() throws Exception {
        Map.Entry<String,EventSet> entry1 = new AbstractMap.SimpleEntry<String,EventSet>("set1", new EventSet());
        EventSet set2 = new EventSet();
        set2.add(new KeyedEvent("a"));
        Map.Entry<String,EventSet> entry2 = new AbstractMap.SimpleEntry<String,EventSet>("set2", set2);
        EventSetSizeComparator<String> instance = new EventSetSizeComparator<String>();
        assertEquals(1, instance.compare(entry1, entry2));
    }

    @Test
    public void compareLargerDescending() throws Exception {
        Map.Entry<String,EventSet> entry1 = new AbstractMap.SimpleEntry<String,EventSet>("set1", new EventSet());
        EventSet set2 = new EventSet();
        set2.add(new KeyedEvent("a"));
        Map.Entry<String,EventSet> entry2 = new AbstractMap.SimpleEntry<String,EventSet>("set2", set2);
        EventSetSizeComparator<String> instance = new EventSetSizeComparator<String>();
        assertEquals(-1, instance.compare(entry2, entry1));
    }

    @Test
    public void compareSmallerAscending() throws Exception {
        Map.Entry<String,EventSet> entry1 = new AbstractMap.SimpleEntry<String,EventSet>("set1", new EventSet());
        EventSet set2 = new EventSet();
        set2.add(new KeyedEvent("a"));
        Map.Entry<String,EventSet> entry2 = new AbstractMap.SimpleEntry<String,EventSet>("set2", set2);
        EventSetSizeComparator<String> instance = new EventSetSizeComparator<String>(false);
        assertEquals(-1, instance.compare(entry1, entry2));
    }

    @Test
    public void compareLargerAscending() throws Exception {
        Map.Entry<String,EventSet> entry1 = new AbstractMap.SimpleEntry<String,EventSet>("set1", new EventSet());
        EventSet set2 = new EventSet();
        set2.add(new KeyedEvent("a"));
        Map.Entry<String,EventSet> entry2 = new AbstractMap.SimpleEntry<String,EventSet>("set2", set2);
        EventSetSizeComparator<String> instance = new EventSetSizeComparator<String>(false);
        assertEquals(1, instance.compare(entry2, entry1));
    }

    @Test
    public void makeSortedSetDescending() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("c"));
        TreeSet<Map.Entry<String,EventSet>> sortedSet = new TreeSet<Map.Entry<String,EventSet>>(new EventSetSizeComparator<String>());
        sortedSet.addAll(pset.entrySet());
        assertEquals("a", sortedSet.first().getKey());
        assertEquals("c", sortedSet.last().getKey());
    }

    @Test
    public void makeSortedSetAscending() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("c"));
        TreeSet<Map.Entry<String,EventSet>> sortedSet = new TreeSet<Map.Entry<String,EventSet>>(new EventSetSizeComparator<String>(false));
        sortedSet.addAll(pset.entrySet());
        assertEquals("c", sortedSet.first().getKey());
        assertEquals("a", sortedSet.last().getKey());
    }

    private static EventKey<String> KEYGETTER = new EventKey<String>() {
        @Override
        public String getKey(Event event) {
            if (KeyedEvent.class.isInstance(event)) return ((KeyedEvent) event).getKey();
            else return null;
        }
    };

    public static class KeyedEvent extends JdoEvent {
        private String key;

        public KeyedEvent(String key) {
            super(new JdoHeader(new Date(), SOURCE), new HashMap<String,EventPart>());
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
