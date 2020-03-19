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
 * StubIncrementalAbstraction.java
 *
 * Created on May 12, 2007, 6:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.eventset;

import com.eventswarm.abstractions.IncrementalAbstractionImpl;
import com.eventswarm.AddEventTrigger;
import java.util.ArrayList;
import com.eventswarm.events.Event;

/**
 *
 * @author andyb
 */
public class StubIncrementalAbstraction extends IncrementalAbstractionImpl {
    
    public static final String DEFAULTKEY = "StubAbstraction";
    public static boolean classShareAble = true;
    public ArrayList<Event> events;
    private boolean shareAble = classShareAble;
    private String keyString = DEFAULTKEY; // Have a key for hashcode so we can test parameterized abstractions
    
    /** Creates a new instance of StubIncrementalAbstraction */
    public StubIncrementalAbstraction(String keyString) {
        this.events = new ArrayList<Event>();
        this.keyString = keyString;
    }
    
    public StubIncrementalAbstraction() {
        this.events = new ArrayList<Event>();
    }
    
    public void handleEvent(Event event) {
        this.events.add(event);
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        this.handleEvent(event);
        super.execute(trigger, event);
    }

    
    public boolean shareable() {
        return this.shareAble;
    }
    
    // allow us to change the shareable property
    public void setShareable(boolean shareAble)  {
        this.shareAble = shareAble;
    }

    public int hashCode() {
       return this.keyString.hashCode();
    }

        
    public boolean equals(Object obj) {
        if (obj == null) return false;
        try {
            return this.keyString.equals(((StubIncrementalAbstraction) obj).getKeyString());
        } catch (ClassCastException exc) {
            return false;
        }
    }
    
    public String getKeyString() {
        return keyString;
    }

    public void clear() {
        events.clear();
    }
}
