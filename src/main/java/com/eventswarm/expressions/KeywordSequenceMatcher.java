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

import com.eventswarm.events.Event;
import com.eventswarm.events.OrderedKeywords;
import org.apache.log4j.Logger;

/**
 * Matcher that matches a sequence of keywords for events that implement the OrderedKeywords interface.
 *
 * An empty keyword sequence always matches. As with the regular keyword matcher, the match is case sensitive. Fold
 * keywords to lower case if case-insensitive matches are required.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class KeywordSequenceMatcher implements Matcher {
    private String keywords[];

    private static Logger logger = Logger.getLogger(KeywordSequenceMatcher.class);

    public KeywordSequenceMatcher(String[] keywords) {
        this.keywords = keywords;
        if (keywords.length == 0) {
            logger.warn("Empty keyword list for sequence matcher");
        }
    }

    @Override
    public boolean matches(Event event) {
        if (keywords.length == 0) {
            return true; // empty always matches
        }
        if (!OrderedKeywords.class.isInstance(event)) {
            logger.warn("Not ordered keyword implementation, so match is false");
            return false; // only OrderedKeywords instances are supported
        } else {
            int current = 0;
            int count = 0;
            for (String keyword : ((OrderedKeywords) event).getOrderedKeywords()) {
                count++;
                if (keyword.equals(keywords[current])) {
                    current++;
                    if (current == keywords.length) {
                        logger.debug("Matched");
                        return true;
                    }
                    // could think about short-cutting here if we don't have enough words left for a match
                    // but perhaps over-complicated
                    logger.debug("Single word match at " + Integer.toString(count) + ", incrementing match pointer");
                } else {
                    logger.debug("Resetting match pointer");
                    current = 0;
                }
            }
            logger.debug("Exiting loop without finding a match");
            return false; // if we fall out of the loop without  matching, result is false
        }
    }

    public String[] getKeywords() {
        return keywords;
    }

    private void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }
}
