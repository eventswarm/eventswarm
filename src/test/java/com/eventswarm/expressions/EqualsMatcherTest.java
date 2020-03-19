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
public class EqualsMatcherTest {

    ValueRetriever<String> retriever = new JsonEvent.StringRetriever("text");

    @Test
    public void testConstruct() throws Exception {
        EqualsMatcher<String> instance = new EqualsMatcher<String>("blah", retriever);
        assertNotNull(instance);
    }

    @Test
    public void testTrueStringMatch() throws Exception {
        EqualsMatcher<String> instance = new EqualsMatcher<String>("blah", retriever);
        boolean result = instance.matches(makeEvent("blah"));
        assertTrue(result);
    }

    @Test
    public void testFalseStringMatch() throws Exception {
        EqualsMatcher<String> instance = new EqualsMatcher<String>("blah", retriever);
        boolean result = instance.matches(makeEvent("nope"));
        assertFalse(result);
    }

    @Test
    public void testFalseNullValueMatch() throws Exception {
        EqualsMatcher<String> instance = new EqualsMatcher<String>(null, retriever);
        boolean result = instance.matches(makeEvent("nope"));
        assertFalse(result);
    }

    @Test
    public void testTrueNullValueMatch() throws Exception {
        EqualsMatcher<String> instance = new EqualsMatcher<String>(null, retriever);
        boolean result = instance.matches(makeNullValueEvent());
        assertFalse(result);
    }

    Event makeEvent(String text) {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'text':" + "'" + text + "'}"));
    }

    Event makeNullValueEvent() {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'text': null}"));
    }
}
