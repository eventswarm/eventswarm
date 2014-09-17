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

import com.eventswarm.expressions.EventExpression;
import com.eventswarm.expressions.Expression;

import java.util.List;

/**
 * An EventPart for events that are created due to the matching of a multi-part expression.
 *
 * The getComponentExpression method provided allows the receiver of such an event to determine which expression
 * component was matched by each event in a Combination.
 *
 * @see com.eventswarm.Combination
 * @see com.eventswarm.Combinations
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface ComplexExpressionPart extends ExpressionPart {
    /**
     * Return the component expression that an event (or null) at index i of a combination has matched
     *
     * If the expression is not a complex expression, getComponentExpression(0) == getExpression.
     *
     * @return the component expression that the event at index i has matched
     */
    public Expression getComponentExpression(int i);

    /**
     * Return the component expressions in the order associated with Combination objects that match the expression
     *
     * @return the component expressions in order
     */
    public List<EventExpression> getPartsAsList();
}
