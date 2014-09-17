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

/**
 * An event expression is one that is satisfied by a single event, but at any
 * point in time, the expression might be satisfied by more than one event.
 *
 * Implementers of this interface must implement the RemoveEventAction to
 * ensure that the number of events held by the expression can be controlled.
 *
 * @author andyb
 */
import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;

public interface EventExpression extends Expression
{
    /**
     * Return the current set of matches for the expression.
     * 
     * @return
     */
    public EventSet getMatches();

    /**
     * Return true if the expression has previously matched the identified event.
     *
     * This function is primarily for evaluation of logical expressions (AND,
     * OR, NOT, XOR).
     */
    public boolean hasMatched(Event event);
}
