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

package com.eventswarm.abstractions;

/**
 *
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import com.eventswarm.AbstractionAddAction;
import com.eventswarm.AbstractionRemoveAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.*;

import java.util.HashSet;
import java.util.Set;

public abstract class MutableAbstractionImpl 
        extends IncrementalAbstractionImpl implements MutableAbstraction 
{

    protected Set<AbstractionRemoveAction> removeActions;

    /**
     * Constructor calls parent constructor and creates a set of remove actions.
     */
    public MutableAbstractionImpl() {
        super();
        this.removeActions = new HashSet<AbstractionRemoveAction>();
    }

    /* private logger for log4j */
    private static Logger logger = Logger.getLogger(MutableAbstractionImpl.class);

    /**
     * Process the removal of an event from an abstraction
     *
     * This method is a placeholder that just calls any registered remove actions and assumes that the abstraction
     * has been updated by the removal of this event. If this is not the case, then child classes should override and
     * call the parent only when the abstraction has been updated.
     *
     * @param trigger
     * @param event
     */
    public void execute(RemoveEventTrigger trigger, Event event) {
        this.fireRemove(event);
    }

    public void registerAction(AbstractionRemoveAction action) {
        this.removeActions.add(action);
    }

    public void unregisterAction(AbstractionRemoveAction action) {
        this.removeActions.remove(action);
    }

    /**
     * Fire the trigger, calling all registered actions.
     */
    protected void fireRemove(Event event) {
        for (AbstractionRemoveAction action : removeActions) {
            logger.debug("Calling action " + action.toString());
            action.execute(this, event);
            logger.debug("Action completed");
        }
    }
}

