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

import com.eventswarm.AddEventTrigger;
import com.eventswarm.Combination;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoCombination;
import com.eventswarm.eventset.EventSet;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.eventswarm.events.jdo.TestEvents;

/**
 * TODO: add tests for activity events within the sequence
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SequenceExpressionTest extends TestCase {
    private SequenceExpression sequence0, sequence1, sequence2, neverMatch1, neverMatch2_1, neverMatch2_2, identitySequence, orIdentitySequence;
    private Event event1 = TestEvents.event,
            event2 = TestEvents.eventAfterSameSrcBeforeSeq,
            event3 = TestEvents.eventAfterSameSrcConcSeq,
            event4 = TestEvents.eventAfterSameSrcAfterSeq;
    private EventExpression expr_true1 = new TrueEventExpression(),
            expr_true2 = new TrueEventExpression(),
            expr_true3 = new TrueEventExpression(),
            expr_true4 = new TrueEventExpression(),
            expr_true5 = new TrueEventExpression(),
            expr_true6 = new TrueEventExpression(),
            expr_false1 = new FalseEventExpression(),
            expr_false2 = new FalseEventExpression(),
            expr_false3 = new FalseEventExpression(),
            event1_identity = new EventMatcherExpression(new IdentityMatcher(event1)),
            event2_identity = new EventMatcherExpression(new IdentityMatcher(event2)),
            event1_or_3_identity = new EventMatcherExpression(new ORMatcher(Arrays.asList(new Matcher[] {new IdentityMatcher(event1), new IdentityMatcher(event3)})));
    private Combination comb_1, comb_2, comb_1_2, comb_1_3, comb_2_3, comb_1_4, comb_3_4, comb_empty;
    private List<Event> eventMatches;
    private List<ComplexExpressionMatchEvent> complexMatches;

    EventMatchAction eventAction = new EventMatchAction(){
        public void execute(EventMatchTrigger trigger, Event event) {
            eventMatches.add(event);
        }
    };

    ComplexExpressionMatchAction complexAction = new ComplexExpressionMatchAction() {
        public void execute(ComplexExpressionMatchTrigger trigger, ComplexExpressionMatchEvent event) {
            complexMatches.add(event);
        }
    };

    public void setUp() throws Exception {
        sequence0 = new SequenceExpression(new ArrayList<EventExpression>());
        sequence1 = new SequenceExpression(Arrays.asList(new EventExpression[] {expr_true1}));
        sequence2 = new SequenceExpression(Arrays.asList(new EventExpression[] {expr_true2, expr_true3}));
        neverMatch1 = new SequenceExpression(Arrays.asList(new EventExpression[] {expr_false1}));
        neverMatch2_1 = new SequenceExpression(Arrays.asList(new EventExpression[] {expr_false2, expr_true4}));
        neverMatch2_2 = new SequenceExpression(Arrays.asList(new EventExpression[] {expr_true5, expr_false3}));
        identitySequence = new SequenceExpression(Arrays.asList(new EventExpression[]{event1_identity, event2_identity}));
        orIdentitySequence = new SequenceExpression(Arrays.asList(new EventExpression[]{event1_or_3_identity, expr_true6}));
        eventMatches = new ArrayList<Event>();
        complexMatches = new ArrayList<ComplexExpressionMatchEvent>();
        comb_1 = new JdoCombination(); comb_1.add(event1);
        comb_2 = new JdoCombination(); comb_2.add(event2);
        comb_1_2 = new JdoCombination(); comb_1_2.add(event1); comb_1_2.add(event2);
        comb_1_3 = new JdoCombination(); comb_1_3.add(event1); comb_1_3.add(event3);
        comb_2_3 = new JdoCombination(); comb_2_3.add(event2); comb_2_3.add(event3);
        comb_1_4 = new JdoCombination(); comb_1_4.add(event1); comb_1_4.add(event4);
        comb_3_4 = new JdoCombination(); comb_3_4.add(event3); comb_3_4.add(event4);
        comb_empty = new JdoCombination();
    }

    public void tearDown() throws Exception {

    }

    public void testIsTrue_empty() throws Exception {
        assertTrue(sequence0.isTrue());
    }

    public void testIsTrue_1expr_0event() throws Exception {
        assertEquals(1, sequence1.expressions.size());
        assertEquals(1, sequence1.eventSets.size());
        assertTrue(sequence1.eventSets.get(0).isEmpty());
        assertFalse(sequence1.isTrue());
    }

    public void testIsTrue_2expr_0event() throws Exception {
        assertFalse(sequence2.isTrue());
    }

    public void testGetPartsAsList_empty() throws Exception {
        assertTrue(sequence0.getPartsAsList().size() == 0);
    }

    public void testGetPartsAsList_single() throws Exception {
        List<EventExpression> parts = sequence1.getPartsAsList();
        assertEquals(1, parts.size());
        assertEquals(expr_true1, parts.get(0));
    }

    public void testGetPartsAsList_multiple() throws Exception {
        List<EventExpression> parts =  sequence2.getPartsAsList();
        assertTrue(parts.size() == 2);
        assertTrue(parts.get(0) == expr_true2);
        assertTrue(parts.get(1) == expr_true3);
    }

    public void testAddEventExecute_empty() throws Exception {
        sequence0.execute((AddEventTrigger) null, event1);
        assertTrue(sequence0.isTrue());
        assertTrue(sequence0.getMatchEvents().size() == 0);
    }

    public void testAddEventExecute_1event_match() throws Exception {
        SequenceExpression expr = sequence1;
        expr.execute((AddEventTrigger) null, event1);
        assertTrue(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 1);
        assertTrue(matchSets.get(0).size() == 1);
        assertTrue(matchSets.get(0).contains(event1));
    }

    public void testAddEventExecute_1event_nomatch() throws Exception {
        SequenceExpression expr = neverMatch1;
        expr.execute((AddEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 1);
        assertTrue(matchSets.get(0).size() == 0);
    }

    public void testAddEventExecute_1event_nopartial() throws Exception {
        SequenceExpression expr = neverMatch2_1;
        expr.execute((AddEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 2);
        assertTrue(matchSets.get(0).size() == 0);
        assertTrue(matchSets.get(1).size() == 0);
    }


    public void testAddEventExecute_1event_partial_withdupe() throws Exception {
        SequenceExpression expr = sequence2;
        expr.execute((AddEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 2);
        assertTrue(matchSets.get(0).size() == 1);
        assertTrue(matchSets.get(0).contains(event1));
        assertFalse(matchSets.get(1).contains(event1));
    }


    public void testAddEventExecute_1event_partial_nodupe() throws Exception {
        SequenceExpression expr = neverMatch2_2;
        expr.execute((AddEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 2);
        assertTrue(matchSets.get(0).size() == 1);
        assertTrue(matchSets.get(0).contains(event1));
        assertFalse(matchSets.get(1).contains(event1));
    }


    public void testAddEventExecute_2event_match() throws Exception {
        SequenceExpression expr = sequence2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertTrue(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 2);
        // First match set should contain both events
        assertTrue(matchSets.get(0).size() == 2);
        assertTrue(matchSets.get(0).contains(event1));
        assertTrue(matchSets.get(0).contains(event2));
        // Second match set should contain only the second event
        assertTrue(matchSets.get(1).size() == 1);
        assertTrue(matchSets.get(1).contains(event2));
    }

    public void testAddEventExecute_2event_nomatch_falsefirst() throws Exception {
        SequenceExpression expr = neverMatch2_1;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 2);
        // neither set should contain events
        assertTrue(matchSets.get(0).size() == 0);
        assertTrue(matchSets.get(1).size() == 0);
    }

    public void testAddEventExecute_2event_nomatch_truefirst() throws Exception {
        SequenceExpression expr = neverMatch2_2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 2);
        // first set should contain both events
        assertTrue(matchSets.get(0).size() == 2);
        assertTrue(matchSets.get(0).contains(event1));
        assertTrue(matchSets.get(0).contains(event2));
        // Second match set should contain no events
        assertTrue(matchSets.get(1).size() == 0);
    }

    public void testAddEventExecute_3event_matchtwice() throws Exception {
        SequenceExpression expr = sequence2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        expr.execute((AddEventTrigger) null, event3);
        assertTrue(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 2);
        // First match set should contain both events
        assertTrue(matchSets.get(0).size() == 3);
        assertTrue(matchSets.get(0).contains(event1));
        assertTrue(matchSets.get(0).contains(event2));
        assertTrue(matchSets.get(0).contains(event3));
        // Second match set should contain only the second event
        assertTrue(matchSets.get(1).size() == 2);
        assertTrue(matchSets.get(1).contains(event2));
        assertTrue(matchSets.get(1).contains(event3));
    }

    public void testAddEventExecute_2event_sequence_matched() throws Exception {
        SequenceExpression expr = identitySequence;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(expr.isTrue());
        assertTrue(matchSets.get(0).size() == 1);
        assertTrue(matchSets.get(0).contains(event1));
        assertTrue(matchSets.get(1).size() == 1);
        assertTrue(matchSets.get(1).contains(event2));
    }

    public void testAddEventExecute_2event_sequence_notmatched() throws Exception {
        SequenceExpression expr = identitySequence;
        expr.execute((AddEventTrigger) null, event2);
        expr.execute((AddEventTrigger) null, event1);
        List<EventSet> matchSets = expr.getMatchEvents();
        assertFalse(expr.isTrue());
        assertTrue(matchSets.get(0).size() == 1);
        assertTrue(matchSets.get(0).contains(event1));
        assertTrue(matchSets.get(1).isEmpty());
    }

    public void testAddEventExecute_dupe_ignored() throws Exception {
        SequenceExpression expr = identitySequence;
        expr.registerAction(eventAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertTrue(eventMatches.size() == 1);
        expr.execute((AddEventTrigger) null, event2);
        // only the first match should be reported (i.e. not a new match)
        assertTrue(eventMatches.size() == 1);
    }

    public void testRemoveEventExecute_remove1_empty() throws Exception {
        SequenceExpression expr = sequence0;
        expr.execute((RemoveEventTrigger) null, event1);
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.size() == 0);
        assertTrue(expr.isTrue());
    }

    public void testRemoveEventExecute_remove1_matched1_false() throws Exception {
        SequenceExpression expr = sequence1;
        expr.execute((AddEventTrigger) null, event1);
        assertTrue(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).isEmpty());
    }

    public void testRemoveEventExecute_remove1_matched1_true1() throws Exception {
        SequenceExpression expr = sequence1;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertTrue(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        assertTrue(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).size() == 1);
        assertTrue(matchSets.get(0).contains(event2));
    }

    public void testRemoveEventExecute_remove1_notmatched() throws Exception {
        SequenceExpression expr = neverMatch1;
        expr.execute((AddEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).isEmpty());
    }
    public void testRemoveEventExecute_remove1_matched1_partial1() throws Exception {
        SequenceExpression expr = neverMatch2_2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).size() == 1);
        assertFalse(matchSets.get(0).contains(event1));
        assertTrue(matchSets.get(0).contains(event2));
        assertTrue(matchSets.get(1).isEmpty());
    }

    public void testRemoveEventExecute_remove1_matched1_partial2() throws Exception {
        SequenceExpression expr = sequence2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertTrue(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).size() == 1);
        assertFalse(matchSets.get(0).contains(event1));
        assertTrue(matchSets.get(0).contains(event2));
        assertTrue(matchSets.get(1).isEmpty());
    }

    public void testRemoveEventExecute_remove2_matched2_false() throws Exception {
        SequenceExpression expr = sequence2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertTrue(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        expr.execute((RemoveEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).isEmpty());
        assertTrue(matchSets.get(1).isEmpty());
    }

    public void testRemoveEventExecute_remove1_matched1_true2() throws Exception {
        SequenceExpression expr = sequence2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        expr.execute((AddEventTrigger) null, event3);
        assertTrue(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        assertTrue(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).size() == 2);
        assertTrue(matchSets.get(0).contains(event2));
        assertTrue(matchSets.get(0).contains(event3));
        assertTrue(matchSets.get(1).size() == 1);
        assertTrue(matchSets.get(1).contains(event3));
    }

    public void testRemoveEventExecute_remove2_notmatched() throws Exception {
        SequenceExpression expr = neverMatch2_1;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        expr.execute((RemoveEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).isEmpty());
        assertTrue(matchSets.get(1).isEmpty());
    }

    public void testRemoveEventExecute_remove2_partial() throws Exception {
        SequenceExpression expr = neverMatch2_2;
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        expr.execute((RemoveEventTrigger) null, event1);
        expr.execute((RemoveEventTrigger) null, event2);
        assertFalse(expr.isTrue());
        List<EventSet> matchSets = expr.getMatchEvents();
        assertTrue(matchSets.get(0).isEmpty());
        assertTrue(matchSets.get(1).isEmpty());
    }

    public void testClear_empty() throws Exception {
        sequence0.clear();
        assertTrue(sequence0.getParts().size() == 0);
        assertTrue(sequence0.getMatchEvents().size() == 0);
    }

    public void testClear_single() throws Exception {
    }

    public void testReset() throws Exception {
    }

    public void testEventMatchRegisterAction_1event_1expr_1matched() throws Exception {
        SequenceExpression expr = sequence1;
        expr.registerAction(eventAction);
        expr.execute((AddEventTrigger) null, event1);
        assertEquals(1, eventMatches.size());
        assertTrue(eventMatches.contains(event1));
    }

    public void testEventMatchRegisterAction_2event_1expr_2matched() throws Exception {
        SequenceExpression expr = sequence1;
        expr.registerAction(eventAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertEquals(2, eventMatches.size());
        assertTrue(eventMatches.contains(event1));
        assertTrue(eventMatches.contains(event2));
    }

    public void testEventMatchRegisterAction_2event_2expr_both_matched() throws Exception {
        SequenceExpression expr = sequence2;
        expr.registerAction(eventAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertEquals(1, eventMatches.size());
        assertTrue(eventMatches.contains(event2));
    }

    public void testEventMatchRegisterAction_2event_2expr_each_matched() throws Exception {
        SequenceExpression expr = identitySequence;
        expr.registerAction(eventAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertEquals(1, eventMatches.size());
        assertTrue(eventMatches.contains(event2));
    }

    public void testEventMatchUnregisterAction() throws Exception {

    }

    public void testComplexExpressionMatchRegisterAction_1event_1expr_1matched() throws Exception {
        SequenceExpression expr = sequence1;
        expr.registerAction(complexAction);
        expr.execute((AddEventTrigger) null, event1);
        assertEquals(1, complexMatches.size());
        assertEquals(1, complexMatches.get(0).count());
        assertTrue(complexMatches.get(0).getCombinations().contains(comb_1));
    }

    public void testComplexExpressionMatchRegisterAction_2event_1expr_2matched() throws Exception {
        SequenceExpression expr = sequence1;
        expr.registerAction(complexAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertEquals(2, complexMatches.size());
        assertEquals(1, complexMatches.get(0).count());
        assertTrue(complexMatches.get(0).getCombinations().contains(comb_1));
        assertEquals(1, complexMatches.get(1).count());
        assertTrue(complexMatches.get(1).getCombinations().contains(comb_2));
    }

    public void testComplexExpressionMatchRegisterAction_2event_2expr_both_matched() throws Exception {
        SequenceExpression expr = sequence2;
        expr.registerAction(complexAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertEquals(1, complexMatches.size());
        assertEquals(1, complexMatches.get(0).count());
        System.out.println("Have combinations " + complexMatches.get(0).getCombinations().toString());
        assertTrue(complexMatches.get(0).getCombinations().contains(comb_1_2));
    }

    public void testComplexExpressionMatchRegisterAction_2event_2expr_each_matched() throws Exception {
        SequenceExpression expr = identitySequence;
        expr.registerAction(complexAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        assertEquals(1, complexMatches.size());
        assertEquals(1, complexMatches.get(0).count());
        assertTrue(complexMatches.get(0).getCombinations().contains(comb_1_2));
    }

    public void testComplexExpressionMatchRegisterAction_3event_2expr_all_matched() throws Exception {
        SequenceExpression expr = sequence2;
        expr.registerAction(complexAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        expr.execute((AddEventTrigger) null, event3);
        assertEquals(2, complexMatches.size());
        assertEquals(1, complexMatches.get(0).getCombinations().size());
        assertTrue(complexMatches.get(0).getCombinations().contains(comb_1_2));
        assertEquals(2, complexMatches.get(1).count());
        assertTrue(complexMatches.get(1).getCombinations().contains(comb_1_3));
        assertTrue(complexMatches.get(1).getCombinations().contains(comb_2_3));
    }

    public void testComplexExpressionMatchRegisterAction_3event_2expr_mixed_match() throws Exception {
        SequenceExpression expr = orIdentitySequence;
        expr.registerAction(complexAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        expr.execute((AddEventTrigger) null, event3);
        assertEquals(2, complexMatches.size());
        assertEquals(1, complexMatches.get(0).getCombinations().size());
        assertTrue(complexMatches.get(0).getCombinations().contains(comb_1_2));
        assertEquals(1, complexMatches.get(1).count());
        assertTrue(complexMatches.get(1).getCombinations().contains(comb_1_3));
    }

    public void testComplexExpressionMatchRegisterAction_4event_2expr_mixed_match() throws Exception {
        SequenceExpression expr = orIdentitySequence;
        expr.registerAction(complexAction);
        expr.execute((AddEventTrigger) null, event1);
        expr.execute((AddEventTrigger) null, event2);
        expr.execute((AddEventTrigger) null, event3);
        expr.execute((AddEventTrigger) null, event4);
        assertEquals(3, complexMatches.size());
        assertEquals(1, complexMatches.get(0).getCombinations().size());
        assertTrue(complexMatches.get(0).getCombinations().contains(comb_1_2));
        assertEquals(1, complexMatches.get(1).count());
        assertTrue(complexMatches.get(1).getCombinations().contains(comb_1_3));
        assertEquals(2, complexMatches.get(2).count());
        assertTrue(complexMatches.get(2).getCombinations().contains(comb_1_4));
        assertTrue(complexMatches.get(2).getCombinations().contains(comb_3_4));
    }


    public void testComplexMatchUnregisterAction() throws Exception {

    }

    class IdentityMatcher implements Matcher {
        Event event;
        public IdentityMatcher(Event event) {
            this.event = event;
        }
        public boolean matches(Event event) {
            return this.event == event;
        }
    }
}
