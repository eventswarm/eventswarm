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

package com.eventswarm.powerset;

import com.eventswarm.eventset.EventSet;

/**
 * Interface for creating new EventSets to use in a Powerset implementation
 *
 * Implementations should return a new EventSet for the specified key. The 
 * Powerset requesting the new EventSet is identified to allow factories to
 * discriminate and track eventsets and their association with the PowerSet 
 * (i.e. for a shared factory).  A null return value indicates that the PowerSet
 * should not add an EventSetfor this key value at this time.  Note that the
 * Powerset will typically call this function every time a new Eventset is
 * required, so even if a null value has previously been returned, it will still
 * be called
 *
 * @author andyb
 */
public interface EventSetFactory<Keytype> {

    EventSet createEventSet(Powerset<Keytype> pset, Keytype key);
}
