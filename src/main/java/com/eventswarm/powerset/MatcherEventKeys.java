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
package com.eventswarm.powerset;

import com.eventswarm.events.Event;
import com.eventswarm.expressions.Matcher;

import java.util.ArrayList;

/**
 * EventKey class that returns the set of Matcher instances that match the event or null if none match, allowing
 * powerset 'split' semantics to be based on more complex conditions than a simple key match.
 *
 * This class evaluates all Matchers and returns all that match. That is, it implements a logical OR.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MatcherEventKeys implements EventKeys<Matcher> {
    private Matcher matchers[];

    /**
     * Create a MatcherEventKeys implementation using the specified matchers.
     *
     * @param matchers The array of matchers to evaluate for each event
     */
    public MatcherEventKeys(Matcher matchers[]) {
        this.matchers = matchers;
    }

    /**
     * Return the set of matchers that match the event in an array
     *
     * @param event
     * @return Array of Matchers that match the event
     */
    @Override
    public Matcher[] getKeys(Event event) {
        ArrayList<Matcher> result = new ArrayList<Matcher>(matchers.length);
        for (Matcher matcher : this.matchers) {
            if (matcher.matches(event)) result.add(matcher);
        }
        return result.toArray(new Matcher[result.size()]);
    }
}
