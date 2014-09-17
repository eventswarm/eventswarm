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
 * IncrementalAbstraction.java
 *
 * Created on 2 May 2007, 11:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.abstractions;

import com.eventswarm.AbstractionAddTrigger;
import com.eventswarm.events.*;
import com.eventswarm.AddEventAction;

/**
 * Interface for EventSet abstractions that can be built incrementally.
 *
 * @author andyb
 */
public interface IncrementalAbstraction extends Abstraction, AddEventAction, AbstractionAddTrigger {

    /** add a new event to the abstraction 
     * 
     * This method is deprecated in favour of the AddEventAction.execute method.
     */
    @Deprecated
    public void handleEvent(Event event);    
}
