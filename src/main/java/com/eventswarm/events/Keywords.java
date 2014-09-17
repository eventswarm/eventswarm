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

package com.eventswarm.events;

import com.eventswarm.abstractions.ValueRetriever;

import java.util.Set;

/**
 * Interface for objects, typically events, that can return a set of keywords.
 *
 * @author andyb
 */
public interface Keywords {
    public Set<String> getKeywords();

    /**
     * Static retriever class implementing a keywords retriever
     */
    public static class KeywordsRetriever implements ValueRetriever<String[]> {
        @Override
        public String[] getValue(Event event) {
            if (Keywords.class.isInstance(event)) {
                Set<String> keywords = ((Keywords) event).getKeywords();
                return keywords.toArray(new String[keywords.size()]);
            } else {
                return null;
            }
        }
    }
}
