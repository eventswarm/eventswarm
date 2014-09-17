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
import java.util.Iterator;
import java.util.List;

/**
 * Match if the event matches any matcher in the list, evaluating the list elements in strictly sequential order.
 *
 * This class implements short-circuit evaluation, that is, it returns as soon as one of the Matcher elements returns
 * true for the supplied event.
 *
 * @author andyb
 */
public class ORMatcher extends ComplexMatcher {

    public ORMatcher(List<Matcher> parts){
        super(parts);
    }

    public boolean matches(Event event) {
        Iterator<Matcher> iter = this.parts.iterator();
        while (iter.hasNext()) {
            if (iter.next().matches(event)) return true;
        }
        return false;
    }
}
