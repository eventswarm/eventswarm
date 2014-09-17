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
package com.eventswarm;

import java.util.List;

/**
 * Parent interface for all Triggers.
 *
 * 
 * @author andyb
 */
public interface Trigger {

    /** All sub-interfaces should identify the corresponding action class. */
    public static Class action = Action.class;

    /** 
     * All implementations should be capable of returning a list of all 
     * triggers implemented (or actions callable) by the implementation.  Not
     * quite sure yet what this list should look like (i.e. classes or
     * instances, type constraints etc).
     * 
     * @return
     */
    // public List<? extends Trigger> getTriggers();
}
