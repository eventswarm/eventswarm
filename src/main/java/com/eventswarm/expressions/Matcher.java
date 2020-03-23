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

import com.eventswarm.events.Event;

/**
 * Interface for classes that match an event.
 *
 * This interface is used in conjunction with logical expressions to construct
 * complex matching logic without the overhead of collecting matching events
 * in an EventSet.
 *
 * @author andyb
 */
public interface Matcher {
    public boolean matches(Event event);

    /** A Matcher that is always true */
    public static Matcher TRUEMATCHER = new Matcher() {
        public boolean matches(Event event) {
            return true;
        }
    };

    /** A Matcher that is always false */
    public static Matcher FALSEMATCHER = new Matcher() {
        public boolean matches(Event event) {
            return false;
        }
    };
}
