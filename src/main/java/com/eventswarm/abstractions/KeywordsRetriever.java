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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Retriever class that splits the text returned by a string retriever into distinct keywords with an optional
 * stopword filter.
 *
 * This class extends the FilteredKeywordExtractor (which depends on the older 'Keywords' interface) to implement
 * stopword filtering.
 *
 * TODO: the implementation is incomplete, so do not use
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class KeywordsRetriever extends FilteredKeywordExtractor implements ValueRetriever<String[]> {

    private ValueRetriever<String> stringRetriever;

    private Pattern regex;

    public static final String WORD_SPLITTER = "\\b";

    public KeywordsRetriever(ValueRetriever<String> stringRetriever) {
        super(new HashSet<String>());
        this.stringRetriever = stringRetriever;
        this.regex = Pattern.compile(WORD_SPLITTER);
    }

    public KeywordsRetriever(Set<String> exclude, ValueRetriever<String> stringRetriever) {
        super(exclude);
        this.stringRetriever = stringRetriever;
    }

    public KeywordsRetriever(Set<String> exclude, int minLength, ValueRetriever<String> stringRetriever) {
        super(exclude, minLength);
        this.stringRetriever = stringRetriever;
    }

    @Override
    public String[] getValue(Event event) {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
