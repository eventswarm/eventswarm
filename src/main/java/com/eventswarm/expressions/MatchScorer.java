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

import com.eventswarm.events.Event;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Scorer that scores an event for each Matcher that succeeds
 *
 * @author andyb
 */
public class MatchScorer implements Scorer {
    
    protected Map<Matcher,Number> parts;

    private MatchScorer() {
        super();
    }

    /**
     * Create a MatchScorer from an array of Matchers using using a constant
     * score for each match.
     *
     * @param matchers
     * @param matchScore
     */
    public MatchScorer(Matcher matchers[], int matchScore) {
        super();
        this.parts = new LinkedHashMap<Matcher,Number>();
        for (int i=0; i<matchers.length; i++) {
            this.parts.put(matchers[i], new Integer(matchScore));
        }
    }

    /**
     * Create a MatchScorer from a map linking each Matcher to an Integer score
     *
     * @param parts
     */
    public MatchScorer(Map<Matcher,Number> parts) {
        super();
        this.setParts(parts);
    }

    /**
     * Return a score equal to the total of scores for each Matcher that matches
     * the event.
     *
     * @param event
     * @return
     */
    public int score(Event event) {
        Iterator<Map.Entry<Matcher,Number>> iter = this.parts.entrySet().iterator();
        Map.Entry<Matcher,Number> entry;
        int score = 0;
        while (iter.hasNext()) {
            entry = iter.next();
            if (entry.getKey().matches(event)) score += entry.getValue().intValue();
        }
        return score;
    }

    public Map<Matcher,Number> getParts() {
        return parts;
    }

    private void setParts(Map<Matcher,Number> parts) {
        this.parts = parts;
    }
}
