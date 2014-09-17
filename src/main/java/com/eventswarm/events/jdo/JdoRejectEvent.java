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
 * JdoRejectEvent.java
 *
 * Created on 27 April 2007, 12:52
 *
 */

package com.eventswarm.events.jdo;

import com.eventswarm.util.*;
import com.eventswarm.events.RejectEvent;

/**
 * Simple, persistable implementation of the Reject event part type.
 *
 * @author andyb
 */
public class JdoRejectEvent extends JdoEventPart implements RejectEvent {
    
    protected String reason;
    
    /**
     * Creates a new instance of JdoRejectEvent
     */
    public JdoRejectEvent() {
        super();
    }
    
    public JdoRejectEvent(String reason) {
        super();
        this.reason = reason;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public boolean equals(Object obj) {
        if (JdoRejectEvent.class.isInstance(obj)) {
            return equals((JdoRejectEvent) obj);
        }
        return false;
    }
    
    private boolean equals (JdoRejectEvent part) {
        if (super.equals(part))                      {return false;}
        if (!Tools.equals(reason, part.getReason())) {return false;}
        return true;
    }
}