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
import com.eventswarm.MutableTarget;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.TestEvents;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.expressions.FalseMatcher;
import com.eventswarm.expressions.Matcher;
import com.eventswarm.expressions.TrueMatcher;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class KeyedActionTest {

    @Test
    public void construct() throws Exception {
        Matcher key = new TrueMatcher();
        Matcher matchers[] = {key};
        MatcherEventKey keyFinder = new MatcherEventKey(matchers);
        Powerset<Matcher> pset = new HashPowerset<Matcher>(keyFinder);
        MutableTarget target = new EventSet();
        KeyedAction<Matcher> instance = new KeyedAction<Matcher>(target, pset, key);
        assertNotNull(instance);
    }


    @Test
    public void noSubset_add() throws Exception {
        Matcher key = new TrueMatcher();
        Matcher matchers[] = {key};
        MatcherEventKey keyFinder = new MatcherEventKey(matchers);
        Powerset<Matcher> pset = new HashPowerset<Matcher>(keyFinder);
        EventSet target = new EventSet();
        KeyedAction<Matcher> instance = new KeyedAction<Matcher>(target, pset, key);
        Event event = TestEvents.jdoEvent;
        pset.execute((AddEventTrigger) null, event);
        assertEquals(1, target.size());
        assertTrue(target.contains(event));
    }

    @Test
    public void withSubset_add() throws Exception {
        Matcher key = new TrueMatcher();
        Matcher matchers[] = {key};
        MatcherEventKey keyFinder = new MatcherEventKey(matchers);
        Powerset<Matcher> pset = new HashPowerset<Matcher>(keyFinder);
        EventSet target = new EventSet();
        Event event1 = TestEvents.jdoEvent;
        pset.execute((AddEventTrigger) null, event1);
        KeyedAction<Matcher> instance = new KeyedAction<Matcher>(target, pset, key);
        Event event2 = TestEvents.jdoEventAfterDiffSrcAfterSeq;
        pset.execute((AddEventTrigger) null, event2);
        assertEquals(1, target.size());
        assertTrue(target.contains(event2));
    }

    @Test
    public void noSubset_add_twice() throws Exception {
        Matcher key = new TrueMatcher();
        Matcher matchers[] = {key};
        MatcherEventKey keyFinder = new MatcherEventKey(matchers);
        Powerset<Matcher> pset = new HashPowerset<Matcher>(keyFinder);
        EventSet target = new EventSet();
        KeyedAction<Matcher> instance = new KeyedAction<Matcher>(target, pset, key);
        Event event1 = TestEvents.jdoEvent;
        pset.execute((AddEventTrigger) null, event1);
        Event event2 = TestEvents.jdoEventAfterDiffSrcAfterSeq;
        pset.execute((AddEventTrigger) null, event2);
        assertEquals(2, target.size());
        assertTrue(target.contains(event1));
        assertTrue(target.contains(event2));
    }

    @Test
    public void eventNotMatching() throws Exception {
        Matcher key = new FalseMatcher();
        Matcher other = new TrueMatcher();
        Matcher matchers[] = {key, other};
        MatcherEventKey keyFinder = new MatcherEventKey(matchers);
        Powerset<Matcher> pset = new HashPowerset<Matcher>(keyFinder);
        EventSet target = new EventSet();
        KeyedAction<Matcher> instance = new KeyedAction<Matcher>(target, pset, key);
        Event event = TestEvents.jdoEvent;
        pset.execute((AddEventTrigger) null, event);
        assertEquals(0, target.size());
        assertFalse(target.contains(event));
        assertTrue(pset.get(other).contains(event));
    }

    @Test
    public void add_prune_add() throws Exception {
        Matcher key = new TrueMatcher();
        Matcher matchers[] = {key};
        MatcherEventKey keyFinder = new MatcherEventKey(matchers);
        HashPowerset<Matcher> pset = new HashPowerset<Matcher>(keyFinder);
        assertTrue(pset.isPrune());
        EventSet target = new EventSet();
        KeyedAction<Matcher> instance = new KeyedAction<Matcher>(target, pset, key);
        Event event1 = TestEvents.jdoEvent;
        pset.execute((AddEventTrigger) null, event1);
        assertEquals(1, target.size());
        assertTrue(target.contains(event1));
        pset.execute((RemoveEventTrigger) null, event1);
        assertEquals(0, target.size());
        Event event2 = TestEvents.jdoEventAfterDiffSrcAfterSeq;
        pset.execute((AddEventTrigger) null, event2);
        // second event should also be added
        assertEquals(1, target.size());
    }
}
