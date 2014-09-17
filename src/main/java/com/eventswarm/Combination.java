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
package com.eventswarm;

import com.eventswarm.events.Event;

import java.util.List;

/**
 * A combination is a sequence of events that satisfies an expression.
 *
 * Typically this data structure is used for complex expressions, where each event in the list matches one component
 * of the complex expression. The list may contain nulls to indicate no match for a given component (and in the case
 * of an XOR, all but one element <em>must</em> be null). The sequence reflects the ordering of the expression
 * components in the expression that was matched, if any.
 *
 * If an expression is not a complex expression, then the sequence must be of length 1.
 *
 * @see com.eventswarm.events.ComplexExpressionMatchEvent
 *
 * TODO: Consider dropping this class in favour of an array or List of Event objects, since there are no methods
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface Combination extends List<Event> {}
