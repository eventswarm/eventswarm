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
 * A ComplexExpressionMatchEvent is an event that signals multiple matches against a multipart expression.
 *
 * This event contains a set of matches for the complex expression, where each match is a Combination (a set of
 * one or more events, ordered by component expression). Depending on the trigger that has fired, this might be the
 * set of new Combinations arising from an added event or a complete set of current Combinations that match.
 *
 * This event is intended to compress the number of events generated when the addition of a single event causes
 * multiple combinations to satisfy an expression. For example, in a sequence expression with 3 component expressions,
 * if the first two components have 10 and 5 matching events respectively, a new match for the third component realises
 * 50 new combinations of events that match the expression. Reporting each match separately as a CombinationMatchEvent
 * is expensive and seldom required.
 *
 * @see Combination
 * @see CombinationsPart
 * @see CombinationMatchEvent
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface ComplexExpressionMatchEvent extends Activity, ComplexExpressionPart, CombinationsPart {}
