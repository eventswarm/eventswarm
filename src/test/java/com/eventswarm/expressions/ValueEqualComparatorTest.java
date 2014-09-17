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
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ValueEqualComparatorTest {
    private ValueRetriever<String> retriever = new JsonEvent.StringRetriever("text");
    private Event event1, event2;
    private ValueEqualComparator<String> instance;

    @Test
    public void testMatches() throws Exception {
        instance = new ValueEqualComparator<String>(retriever);
        event1 = makeEvent("blah");
        event2 = makeEvent("blah");
        assertTrue(instance.matches(event1,event2));
    }

    @Test
    public void testNotMatches() throws Exception {
        instance = new ValueEqualComparator<String>(retriever);
        event1 = makeEvent("blah");
        event2 = makeEvent("blat");
        assertFalse(instance.matches(event1,event2));
    }

    @Test
    public void testNullFirstValue() throws Exception {
        instance = new ValueEqualComparator<String>(retriever);
        event1 = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        event2 = makeEvent("blat");
        assertFalse(instance.matches(event1,event2));
    }

    @Test
    public void testNullSecondValue() throws Exception {
        instance = new ValueEqualComparator<String>(retriever);
        event1 = makeEvent("blat");
        event2 = new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}"));
        assertFalse(instance.matches(event1,event2));
    }

    Event makeEvent(String text) {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'text':" + "'" + text + "'}"));
    }
}
