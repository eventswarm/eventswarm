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
 * StubAbstraction.java
 *
 * Created on May 12, 2007, 6:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.eventset;


import com.eventswarm.abstractions.Abstraction;
import java.util.ArrayList;
import java.util.Iterator;
import com.eventswarm.events.Event;

/**
 *
 * @author andyb
 */
public class StubAbstraction implements Abstraction {
    
    public static final String DEFAULTKEY = "StubAbstraction";
    public static boolean classShareAble = true;
    public ArrayList<Event> events;
    private boolean current=false;
    private boolean shareAble = classShareAble;
    private String keyString = DEFAULTKEY;
    
    /** Creates a new instance of StubAbstraction */
    public StubAbstraction(String keyString) {
        this.events = new ArrayList<Event>();
        this.keyString = keyString;
    }
    
    public StubAbstraction() {
        this.events = new ArrayList<Event>();
    }

    public void buildAbstraction(EventSet set) {
        // just copy events, in order
        events.clear();
        Iterator<Event> iter = set.iterator();
        while (iter.hasNext()) {
            events.add(iter.next());
        }
        this.current = true;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public boolean shareable() {
        return this.shareAble;
    }
    
    public void setShareable(boolean shareAble) {
        this.shareAble = shareAble;
    }

    public boolean isCurrent() {
        return(this.current);
    }
    
    public boolean equals(Object obj) {
        if (obj == null) return false;
        try {
            return this.keyString.equals(((StubAbstraction) obj).getKeyString());
        } catch (ClassCastException exc) {
            return false;
        }
    }
    
    public int hashCode() {
        return this.keyString.hashCode();
    }

    public String getKeyString() {
        return keyString;
    }

    public void clear() {
        events.clear();
    }
}
