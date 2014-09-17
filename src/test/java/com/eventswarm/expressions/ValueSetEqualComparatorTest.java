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
package com.eventswarm.expressions;

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ValueSetEqualComparatorTest {
    private ValueRetriever<String> retriever1 = new JsonEvent.StringRetriever("text1");
    private ValueRetriever<String> retriever2 = new JsonEvent.StringRetriever("text2");
    private List<ValueRetriever<String>> retrievers = new ArrayList<ValueRetriever<String>>(2);
    private Event event1, event2;
    private ValueSetEqualComparator<String> instance;

    @Before
    public void setup() throws Exception {
        retrievers.add(retriever1);
        retrievers.add(retriever2);
    }

    @Test
    public void testMatches() throws Exception {
        instance = new ValueSetEqualComparator<String>(retrievers);
        event1 = makeEvent("blah", "blah");
        event2 = makeEvent("blah", "blah");
        assertTrue(instance.matches(event1,event2));
    }

    @Test
    public void testFirstMatches() throws Exception {
        instance = new ValueSetEqualComparator<String>(retrievers);
        event1 = makeEvent("blah", "blah");
        event2 = makeEvent("blah", "blat");
        assertFalse(instance.matches(event1,event2));
    }

    @Test
    public void testSecondMatches() throws Exception {
        instance = new ValueSetEqualComparator<String>(retrievers);
        event1 = makeEvent("blah", "blah");
        event2 = makeEvent("blat", "blah");
        assertFalse(instance.matches(event1,event2));
    }

    @Test
    public void testNoRetrievers() throws Exception {
        retrievers.remove(retriever1); retrievers.remove(retriever2);
        instance = new ValueSetEqualComparator<String>(retrievers);
        event1 = makeEvent("blah", "blah");
        event2 = makeEvent("blat", "blah");
        assertTrue(instance.matches(event1,event2));
    }


    @Test
    public void testNullFirstValue() throws Exception {
        instance = new ValueSetEqualComparator<String>(retrievers);
        event1 = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        event2 = makeEvent("blat", "blah");
        assertFalse(instance.matches(event1,event2));
    }

    @Test
    public void testNullSecondValue() throws Exception {
        instance = new ValueSetEqualComparator<String>(retrievers);
        event1 = makeEvent("blat", "blah");
        event2 = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        assertFalse(instance.matches(event1,event2));
    }

    Event makeEvent(String text1, String text2) {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(),
                new JSONObject("{'text1':'" + text1 + "', 'text2':'" + text2 + "'}"));
    }
}
