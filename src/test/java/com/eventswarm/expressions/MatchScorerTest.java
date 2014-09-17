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

import com.eventswarm.events.jdo.TestEvents;
import org.junit.Test;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MatchScorerTest {
    Map<Matcher,Number> parts;
    Matcher[] list;

    @Before
    public void setup() throws Exception {
        parts = new HashMap<Matcher,Number>();
    }

    @Test
    public void testConstructWithMap() throws Exception {
        MatchScorer instance = new MatchScorer(parts);
        assertNotNull(instance);
    }

    @Test
    public void testConstructWithArray() throws Exception {
        list = new Matcher[1];
        list[0] = new TrueMatcher();
        MatchScorer instance = new MatchScorer(list, 1);
        assertNotNull(instance);
    }

    @Test
    public void testSingleScoreMap() throws Exception {
        parts.put(new TrueMatcher(), 2);
        MatchScorer instance = new MatchScorer(parts);
        int result = instance.score(TestEvents.jdoEvent);
        assertEquals(result, 2);
    }

    @Test
    public void testSingleScoreArray() throws Exception {
        list = new Matcher[1];
        list[0] = new TrueMatcher();
        MatchScorer instance = new MatchScorer(list, 2);
        int result = instance.score(TestEvents.jdoEvent);
        assertEquals(result, 2);
    }

    @Test
    public void testWithNonMatch() throws Exception {
        parts.put(new NOTMatcher(new TrueMatcher()), 1);
        MatchScorer instance = new MatchScorer(parts);
        int result = instance.score(TestEvents.jdoEvent);
        assertEquals(result, 0);
    }

    @Test
    public void testMultipleScoreMap() throws Exception {
        parts.put(new TrueMatcher(), 2);
        parts.put(new TrueMatcher(), 1);
        MatchScorer instance = new MatchScorer(parts);
        int result = instance.score(TestEvents.jdoEvent);
        assertEquals(result, 3);
    }

    @Test
    public void testMultipleScoreArray() throws Exception {
        list = new Matcher[2];
        list[0] = new TrueMatcher();
        list[1] = new TrueMatcher();
        MatchScorer instance = new MatchScorer(list, 2);
        int result = instance.score(TestEvents.jdoEvent);
        assertEquals(result, 4);
    }

    @Test
    public void testOneScoreFromMany() throws Exception {
        parts.put(new TrueMatcher(), 2);
        parts.put(new NOTMatcher(new TrueMatcher()), 1);
        MatchScorer instance = new MatchScorer(parts);
        int result = instance.score(TestEvents.jdoEvent);
        assertEquals(result, 2);
    }

    @Test
    public void testSomeScoresInMany() throws Exception {
        parts.put(new TrueMatcher(), 2);
        parts.put(new NOTMatcher(new TrueMatcher()), 1);
        parts.put(new TrueMatcher(), 1);
        MatchScorer instance = new MatchScorer(parts);
        int result = instance.score(TestEvents.jdoEvent);
        assertEquals(result, 3);
    }
}
