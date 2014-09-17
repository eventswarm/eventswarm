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

import com.eventswarm.events.OrderedKeywords;
import com.eventswarm.events.jdo.JdoEvent;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class KeywordSequenceMatcherTest {
    @Test
    public void emptyIsTrue() throws Exception {
        String matchSequence[] = {};
        String eventSequence[] = {"a", "b"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void emptyIsTrueForEmpty() throws Exception {
        String matchSequence[] = {};
        String eventSequence[] = {};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void matchSingleInSingle() throws Exception {
        String matchSequence[] = {"a"};
        String eventSequence[] = {"a"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void matchSingleAtBeginning() throws Exception {
        String matchSequence[] = {"a"};
        String eventSequence[] = {"a", "b"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void matchSingleAtEnd() throws Exception {
        String matchSequence[] = {"a"};
        String eventSequence[] = {"b", "a"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void noMatchSingleInSingle() throws Exception {
        String matchSequence[] = {"a"};
        String eventSequence[] = {"b"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertFalse(result);
    }

    @Test
    public void noMatchSingleInMulti() throws Exception {
        String matchSequence[] = {"a"};
        String eventSequence[] = {"b", "c"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertFalse(result);
    }

    @Test
    public void matchMultiComplete() throws Exception {
        String matchSequence[] = {"a", "b"};
        String eventSequence[] = {"a", "b"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void matchMultiAtBeginning() throws Exception {
        String matchSequence[] = {"a", "b"};
        String eventSequence[] = {"a", "b", "c"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void matchMultiAtEnd() throws Exception {
        String matchSequence[] = {"a", "b"};
        String eventSequence[] = {"c", "a", "b"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    @Test
    public void matchMultiAtEndRepeated() throws Exception {
        String matchSequence[] = {"a", "b"};
        String eventSequence[] = {"b", "a", "b"};
        KeywordSequenceMatcher instance = new KeywordSequenceMatcher(matchSequence);
        boolean result = instance.matches(new SequencedKeywordsEvent(eventSequence));
        assertTrue(result);
    }

    public class SequencedKeywordsEvent extends JdoEvent implements OrderedKeywords {
        private List<String> keywords;

        public SequencedKeywordsEvent(String[] keywords) {
            this.keywords = Arrays.asList(keywords);
        }

        @Override
        public List<String> getOrderedKeywords() {
            return keywords;
        }
    }
}
