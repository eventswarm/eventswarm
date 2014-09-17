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
package com.eventswarm.events;

import com.eventswarm.Combination;
import com.eventswarm.Combinations;

import java.util.Set;

/**
 * A CombinationsPart is an EventPart capturing a set of event combinations, typically representing multiple
 * matches for a multipart expression.
 *
 * @see com.eventswarm.Combination
 * @see ComplexExpressionMatchEvent
 * @see com.eventswarm.events.CombinationMatchEvent
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface CombinationsPart extends NestedEvents, Combinations {
    /**
     * Return a set of combinations of events that satisfy the expression
     *
     * Combinations will all have a length equal to the number of expression parts, that is:
     *
     * @pre getExpression().getParts().size()
     *
     * @see com.eventswarm.Combination
     *
     * @return
     */
    public Set<Combination> getCombinations();

    /**
     * Returns the number of combinations captured in this match.
     *
     * @return
     */
    public int count();
}
