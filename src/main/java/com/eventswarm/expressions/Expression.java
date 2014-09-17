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
 * Base interface for all expressions
 *
 * TODO We should include labelling in this interface, but for single event
 * expressions (i.e. not patterns) we don't need it.  So for now we'll leave it
 * out. When we do add it, we might create an abstract class with a default
 * implementation for single event expressions.
 *
 * @author andyb
 */
import com.eventswarm.Clear;
import com.eventswarm.MutableTarget;


public interface Expression extends MutableTarget, EventMatchTrigger, Clear {

    /**
     * Return true if the expression is satisfied by one or more sets of events.
     * 
     * @return
     */
    public boolean isTrue();

    /**
     * All expressions must have a unique identifier for persistence
     * 
     * @return
     */
    public String getId();

    /**
     * Reset the expression, removing any registered actions and clearing the
     * set of matches.
     */
    public void reset();
}
