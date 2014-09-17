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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.expressions;

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import com.eventswarm.events.Keywords;

import java.util.regex.Pattern;

/**
 * Matcher to match a regular expression in a string retrieved by a value retriever
 *
 * This implementation uses the Java Pattern class and an associated Matcher instance to perform the matching.
 * Regular expression syntax is defined in the oracle API for java.util.regex.Pattern.
 *
 * @author andyb
 */
public class StringValueRegexMatcher implements Matcher {

    private String regex = null;
    private Pattern pattern = null;
    private ValueRetriever<String> retriever;

    private StringValueRegexMatcher() {
        super();
    }

    public StringValueRegexMatcher(String regex, ValueRetriever<String> retriever) {
        super();
        this.setRegex(regex);
        this.setRetriever(retriever);
    }

    /**
     * Check for a match of the regex in the string retrieved using the retriever
     *
     * @param event
     * @return true if the regex matches the retrieved string
     */
    public boolean matches(Event event) {
        return pattern.matcher(retriever.getValue(event)).matches();
    }

    public String getRegex() {
        return regex;
    }

    private void setRegex(String regex) {
        this.regex = regex;
        this.pattern = Pattern.compile(regex);
    }

    public ValueRetriever<String> getRetriever() {
        return retriever;
    }

    private void setRetriever(ValueRetriever<String> retriever) {
        this.retriever = retriever;
    }
}
