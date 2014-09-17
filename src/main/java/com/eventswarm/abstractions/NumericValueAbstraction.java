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

package com.eventswarm.abstractions;

/**
 * Interface for abstractions that are characterised by a single numeric value.
 *
 * Note that this interface is not associated with any particular triggers or
 * actions.
 * 
 * @author andyb
 */
public interface NumericValueAbstraction {

    /**
     * Return the current numeric value associated with the abstraction.
     * 
     * @return
     */
    public Number getValue();
}
