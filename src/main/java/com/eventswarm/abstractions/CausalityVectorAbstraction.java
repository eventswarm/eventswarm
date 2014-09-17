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
 * CausalityVectorAbstraction.java
 *
 * Created on 2 May 2007, 14:56
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.abstractions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.*;
import java.util.*;

/** Maintains an up-to-date causality vector for the current event set.
 *
 * @author andyb
 */
public class CausalityVectorAbstraction extends IncrementalAbstractionImpl {
    
    private Map<Source,Event> vector;
    
    /** Creates a new instance of CausalityVectorAbstraction */
    public CausalityVectorAbstraction() {
        this.vector = new HashMap<Source,Event>();
    }
    
    /** 
     * Maintain a Map containing the most recent event from each source 
     *
     * Note that the trigger is ignored in this case, so we can use the default
     * implementation of the deprecated handleEvent method.
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        Source key = event.getHeader().getSource();
        if(vector.containsKey(key)) {
            // Same source. Replace old event, provided it really is older.
            Event old = vector.get (key);
            if (!event.isBefore(old)) {
                vector.put(key, event);
            }
        } else {
            // Source was previously unknown.  Add the event to the vector.
            vector.put(key, event);
        }
        super.execute(trigger, event);
    }

    
    /** Return the current causality vector. 
     * 
     * This method copies the state held in the abstraction so that the vector
     * is immutable.
     */
    public CausalityVector getCurrent() {
        return new CausalityVectorImpl(new HashMap<Source,Event>(vector));
    }

    public void clear() {
        this.vector.clear();
    }
}
