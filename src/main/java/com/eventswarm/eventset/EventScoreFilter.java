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

package com.eventswarm.eventset;

import com.eventswarm.events.Event;
import com.eventswarm.expressions.Matcher;
import com.eventswarm.expressions.Scorer;

/**
 * Filters events using a scorer to allow only events that meet a minimum score.
 *
 * @author andyb
 */
public class EventScoreFilter extends AbstractFilter {

    private Scorer scorer;
    private int minimum;

    private EventScoreFilter() {
        super();
    }

    /**
     * Create a new EventScoreFilter with using the specified scorer and
     * minimum score.
     * 
     * @param scorer
     * @param minimum
     */
    public EventScoreFilter(Scorer scorer, int minimum) {
        super();
        this.setScorer(scorer);
        this.setMinimum(minimum);
    }

    @Override
    protected boolean include(Event event) {
        return (this.scorer.score(event) < this.minimum);
    }

    /**
     * Get the value of scorer
     *
     * @return the value of scorer
     */
    public Scorer getScorer() {
        return scorer;
    }

    /**
     * Set the value of scorer
     *
     * @param scorer new value of scorer
     */
    private void setScorer(Scorer scorer) {
        this.scorer = scorer;
    }

    /**
     * Get the value of minimum
     *
     * @return the value of minimum
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * Set the value of minimum
     *
     * @param minimum new value of minimum
     */
    private void setMinimum(int minimum) {
        this.minimum = minimum;
    }
}
