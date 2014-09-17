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

public class MatcherEventKeysTest {
    @Test
    public void getKeysEmpty() throws Exception {
        Matcher matchers[] = new Matcher[0];
        Event event = TestEvents.jdoEvent;
        MatcherEventKeys instance = new MatcherEventKeys(matchers);
        Matcher result[] = instance.getKeys(event);
        assertEquals(0, result.length);
    }


    @Test
    public void getKeysSingleMatch() throws Exception {
        Matcher target = new TrueMatcher();
        Matcher matchers[] = {target};
        Event event = TestEvents.jdoEvent;
        MatcherEventKeys instance = new MatcherEventKeys(matchers);
        Matcher result[] = instance.getKeys(event);
        assertEquals(target, result[0]);
    }

    @Test
    public void getKeysSingleNoMatch() throws Exception {
        Matcher matchers[] = {new FalseMatcher()};
        Event event = TestEvents.jdoEvent;
        MatcherEventKeys instance = new MatcherEventKeys(matchers);
        Matcher result[] = instance.getKeys(event);
        assertEquals(0, result.length);
    }

    @Test
    public void getKeysMultiple1Match() throws Exception {
        Matcher target = new TrueMatcher();
        Matcher matchers[] = {new FalseMatcher(), target};
        Event event = TestEvents.jdoEvent;
        MatcherEventKeys instance = new MatcherEventKeys(matchers);
        Matcher result[] = instance.getKeys(event);
        assertEquals(target, result[0]);
    }

    @Test
    public void getKeysMultipleNoMatch() throws Exception {
        Matcher matchers[] = {new FalseMatcher(), new FalseMatcher()};
        Event event = TestEvents.jdoEvent;
        MatcherEventKeys instance = new MatcherEventKeys(matchers);
        Matcher result[] = instance.getKeys(event);
        assertEquals(0, result.length);
    }

    @Test
    public void getKeysMultiple2Match() throws Exception {
        Matcher target1 = new TrueMatcher();
        Matcher target2 = new TrueMatcher();
        Matcher matchers[] = {new FalseMatcher(), target1, new FalseMatcher(), target2, new FalseMatcher()};
        Event event = TestEvents.jdoEvent;
        MatcherEventKeys instance = new MatcherEventKeys(matchers);
        Matcher result[] = instance.getKeys(event);
        assertEquals(target1, result[0]);
        assertEquals(target2, result[1]);
    }
}
