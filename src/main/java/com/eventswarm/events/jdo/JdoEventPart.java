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
 * CastorEventPart.java
 *
 * Created on April 22, 2007, 10:44 AM
 *
 */

package com.eventswarm.events.jdo;

import java.util.Set;
import com.eventswarm.util.*;
import com.eventswarm.events.*;

/**
 * Implementation of the EventPart interface suitable for persistence.
 *
 * @author andyb
 */
public abstract class JdoEventPart implements EventPart {
    
    protected Event event = null;
    protected Long id;
    
    /** Creates a new instance of JdoEventPart */
    public JdoEventPart() {
        super();
    }
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
        
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean equals(Object obj) {
        if (JdoEventPart.class.isInstance(obj)) {
            return equals((JdoEventPart) obj);
        }
        return false;
    }    
    
    public boolean equals(JdoEventPart part) {
        if (!Tools.equals(event, part.getEvent())) return false;
        return true;
    }    
}
