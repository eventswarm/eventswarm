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

import com.eventswarm.Combination;
import com.eventswarm.events.CombinationsPart;
import com.eventswarm.events.Event;
import junit.framework.TestCase;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCondensedCombinationsPartTest extends TestCase {
    Event event1, event2;
    SortedSet<Event> events_single, events_single_2, events_empty, events_multiple;
    List<SortedSet<Event>> list_1_single, list_1_single_2, list_1_null, list_1_multiple, list_2_single, list_2_null, list_2_empty, list_2_multiple, list_2_multiple_single, list_2_single_multiple;
    Combination comb1_event1, comb1_null, comb1_event2, comb2_event1, comb2_null_event1, comb2_event1_event1, comb2_event1_event2, comb2_event2_event1, comb2_event2, comb2_event2_event2;
    Set<List<SortedSet<Event>>> set_1_single, set_2_single_null, set_2_multiple_single, set_2_list_2_multiple_single, set_2_multiple_single_reversed ;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        event1 = TestEvents.event;
        event2 = TestEvents.eventAfterSameSrcAfterSeq;
        events_single = new TreeSet<Event>(); events_single.add(event1);
        events_single_2 = new TreeSet<Event>(); events_single_2.add(event2);
        events_multiple = new TreeSet<Event>(); events_multiple.add(event1); events_multiple.add(event2);
        events_empty = new TreeSet<Event>();
        list_1_single = new ArrayList<SortedSet<Event>>(); list_1_single.add(events_single);
        list_1_single_2 = new ArrayList<SortedSet<Event>>(); list_1_single_2.add(events_single_2);
        list_1_null = new ArrayList<SortedSet<Event>>(); list_1_null.add(events_empty);
        list_1_multiple = new ArrayList<SortedSet<Event>>(); list_1_multiple.add(events_multiple);
        list_2_single = new ArrayList<SortedSet<Event>>(); list_2_single.add(events_single); list_2_single.add(events_single);
        list_2_null = new ArrayList<SortedSet<Event>>(); list_2_null.add(events_empty); list_2_null.add(events_single);
        list_2_empty = new ArrayList<SortedSet<Event>>(); list_2_empty.add(events_empty); list_2_empty.add(events_single);
        list_2_multiple = new ArrayList<SortedSet<Event>>(); list_2_multiple.add(events_multiple); list_2_multiple.add(events_multiple);
        list_2_multiple_single = new ArrayList<SortedSet<Event>>(); list_2_multiple_single.add(events_multiple); list_2_multiple_single.add(events_single_2);
        list_2_single_multiple = new ArrayList<SortedSet<Event>>(); list_2_single_multiple.add(events_single_2); list_2_single_multiple.add(events_multiple);
        set_1_single = new HashSet<List<SortedSet<Event>>>(); set_1_single.add(list_1_single);
        set_2_single_null = new HashSet<List<SortedSet<Event>>>(); set_2_single_null.add(list_1_single); set_2_single_null.add(list_1_null);
        set_2_multiple_single = new HashSet<List<SortedSet<Event>>>(); set_2_multiple_single.add(list_1_multiple); set_2_multiple_single.add(list_1_single);
        set_2_list_2_multiple_single = new HashSet<List<SortedSet<Event>>>(); set_2_list_2_multiple_single.add(list_2_multiple); set_2_list_2_multiple_single.add(list_1_multiple);
        set_2_multiple_single_reversed = new HashSet<List<SortedSet<Event>>>(); set_2_multiple_single_reversed.add(list_2_multiple_single); set_2_multiple_single_reversed.add(list_2_single_multiple);
        comb1_event1 = new JdoCombination(); comb1_event1.add(event1);
        comb1_null = new JdoCombination(); comb1_null.add(null);
        comb1_event2 = new JdoCombination(); comb1_event2.add(event2);
        comb2_event1 = new JdoCombination(); comb2_event1.add(event1); comb2_event1.add(event1);
        comb2_null_event1 = new JdoCombination(); comb2_null_event1.add(null); comb2_null_event1.add(event1);
        comb2_event2 = new JdoCombination(); comb2_event2.add(event2); comb2_event2.add(event2);
        comb2_event1_event2 = new JdoCombination(); comb2_event1_event2.add(event1); comb2_event1_event2.add(event2);
        comb2_event2_event1 = new JdoCombination(); comb2_event2_event1.add(event2); comb2_event2_event1.add(event1);
    }

    public void testGetCombinations_null_list() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart((List<SortedSet<Event>>) null);
        assertTrue(part.getCombinations().isEmpty());
    }

    public void testGetCombinations_null_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart((Set<List<SortedSet<Event>>>) null);
        assertTrue(part.getCombinations().isEmpty());
    }

    public void testGetCombinations_single_list_single_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_1_single);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("1 single element set in one list, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb1_event1.toString());
        assertEquals(1, combinations.size());
        assertTrue(combinations.contains(comb1_event1));
    }

    public void testGetCombinations_single_list_null_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_1_null);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("1 null element set in one list, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb1_null.toString());
        assertEquals(1, combinations.size());
        assertTrue(combinations.contains(comb1_null));
    }

    public void testGetCombinations_single_list_multiple_single_elem_sets() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_single);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("2 single element sets in one list, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb2_event1.toString());
        assertEquals(1, combinations.size());
        assertTrue(combinations.contains(comb2_event1));
    }

    public void testGetCombinations_single_list_multiple_single_elem_sets_incl_null() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_null);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("1 single element set and one null element set in one list, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb2_null_event1.toString());
        assertEquals(1, combinations.size());
        assertTrue(combinations.contains(comb2_null_event1));
    }

    public void testGetCombinations_single_list_single_2_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_1_multiple);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("2 element set in one list, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb1_event1.toString());
        System.out.println("           and: " + comb1_event2.toString());
        assertEquals(2, combinations.size());
        assertTrue(combinations.contains(comb1_event1));
        assertTrue(combinations.contains(comb1_event2));
    }

    public void testGetCombinations_single_list_multiple_2_elem_sets() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_multiple);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("2 element set in one list, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb2_event1.toString());
        System.out.println("           and: " + comb2_event2.toString());
        System.out.println("           and: " + comb2_event1_event2.toString());
        System.out.println("           and: " + comb2_event2_event1.toString());
        assertEquals(4, combinations.size());
        assertTrue(combinations.contains(comb2_event1));
        assertTrue(combinations.contains(comb2_event2));
        assertTrue(combinations.contains(comb2_event1_event2));
        assertTrue(combinations.contains(comb2_event2_event1));
    }

    public void testGetCombinations_single_list_multiple_sets_incl_empty() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_empty);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("1 single element set and one empty set in one list, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb2_null_event1.toString());
        assertEquals(1, combinations.size());
        assertTrue(combinations.contains(comb2_null_event1));
    }

    public void testGetCombinations_set_single_list_single_1_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_1_single);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("1 single element set, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb1_event1.toString());
        assertEquals(1, combinations.size());
        assertTrue(combinations.contains(comb1_event1));
    }

    public void testGetCombinations_set_2_list_1_elem_set_null_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_single_null);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("2 lists, one single element set, one null element set, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb1_event1.toString());
        System.out.println("           and: " + comb1_null.toString());
        assertEquals(2, combinations.size());
        assertTrue(combinations.contains(comb1_event1));
        assertTrue(combinations.contains(comb1_null));
    }

    public void testGetCombinations_set_2_list_single_2_elem_set_single_1_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_multiple_single);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("2 lists, one multiple element set, one single element set, combinations: " + combinations.toString());
        System.out.println("Overlapping sets, so should reduce to 2 single-element combinations");
        System.out.println("Should contain: " + comb1_event1.toString());
        System.out.println("           and: " + comb1_event2.toString());
        assertEquals(2, combinations.size());
        assertTrue(combinations.contains(comb1_event1));
        assertTrue(combinations.contains(comb1_event2));
    }

    public void testGetCombinations_set_2_list_multiple_2_elem_sets_single_2_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_list_2_multiple_single);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("2 lists, one containing 2 multiple element set, one containing a single multiple element set, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb1_event1.toString());
        System.out.println("           and: " + comb1_event2.toString());
        System.out.println("           and: " + comb2_event1.toString());
        System.out.println("           and: " + comb2_event2.toString());
        System.out.println("           and: " + comb2_event1_event2.toString());
        System.out.println("           and: " + comb2_event2_event1.toString());
        assertEquals(6, combinations.size());
        assertTrue(combinations.contains(comb1_event1));
        assertTrue(combinations.contains(comb1_event2));
        assertTrue(combinations.contains(comb2_event1));
        assertTrue(combinations.contains(comb2_event2));
        assertTrue(combinations.contains(comb2_event1_event2));
        assertTrue(combinations.contains(comb2_event2_event1));
    }

    public void testGetCombinations_set_2_list_2_1_list_1_2_reversed() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_multiple_single_reversed);
        Set<Combination> combinations = part.getCombinations();
        System.out.println("2 lists, 1 elem and 2 elem, then 2 elem and 1 elem, combinations: " + combinations.toString());
        System.out.println("Should contain: " + comb2_event2.toString());
        System.out.println("           and: " + comb2_event2_event1.toString());
        System.out.println("           and: " + comb2_event1_event2.toString());
        assertEquals(3, part.count());
        assertEquals(3, combinations.size());
        assertFalse(combinations.contains(comb2_event1));
        assertTrue(combinations.contains(comb2_event2_event1));
        assertTrue(combinations.contains(comb2_event1_event2));
        assertTrue(combinations.contains(comb2_event2));
    }


    public void testCount_null_list() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart((List<SortedSet<Event>>) null);
        assertEquals(0, part.count());
    }

    public void testCount_null_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart((Set<List<SortedSet<Event>>>) null);
        assertEquals(0, part.count());
    }

    public void testGetCount_single_list_single_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_1_single);
        assertEquals(1, part.count());
    }

    public void testCount_single_list_multiple_single_elem_sets() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_single);
        assertEquals(1, part.count());
    }

    public void testCount_single_list_multiple_sets_incl_empty() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_empty);
        assertEquals(1, part.count());
    }

    public void testCount_single_list_single_2_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_1_multiple);
        assertEquals(2, part.count());
    }

    public void testCount_set_single_list_single_1_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_1_single);
        assertEquals(1, part.count());
    }

    public void testCount_set_2_list_single_1_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_single_null);
        assertEquals(2, part.count());
    }

    public void testCount_single_list_multiple_2_elem_sets() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_multiple);
        assertEquals(4, part.count());
    }

    public void testCount_set_2_list_multiple_2_elem_sets_single_2_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_list_2_multiple_single);
        assertEquals(6, part.count());
    }

    public void testGetEvents_null_list() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart((List<SortedSet<Event>>) null);
        assertTrue(part.getEvents().isEmpty());
    }

    public void testGetEvents_null_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart((Set<List<SortedSet<Event>>>) null);
        assertTrue(part.getEvents().isEmpty());
    }

    public void testGetEvents_single_list_single_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_1_single);
        assertEquals(1, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
    }

    public void testGetEvents_single_list_multiple_single_elem_sets() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_single);
        assertEquals(1, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
    }

    public void testGetEvents_single_list_multiple_sets_incl_empty() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_empty);
        assertEquals(1, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
    }

    public void testGetEvents_single_list_single_2_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_1_multiple);
        assertEquals(2, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
        assertTrue(part.getEvents().contains(event2));
    }

    public void testGetEvents_set_single_list_single_1_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_1_single);
        assertEquals(1, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
    }

    public void testGetEvents_set_2_list_single_1_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_single_null);
        assertEquals(1, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
    }

    public void testGetEvents_single_list_multiple_2_elem_sets() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(list_2_multiple);
        assertEquals(2, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
        assertTrue(part.getEvents().contains(event2));
    }

    public void testGetEvents_set_2_list_multiple_2_elem_sets_single_2_elem_set() throws Exception {
        CombinationsPart part = new JdoCondensedCombinationsPart(set_2_list_2_multiple_single);
        assertEquals(2, part.getEvents().size());
        assertTrue(part.getEvents().contains(event1));
        assertTrue(part.getEvents().contains(event2));
    }
}
