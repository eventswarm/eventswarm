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

package com.eventswarm.expressions;

import com.eventswarm.events.Event;
import java.util.List;

/**
 * Matcher that negates the nominated matcher.
 *
 * @author andyb
 */
public class NOTMatcher implements Matcher {
    
    protected Matcher negate;

    protected NOTMatcher() {
        super();
    }

    public NOTMatcher(Matcher negate) {
        this.negate = negate;
    }

    public boolean matches(Event event) {
        return !negate.matches(event);
    }

    public Matcher getNegate() {
        return negate;
    }

    private void setNegate(Matcher negate) {
        this.negate = negate;
    }
}
