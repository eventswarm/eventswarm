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
 * IncrementalAbstractionImpl.java
 *
 * Created on 2 May 2007, 14:02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.eventset;
import com.eventswarm.abstractions.IncrementalAbstractionImpl;
import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.PassThru;
import java.util.*;
import org.apache.log4j.*;

/**
 * Implementation of the PassThru methods which just pass on events to any
 * registered actions.  Useful as a default inlet for processing trees.
 *
 * @author andyb
 */
public class PassThruImpl
        extends IncrementalAbstractionImpl
        implements PassThru
{
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(PassThruImpl.class);

    /* set of downstream actions */
    private Set<AddEventAction> addActions = new HashSet<AddEventAction>();

    /** Creates a new instance of PassThruImpl */
    public PassThruImpl() {
        super();
    }

    /**
     * Method for passing new events through to registered actions.
     * 
     * @param trigger Upstream trigger producing the event
     * @param event Event to be passed through
     */
    public void execute(AddEventTrigger trigger, Event event) {
        for (AddEventAction action : this.addActions) {
            action.execute(trigger, event);
        }
        // This class does not directly update any abstractions but subclasses might, so call the super implementation
        // to fire the AbstractionAddTrigger actions
        super.execute(trigger, event);
    }

    /**
     * Register action to receive events from the PassThru
     *
     * @param action
     */
    public void registerAction(AddEventAction action) {
        this.addActions.add(action);
    }

    /**
     * Unregister action from the PassThru
     *
     * @param action
     */
    public void unregisterAction(AddEventAction action) {
        this.addActions.remove(action);
    }

    public void reset() {
        super.reset();
        this.addActions.clear();
    }

    public void clear() {
        // do nothing, since we have no state
    }

}
