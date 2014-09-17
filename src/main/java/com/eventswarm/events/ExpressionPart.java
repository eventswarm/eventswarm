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

import com.eventswarm.expressions.Expression;

/**
 * An EventPart for events that are created due to the matching of an expression.
 *
 * This interface and its derivatives are typically used for complex expressions, where there are multiple events
 * matched by the expression. For single-event matches, the event that matched will usually be passed.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface ExpressionPart extends EventPart {
    /**
     * Return the expression that has been matched
     *
     * @return
     */
    public Expression getExpression();
}
