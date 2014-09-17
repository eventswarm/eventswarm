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

/**
 * Event created to signal a single match for a multi-part expression.
 *
 * Each match is a combination of events, that is, an ordered list of events each of which satisfy a component of the
 * multi-part expression.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface CombinationMatchEvent extends ComplexExpressionPart, Activity {
    /**
     * Return the Combination of events that has matched, in order.
     *
     * The Combination may contain nulls, e.g. for an XOR or OR expression.
     * The Combination will have a length equal to the number of expression parts, that is:
     *
     * @pre getExpression().getParts().size()
     *
     * @see Combination
     *
     * @return the Combination of events that has matched
     */
    public Combination getCombination();
}
