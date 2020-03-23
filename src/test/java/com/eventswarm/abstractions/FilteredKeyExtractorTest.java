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
package com.eventswarm.abstractions;

import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Keywords;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class FilteredKeyExtractorTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void excludeOneFromMany() throws Exception {
        String[] stopwords = {"a"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(2, result.size());
        assertFalse(result.contains("a"));
    }

    @Test
    public void excludeOneFromOne() throws Exception {
        String[] stopwords = {"a"};
        String[] keywords = {"a"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        String[] result = extractor.getValue(event);
        assertNull(result);
    }

    @Test
    public void excludeManyFromMany() throws Exception {
        String[] stopwords = {"a", "c"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(1, result.size());
        assertTrue(result.contains("b"));
    }

    @Test
    public void excludeOneOfTwoFromMany() throws Exception {
        String[] stopwords = {"a", "d"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(2, result.size());
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    public void excludeAllFromMany() throws Exception {
        String[] stopwords = {"a", "b", "c"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        String[] result = extractor.getValue(event);
        assertNull(result);
    }

    @Test
    public void excludeFromNone() throws Exception {
        String[] stopwords = {"a", "b", "c"};
        String[] keywords = {};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        String[] result = extractor.getValue(event);
        assertNull(result);
    }

    @Test
    public void excludeLessThanMin() throws Exception {
        String[] stopwords = {"a"};
        String[] keywords = {"a", "bbbb", "cc"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)), 3);
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(1, result.size());
        assertFalse(result.contains("cc"));
    }

    @Test
    public void includeEqualsMin() throws Exception {
        String[] stopwords = {"a"};
        String[] keywords = {"a", "bbbb", "cc"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)), 2);
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(2, result.size());
        assertTrue(result.contains("cc"));
    }
    @Test
    public void excludeNone() throws Exception {
        String[] stopwords = {};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(3, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }


    @Test
    public void addKey() throws Exception {
        String[] stopwords = {"a"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        extractor.add("c");
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(1, result.size());
        assertTrue(result.contains("b"));
    }

    @Test
    public void addExistingKey() throws Exception {
        String[] stopwords = {"a"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        extractor.add("a");
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(2, result.size());
        assertTrue(result.contains("b"));
        assertTrue(result.contains("c"));
    }

    @Test
    public void removeKey() throws Exception {
        String[] stopwords = {"a", "c"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        extractor.remove("c");
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(2, result.size());
        assertFalse(result.contains("a"));
    }

    @Test
    public void removeNonExistentKey() throws Exception {
        String[] stopwords = {"a", "c"};
        String[] keywords = {"a", "b", "c"};
        Event event = new KeywordsEvent(keywords);
        FilteredKeywordExtractor extractor = new FilteredKeywordExtractor(new HashSet<String>(Arrays.asList(stopwords)));
        extractor.remove("d");
        List<String> result = Arrays.asList(extractor.getValue(event));
        assertEquals(1, result.size());
        assertTrue(result.contains("b"));
    }

    @Test
    public void loadDefaultStopwords() throws Exception {
        Set<String> result = FilteredKeywordExtractor.defaultWords();
        assertEquals(646, result.size());
        assertTrue(result.contains("www"));
    }

    public static class KeywordsEvent extends JdoEvent implements Keywords {
        public static Source SOURCE = new JdoSource("FilteredKeyExtractorTest");

        private Set<String> words;

        public KeywordsEvent(String[] words) {
            super(new JdoHeader(new Date(), SOURCE), ((Map<String, EventPart>) null));
            this.words = new HashSet<String>(Arrays.asList(words));
        }

        public Set<String> getKeywords() {
            return words;
        }
    }
}
