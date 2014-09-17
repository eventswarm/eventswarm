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

import java.util.List;

/**
 * EventKey class that returns the first Matcher instance that matches the event or null if none match, allowing
 * powerset 'split' semantics to be based on more complex conditions than a simple key match.
 *
 * This class evaluates the Matchers in order and returns as soon as one matches. That is, it implements a logical OR
 * with short-circuit evaluation.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MatcherEventKey implements EventKey<Matcher> {
    private Matcher matchers[];

    /**
     * Create a MatcherEventKey implementation using the specified matchers.
     *
     * @param matchers
     */
    public MatcherEventKey(Matcher matchers[]) {
        this.matchers = matchers;
    }

    /**
     * Return the first matcher that matches
     *
     * @param event
     * @return
     */
    @Override
    public Matcher getKey(Event event) {
        for (Matcher matcher : this.matchers) {
            if (matcher.matches(event)) return matcher;
        }
        return null;
    }
}
