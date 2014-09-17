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
 * Abstraction.java
 *
 * Created on 2 May 2007, 11:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.abstractions;

import com.eventswarm.Clear;
import com.eventswarm.events.*;
import com.eventswarm.eventset.EventSet;

/**
 * Interface for abstractions over an EventSet
 *
 * Abstractions are classes that provide filters, summaries, annotations etc over
 * a set of events.  Abstractions are managed by the EventSet, so they should be 
 * created using the <code>getAbstraction</code> method of the EventSet wherever
 * possible.  This requires a no-argument constructor.  
 * 
 * If parameterisation is required (e.g. duration for sliding window filters), 
 * then these abstractions should be created by the caller and registered using 
 * the <code>registerAbstraction</code> method of the EventSet.
 *
 * @author andyb
 */
public interface Abstraction extends Clear {

    /** Create an abstraction over a set of events */
    public void buildAbstraction(EventSet set);

    /** Return true if an instance of this abstraction can be usefully 
     *  shared across many users.  
     * 
     * This is a class level boolean (i.e. either all instances can be shared or
     * none can be shared).  We can't enforce this, but errors will occur if 
     * this rule is not observed.
     *  
     * If true, the <code>getAbstraction</code> and <code>registerAbstraction</code> methods will 
     * always return an existing abstraction if it exists.  For registerAbstraction, 
     * the EventSet uses <code>equals</code> and <code>hashCode</code> methods to 
     * identify if an existing abstraction exists.  Thus, these methods should be 
     * overridden appropriately by the abstraction to ensure that we don't register 
     * multiple equivalent abstractions.  For example, if two different EventSet 
     * clients register a sliding time window abstraction with a duration of one hour, the 
     * <code>hashCode</code> method on that abstraction should return 
     * the same value if the durations match and the <code>equals</code> method
     * should return true if the durations match.
     */
    public boolean shareable();
    
    /** Return true if this abstraction is up-to-date */
    public boolean isCurrent();
    
    /** Set the status of the abstraction.  
     * 
     * This method should only be used by the EventSet.  
     */
     public void setCurrent(boolean current);
}
