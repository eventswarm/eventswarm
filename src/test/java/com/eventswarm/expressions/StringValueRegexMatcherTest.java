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
public class StringValueRegexMatcherTest {

    ValueRetriever<String> retriever = new JsonEvent.StringRetriever("text");

    @Test
    public void testConstruct() throws Exception {
        StringValueRegexMatcher instance = new StringValueRegexMatcher("blah", retriever);
        assertNotNull(instance);
    }

    @Test
    public void testTrueStringMatch() throws Exception {
        StringValueRegexMatcher instance = new StringValueRegexMatcher(".*blah.*", retriever);
        boolean result = instance.matches(makeEvent(" blah, and some other words"));
        assertTrue(result);
    }

    @Test
    public void testFalseStringMatch() throws Exception {
        StringValueRegexMatcher instance = new StringValueRegexMatcher(".*blah.*", retriever);
        boolean result = instance.matches(makeEvent("nope"));
        assertFalse(result);
    }

    @Test
    public void testTrueWordMatch() throws Exception {
        StringValueRegexMatcher instance = new StringValueRegexMatcher(".*\\bblah\\b.*", retriever);
        boolean result = instance.matches(makeEvent(" blah, and some other words"));
        assertTrue(result);
    }

    @Test
    public void testFalseWordMatch() throws Exception {
        StringValueRegexMatcher instance = new StringValueRegexMatcher(".*\\bblah\\b.*", retriever);
        boolean result = instance.matches(makeEvent(" notblah, and some other words"));
        assertFalse(result);
    }

    @Test
    public void testTrueSequenceMatch() throws Exception {
        StringValueRegexMatcher instance = new StringValueRegexMatcher(".*\\bblah\\W*\\bblah\\b.*", retriever);
        boolean result = instance.matches(makeEvent(" blah blah, and some other words"));
        assertTrue(result);
    }

    @Test
    public void testTrueHashtagMatch() throws Exception {
        StringValueRegexMatcher instance = new StringValueRegexMatcher(".*\\s#blah\\b.*", retriever);
        boolean result = instance.matches(makeEvent(" blah #blah, and some other words"));
        assertTrue(result);
    }

    Event makeEvent(String text) {
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'text':" + "'" + text + "'}"));
    }
}
