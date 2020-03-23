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
import com.eventswarm.events.Keywords;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Keyword extractor that removes unwanted words from to the set of words returned by an event
 * Keywords implementation, typically used to remove stopwords.
 *
 * The implementation provides two filtering mechanisms: an exclusion set and a minimum length.
 * The exclusion set is mandatory, the default minimum length is 1 (i.e. no minimum). In most
 * circumstances, we would recommend a minimum length of 3.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class FilteredKeywordExtractor implements ValueRetriever<String[]> {
    public static int DEFAULT_MIN_LENGTH = 1;
    public static String DEFAULT_EN_WORDS = "stopwords/stopwords_en.txt";
    public static String DEFAULT_WEB_WORDS = "stopwords/stopwords_web.txt";
    private Set<String> exclude;
    private int minLength;

    /**
     * Exclude words from the specified set with no minimum length.
     *
     * @param exclude
     */
    public FilteredKeywordExtractor(Set<String> exclude) {
        this.exclude = exclude;
        this.minLength = DEFAULT_MIN_LENGTH;
    }

    /**
     * Exclude words from the specified set with a length greater than or equal to minLength
     *
     * @param exclude
     * @param minLength
     */
    public FilteredKeywordExtractor(Set<String> exclude, int minLength) {
        this.exclude = exclude;
        this.minLength = minLength;
    }

    /**
     * Return a filtered array of keywords from the supplied event or null if there are no keywords
     *
     * @param event
     * @return
     */
    public String[] getValue(Event event) {
        if (!Keywords.class.isInstance(event)) return null;
        return filterKeywords(((Keywords) event).getKeywords());
    }

    /**
     * Do the actual filtering in a way that is re-usable by subclasses (i.e. not tied to the Keywords interface)
     *
     * @param keys
     * @return
     */
    protected String[] filterKeywords(Iterable<String> keys) {
        ArrayList<String> result = new ArrayList<String>();
        for (String key : keys) {
            if (include(key)) {
                result.add(key);
            }
        }
        if (result.size() > 0) {
            return result.toArray(new String[result.size()]);
        } else {
            return null;
        }
    }

    /**
     * Apply stopword filtering, returning true if the keyword should be included
     *
     * @param keyword
     * @return
     */
    protected boolean include(String keyword) {
        return !exclude.contains(keyword) && keyword.length() >= minLength;
    }

    /**
     * Add a new key to the excluded set
     *
     * @param key
     */
    public void add(String key) {
        exclude.add(key);
    }

    /**
     * Remove a key from the excluded set
     *
     * @return
     */
    public void remove(String key) {
        exclude.remove(key);
    }

    public Set<String> getExclude() {
        return exclude;
    }

    public void setExclude(Set<String> exclude) {
        this.exclude = exclude;
    }

    /**
     * Utility method to read stopwords using a reader
     *
     * @param reader
     * @return
     */
    public static Set<String> readLines(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        Set<String> stopwords = new HashSet<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stopwords.add(line);
        }
        return stopwords;
    }

    /**
     * Read default English + web stopwords into a set
     *
     * The default stopwords are included in the EventSwarm jar
     */
    public static Set<String> defaultWords() throws IOException {
        InputStream stream = FilteredKeywordExtractor.class.getClassLoader().getResourceAsStream(DEFAULT_EN_WORDS);
        Set<String> result = readLines(new InputStreamReader(stream));
        result.addAll(readLines(new InputStreamReader(FilteredKeywordExtractor.class.getClassLoader().getResourceAsStream(DEFAULT_WEB_WORDS))));
        return result;
    }
}
