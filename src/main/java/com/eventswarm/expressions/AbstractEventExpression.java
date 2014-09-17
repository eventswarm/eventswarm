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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;
import com.eventswarm.eventset.AtMostNWindow;
import java.util.Set;
import java.util.HashSet;

/**
 * Abstract class implementing the common behaviour of all event expressions.
 *
 * Event expressions have quite a few methods, but most of them are the same
 * regardless of the expression logic.  Hence this class.  
 *
 * @author andyb
 */
public abstract class AbstractEventExpression 
        implements EventExpression, AddEventTrigger, RemoveEventTrigger, MatchLimit
{
    protected Set<EventMatchAction> actions;
    protected EventSet matches;
    protected String id;
    protected int limit;

    /**
     * Default limit on the number of matches that will be held at any time
     */
    public static final int MATCH_LIMIT = 5;

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    protected void setId(String id) {
        this.id = id;
    }

    /**
     * Default implementation creates a AtMostNWindow for matches with a size of
     * MATCH_LIMIT.
     *
     * The default ID is the hashcode of the object. This will need to be
     * replaced with something more appropriate if persistence is used, since
     * the default hashcode value is only unique in the context of the JVM.
     *
     */
    public AbstractEventExpression() {
        setAttributes(MATCH_LIMIT);
    }

    /**
     * Creates a AtMostNWindow for matches with the specified size
     *
     * The default ID is the hashcode of the object. This will need to be
     * replaced with something more appropriate if persistence is used, since
     * the default hashcode value is only unique in the context of the JVM.
     *
     */
    public AbstractEventExpression(int limit) {
        setAttributes(limit);
    }

    private void setAttributes(int limit) {
        this.setLimit(limit);
        this.actions = new HashSet<EventMatchAction>();
        this.id = Integer.toHexString(this.hashCode());
    }

    /**
     * Get the current match limit
     *
     * @return
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Set the match limit, creating a AtMostNWindow of that size
     *
     * This method replaces the match set of this instance, so should be used
     * with care.
     *
     * @param limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
        this.matches = new AtMostNWindow(limit);
    }


    /**
     * This method receives events from upstream trigger implementations
     * and includes the new event if a match is found
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        // make sure the event is not already in the set of matches
        if (!this.matches.contains(event) && this.matched(trigger, event)) {
            this.matches.execute(trigger, event);
            this.fire(event);
        }
    }

    /**
     * Child classes implement the matching logic of the expression in this
     * method.
     *
     * Default implementation included for backwards compatibility
     *
     * TODO: WARNING: overriding this doesn't work in most cases due to Java's
     * somewhat funky method despatching altorithm.
     *
     * @param event
     */
    protected boolean matched(AddEventTrigger trigger, Event event) {
        return false;
    }
    
    public void execute(RemoveEventTrigger trigger, Event event) {
        this.matches.execute(trigger, event);
    }

    public boolean hasMatched(Event event) {
        return this.matches.contains(event);
    }

    /**
     * Return true if we have any matches
     *
     * @return
     */
    public boolean isTrue() {
        return !this.matches.isEmpty();
    }

    public EventSet getMatches() {
        return this.matches;
    }

    public void registerAction(EventMatchAction action) {
        this.actions.add(action);
    }

    public void unregisterAction(EventMatchAction action) {
        this.actions.remove(action);
    }

    public void registerAction(AddEventAction action) {
        this.matches.registerAction(action);
    }

    public void unregisterAction(AddEventAction action) {
        this.matches.unregisterAction(action);
    }

    public void registerAction(RemoveEventAction action) {
        this.matches.registerAction(action);
    }

    public void unregisterAction(RemoveEventAction action) {
        this.matches.unregisterAction(action);
    }

    /**
     * Clear the set of matches
     */
    public void clear() {
        this.matches.clear();
    }


    /**
     * Reset the expression, removing any registered actions and clearing the
     * set of matches.
     */
    public void reset() {
        this.actions.clear();
        this.clear();
    }

    /**
     * Helper method to fire actions when a match is found
     *
     * @param event
     */
    protected void fire(Event event) {
        for (EventMatchAction action : this.actions) {
            action.execute(this, event);
        }
    }
}
