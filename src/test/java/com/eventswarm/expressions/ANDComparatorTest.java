package com.eventswarm.expressions;

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 * Date: 26/09/2014
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ANDComparatorTest {
    List<Comparator> comparators;
    Event event1, event2;

    @Before
    public void setup() throws Exception {
        comparators = new ArrayList<Comparator>();
    }

    @Test
    public void testConstructNull() throws Exception {
        ANDComparator instance = new ANDComparator(comparators);
        assertNotNull(instance);
        assertEquals(comparators, instance.getComparators());
    }

    @Test
    public void testConstructNotNull() throws Exception {
        comparators.add(new ValueEqualComparator<String>(new JsonEvent.StringRetriever("a")));
        ANDComparator instance = new ANDComparator(comparators);
        assertNotNull(instance);
        assertNotNull(instance.getComparators());
    }

    @Test
    public void testMatchSingle() throws Exception {
        comparators.add(new ValueEqualComparator<String>(new JsonEvent.StringRetriever("text1")));
        ANDComparator instance = new ANDComparator(comparators);
        event1 = makeEvent("blah", "blat");
        event2 = makeEvent("blah", "blah");
        assertTrue(instance.matches(event1, event2));
    }

    @Test
    public void testNotMatchSingle() throws Exception {
        comparators.add(new ValueEqualComparator<String>(new JsonEvent.StringRetriever("text1")));
        ANDComparator instance = new ANDComparator(comparators);
        event1 = makeEvent("blah", "blat");
        event2 = makeEvent("blat", "blah");
        assertFalse(instance.matches(event1, event2));
    }

    @Test
    public void testMatchDouble() throws Exception {
        comparators.add(new ValueEqualComparator<String>(new JsonEvent.StringRetriever("text1")));
        comparators.add(new ValueEqualComparator<String>(new JsonEvent.StringRetriever("text2")));
        ANDComparator instance = new ANDComparator(comparators);
        event1 = makeEvent("blah", "blat");
        event2 = makeEvent("blah", "blat");
        assertTrue(instance.matches(event1, event2));
    }

    @Test
    public void testNotMatchDouble() throws Exception {
        comparators.add(new ValueEqualComparator<String>(new JsonEvent.StringRetriever("text1")));
        comparators.add(new ValueEqualComparator<String>(new JsonEvent.StringRetriever("text2")));
        ANDComparator instance = new ANDComparator(comparators);
        event1 = makeEvent("blah", "blat");
        event2 = makeEvent("blah", "blat");
        assertTrue(instance.matches(event1, event2));
    }

    Event makeEvent(String text1, String text2) {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'text1':'" + text1 + "', 'text2':" + text2 + "'}"));
    }
}
