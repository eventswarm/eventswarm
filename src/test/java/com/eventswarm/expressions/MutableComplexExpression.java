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

import com.eventswarm.events.ComplexExpressionMatchEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoComplexExpressionMatchEvent;
import com.eventswarm.events.jdo.JdoComplexExpressionPart;
import com.eventswarm.events.jdo.JdoCondensedCombinationsPart;

import java.util.*;

/**
 * Simple test class that allows us to control the state of a nested expression
 *
 * @author andyb
 */
public class MutableComplexExpression extends MutableExpression implements ComplexExpression {
    Set<ComplexExpressionMatchAction> actions = new HashSet<ComplexExpressionMatchAction>();

    public List<EventExpression> getPartsAsList() {
        return null;
    }

    public void registerAction(ComplexExpressionMatchAction action) {
        this.actions.add(action);
    }

    public void unregisterAction(ComplexExpressionMatchAction action) {
        this.actions.remove(action);
    }

    protected void fire(Event event) {
        super.fire(event);
        TreeSet<Event> events = new TreeSet<Event>(); events.add(event);
        List<SortedSet<Event>> combinations = new ArrayList<SortedSet<Event>>(); combinations.add(events);
        ComplexExpressionMatchEvent cevent = new JdoComplexExpressionMatchEvent(new JdoComplexExpressionPart(this), new JdoCondensedCombinationsPart(combinations));
        for (ComplexExpressionMatchAction action : actions) {
            action.execute(this, cevent);
        }
    }

    public Collection<? extends Expression> getParts() {
        return null;
    }
}
