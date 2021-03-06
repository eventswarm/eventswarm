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
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.MutablePassThru;
import java.util.*;
import org.apache.log4j.*;

/**
 * Implementation of the PassThru methods which just pass on events to any
 * registered actions.  Useful as a default inlet for processing trees.
 *
 * @author andyb
 */
public class MutablePassThruImpl
        extends PassThruImpl
        implements MutablePassThru
{

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(MutablePassThruImpl.class);

    /* set of downstream actions */
    private Set<RemoveEventAction> removeActions = new HashSet<RemoveEventAction>();

    /** Creates a new instance of PassThruImpl */
    public MutablePassThruImpl() {
        super();
    }

    public void execute(RemoveEventTrigger trigger, Event event) {
        for (RemoveEventAction action : this.removeActions) {
            action.execute(trigger, event);
        }
    }

    public void registerAction(RemoveEventAction action) {
        this.removeActions.add(action);
    }

    public void unregisterAction(RemoveEventAction action) {
        this.removeActions.remove(action);
    }

    public void reset() {
        this.removeActions.clear();
    }

    public void clear() {
        // do nothing, since we have no state
    }
}
