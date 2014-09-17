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
 * JdoNestedEvent.java
 *
 * Created on 29 May 2007, 16:20
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.events.jdo;

import com.eventswarm.util.*;
import com.eventswarm.events.*;

/**
 *
 * @author vji
 */
public class JdoNestedEvent extends JdoEventPart implements NestedEvent {
    
    private Event nestedEvent = null;
    
    /**
     * Creates a new instance of JdoNestedEvent
     */
    public JdoNestedEvent() {
        super();
    }

    public JdoNestedEvent(Event nestedEvent) {
        super();
        this.nestedEvent = nestedEvent;
    }
    
    public Event getNestedEvent() {
        return this.nestedEvent;
    }
    
    public void setNestedEvent(Event nestedEvent) {
        this.nestedEvent = nestedEvent;
    }    

    public boolean equals(Object obj) {
        if (JdoNestedEvent.class.isInstance(obj)) {
            return equals((JdoNestedEvent) obj);
        }
        return false;
    }
    
    private boolean equals (JdoNestedEvent part) {
        if (super.equals(part))                                {return false;}
        if (!Tools.equals(nestedEvent, part.getNestedEvent())) {return false;}
        return true;
    }    
}