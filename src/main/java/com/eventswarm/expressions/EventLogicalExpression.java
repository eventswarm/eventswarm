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
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Abstract parent class for multi-component event logical expressions to minimise
 * duplicated code.  The only difference between AND, OR and XOR is in the
 * matching logic (i.e. executing the AddEventAction).
 *
 * Note that to minimise overheads, this class expects that subclasses will
 * remove events from component expressions immediately.
 *
 * @author andyb
 */
public abstract class EventLogicalExpression extends AbstractEventExpression
        implements MultipartExpression {

    protected List<EventExpression> parts;

    // implementing classes should set this value appropriately
    protected static String joiner = "AND/OR/XOR";

    /**
     * Create a conjunction of the provided expressions.
     *
     * To minimise overheads, the class removes events from component expressions
     * immediately. Thus component expressions should not be used elsewhere.
     *
     * Null expressions are discarded, if supplied.
     *
     * @param expr1
     * @param expr2
     */
    public EventLogicalExpression(EventExpression expr1, EventExpression expr2) {
        super();
        this.parts = new ArrayList<EventExpression>(2);
        this.parts.add(expr1);
        this.parts.add(expr2);
        this.setAttributes(parts);
    }

    /**
     * Create a conjunction of the provided expressions.
     *
     * To minimise overheads, the class removes events from component expressions
     * immediately. Thus component expressions should not be used elsewhere.
     * 
     * This constructor removes nulls from the list, if present.
     *
     * @param expr1
     * @param expr2
     */
    public EventLogicalExpression(List<EventExpression> parts) {
        super();
        this.setAttributes(parts);
    }

    /**
     * As per list-only constructor, but setting the specified limit on the
     * number of matches held.
     *
     */
    public EventLogicalExpression(List<EventExpression> parts, int limit) {
        super(limit);
        this.setAttributes(parts);
    }

    protected void setAttributes(List<EventExpression> parts) {
        this.parts = parts;
        // remove nulls, relying on the removal to return false when none are found
        while (this.parts.remove(null)) {}
    }

    /**
     * Hide the default constructor
     */
    protected EventLogicalExpression() {
    }

    /**
     * Return the components of this complex expression
     * 
     * @return
     */
    public List<EventExpression> getParts() {
        return this.parts;
    }

    protected String getJoiner() {
        // implementing classes should set this value appropriately
        return "AND/OR/XOR";
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String fill = "\n  ";
        String join = fill + this.getJoiner() + " ";
        buf.append ("[[" + fill);
        for (EventExpression expr : this.parts) {
            //buf.append(" ");
            buf.append(expr.toString().replaceAll("\n", fill));
            buf.append(join);
        }
        buf.delete(buf.lastIndexOf(join), buf.length());
        buf.append("\n]]");
        return (buf.toString());
    }

    /**
     * Implement clear by clearing all of the parts
     */
    @Override
    public void clear() {
        for (EventExpression expr : parts) {
            expr.clear();
        }
        super.clear();
    }
}
