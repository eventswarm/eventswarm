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
package com.eventswarm.events.jdo;

import com.eventswarm.Combination;
import com.eventswarm.events.*;
import com.eventswarm.expressions.EventExpression;
import com.eventswarm.expressions.Expression;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoComplexExpressionMatchEvent extends JdoActivity implements ComplexExpressionMatchEvent {

    public static String COMBINATIONS="combinations";
    public static String EXPRESSION="expression";

    private transient ComplexExpressionPart expression;
    private transient CombinationsPart combinations = null;
    private transient Set<Combination> matches;

    private JdoComplexExpressionMatchEvent() {
        super();
    }

    public JdoComplexExpressionMatchEvent(ComplexExpressionPart expression, CombinationsPart combinations) {
        super();
        Map<String, EventPart> parts = new HashMap<String, EventPart>();
        parts.put(COMBINATIONS, combinations);
        parts.put(EXPRESSION, expression);
        this.setParts(parts);
        // Use the timestamp and source of the last event
        // Set sequence number to -1 because we distinguish activities using their component events so this is not used
        Header header = new JdoHeader(events.last().getHeader().getTimestamp(), -1, events.last().getHeader().getSource());
        this.setHeader(header);
    }

    @Override
    public void setParts(Map<String, EventPart> eventParts) {
        this.combinations = (CombinationsPart) eventParts.get(COMBINATIONS);
        this.expression = (ComplexExpressionPart) eventParts.get(EXPRESSION);
        this.events = this.combinations.getEvents();
        super.setParts(eventParts);
    }

    @Override
    public Set<Combination> getCombinations() {
        if (this.matches == null) {
            this.matches = this.combinations.getCombinations();
        }
        return this.matches;
    }

    @Override
    public int count() {
        return this.combinations.count();
    }

    @Override
    public Expression getComponentExpression(int i) {
        return this.expression.getComponentExpression(i);
    }

    @Override
    public List<EventExpression> getPartsAsList() {
        return this.expression.getPartsAsList();
    }

    @Override
    public Expression getExpression() {
        return this.expression.getExpression();
    }

    // TODO: implement a compareTo method that distinguishes different combinations of the same underlying events
}
