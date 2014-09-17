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

import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import com.eventswarm.expressions.FalseMatcher;
import com.eventswarm.expressions.Matcher;
import com.eventswarm.expressions.TrueMatcher;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MatcherEventKeyTest {
    @Test
    public void getKeyEmpty() throws Exception {
        Matcher matchers[] = new Matcher[0];
        Event event = TestEvents.jdoEvent;
        MatcherEventKey instance = new MatcherEventKey(matchers);
        Matcher result = instance.getKey(event);
        assertNull(result);
    }

    @Test
    public void getKeySingleMatch() throws Exception {
        Matcher target = new TrueMatcher();
        Matcher matchers[] = {target};
        Event event = TestEvents.jdoEvent;
        MatcherEventKey instance = new MatcherEventKey(matchers);
        Matcher result = instance.getKey(event);
        assertEquals(target, result);
    }

    @Test
    public void getKeySingleNoMatch() throws Exception {
        Matcher matchers[] = {new FalseMatcher()};
        Event event = TestEvents.jdoEvent;
        MatcherEventKey instance = new MatcherEventKey(matchers);
        Matcher result = instance.getKey(event);
        assertNull(result);
    }

    @Test
    public void getKeyMatchSecond() throws Exception {
        Matcher target = new TrueMatcher();
        Matcher matchers[] = {new FalseMatcher(), target};
        Event event = TestEvents.jdoEvent;
        MatcherEventKey instance = new MatcherEventKey(matchers);
        Matcher result = instance.getKey(event);
        assertEquals(target, result);
    }

    @Test
    public void getKeyMultipleNoMatch() throws Exception {
        Matcher matchers[] = {new FalseMatcher(), new FalseMatcher()};
        Event event = TestEvents.jdoEvent;
        MatcherEventKey instance = new MatcherEventKey(matchers);
        Matcher result = instance.getKey(event);
        assertNull(result);
    }

    @Test
    public void getKeyMultiple2Match() throws Exception {
        Matcher target = new TrueMatcher();
        Matcher matchers[] = {new FalseMatcher(), target, new TrueMatcher()};
        Event event = TestEvents.jdoEvent;
        MatcherEventKey instance = new MatcherEventKey(matchers);
        Matcher result = instance.getKey(event);
        assertEquals(target, result);
    }
}

