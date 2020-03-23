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
import java.util.List;

/**
 * Matcher class that matches events having at least N matches from the component
 * matcher parts.
 *
 * Default for N is 2. If less than the minimum number of matchers is provided,
 * the matcher will never match.
 *
 * @author andyb
 */
public class AtLeastNMatcher extends ComplexMatcher {

    private int minimum = 2;
    private int count = 0;

    /**
     * Create a new matcher with the specified minimum number of matches
     * 
     * @param parts
     * @param minimum
     */
    public AtLeastNMatcher(List<Matcher> parts, int minimum) {
        super(parts);
        this.minimum = minimum;
    }

    /**
     * Iterate through the Matcher parts, returning true if &ge;N match or
     * false if &lt;N match.
     *
     * This method does not short-circuit: all Matchers are tested and the number
     * of successful matches is counted. The result of this method is thread
     * safe, but note that the related count is not. See getCount.
     *
     * @param event
     * @return
     */
    public boolean matches(Event event) {
        int count = 0;
        for (Matcher matcher : this.parts) {
            if (matcher.matches(event)) count++;
        }
        this.count = count;
        return (count >= this.minimum);
    }

    /**
     * Return the number of matches in the last match attempt
     *
     * This method is not thread safe: use separate matchers in each thread if
     * concurrent matching is required with counts.
     * 
     * @return
     */
    public int getCount() {
        return this.count;
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
