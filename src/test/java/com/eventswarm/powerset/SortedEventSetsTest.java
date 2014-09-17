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

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SortedEventSetsTest {
    private static Source SOURCE = new JdoSource("SortedEventSetsTest");

    @Test
    public void getSetsDefaultOrder() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        SortedEventSets<String> instance = new SortedEventSets<String>(pset);
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("c"));
        SortedSet<Map.Entry<String,EventSet>> result = instance.getEventSets();
        assertEquals("a", result.first().getKey());
        assertEquals("c", result.last().getKey());
    }

    @Test
    public void getSetsAlternateOrder() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        SortedEventSets<String> instance = new SortedEventSets<String>(pset, new EventSetSizeComparator<String>(false));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("c"));
        SortedSet<Map.Entry<String,EventSet>> result = instance.getEventSets();
        assertEquals("c", result.first().getKey());
        assertEquals("a", result.last().getKey());
    }

    @Test
    public void getSetsEmpty() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        SortedEventSets<String> instance = new SortedEventSets<String>(pset);
        SortedSet<Map.Entry<String,EventSet>> result = instance.getEventSets();
        assertTrue(result.isEmpty());
    }

    @Test
    public void getEmptySetsModifiedAfter() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        SortedEventSets<String> instance = new SortedEventSets<String>(pset);
        SortedSet<Map.Entry<String,EventSet>> result = instance.getEventSets();
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        assertTrue(result.isEmpty());
    }

    @Test
    public void getSetsNewSetAfter() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        SortedEventSets<String> instance = new SortedEventSets<String>(pset);
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        SortedSet<Map.Entry<String,EventSet>> result = instance.getEventSets();
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        assertEquals(1, result.size());
        assertEquals("a", result.first().getKey());
        assertEquals(2, result.first().getValue().size());
    }

    @Test
    public void getSetsNewOrderAfter() throws Exception {
        Powerset<String> pset = new HashPowerset<String>(KEYGETTER);
        SortedEventSets<String> instance = new SortedEventSets<String>(pset);
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("a"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        SortedSet<Map.Entry<String,EventSet>> result1 = instance.getEventSets();
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        pset.execute((AddEventTrigger) null, new KeyedEvent("b"));
        SortedSet<Map.Entry<String,EventSet>> result2 = instance.getEventSets();
        assertEquals(2, result1.size());
        assertEquals("a", result1.first().getKey());
        assertEquals(2, result1.first().getValue().size());
        assertEquals(3, result1.last().getValue().size());
        assertEquals("b", result2.first().getKey());
        assertEquals(3, result2.first().getValue().size());
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
