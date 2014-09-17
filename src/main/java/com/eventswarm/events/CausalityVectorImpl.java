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
 * CausalityVectorImpl.java
 *
 * Created on 29 April 2007, 15:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.events;

import java.util.*;
import com.eventswarm.eventset.*;

/**
 * This is an implementation of causality vectors based on the identification
 * of a set of directly preceding events.  
 *
 * @author andyb
 */
public class CausalityVectorImpl implements CausalityVector {
    
    // Let people initialise us using just a set of events
    private Set<Event> predecessors;
    // but build a map to make comparisons efficient
    private Map<Source,Event> vector = null;
    
    /**
     * Creates a new instance of CausalityVectorImpl
     */
    public CausalityVectorImpl() {
        super();
    }
    
    /** Creates a new instance using the supplied vector */
    public CausalityVectorImpl(Map<Source,Event> vector) {
        this.vector = vector;
    }
    
    // persist using the vector
    private Map<Source,Event> getVector () {
        return this.vector;
    }
    
    private void setVector(Map<Source,Event> vector) {
        this.vector = vector;
    }
    
    public boolean isBefore(CausalityVectorImpl vector) {
        return false;
    }
    
    public boolean isAfter(CausalityVectorImpl vector) {
        return false;
    }
    
    public boolean isConcurrent() {
        return true;
    }

}
