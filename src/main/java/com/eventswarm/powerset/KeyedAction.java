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
package com.eventswarm.powerset;

import com.eventswarm.AddEventAction;
import com.eventswarm.MutableTarget;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;

/**
 * Simple helper class that registers add and remove actions against a particular key in a powerset, which can be
 * used, for example, to register different expressions for different powerset keys.
 *
 * This class either registers the actions immediately if the keyed subset exists or registers to receive
 * NewSetTrigger actions if the subset does not exist, with the NewSetAction doing the registration when the
 * key is matched.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class KeyedAction<Keytype> implements NewSetAction<Keytype> {

    private MutableTarget action;
    private Powerset<Keytype> pset;
    private Keytype key;

    private static Logger logger = Logger.getLogger(KeyedAction.class);

    /**
     *
     */

    public KeyedAction(MutableTarget action, Powerset<Keytype> pset, Keytype key) {
        this.action = action;
        this.pset = pset;
        this.key = key;
        if (pset.containsKey(key)) {
            // subset already exists, register the action
            register(this.pset.get(key));
        } else {
            // subset does not exist, so wait for it to be created
            this.pset.registerAction(this);
        }
    }

    /**
     * Wait for the powerset to add a subset for the specified key then add the actions to the powerset.
     *
     * @param trigger Identifies the source of the trigger
     * @param es The EventSet that has been created
     * @param key The key associated with the EventSet
     */
    public void execute(NewSetTrigger<Keytype> trigger, EventSet es, Keytype key) {
        if (key.equals(this.key)) {
            logger.debug("Have match for key " + key.toString() + ", registering my action");
            register(es);
            //this.pset.unregisterAction(this);
        }
    }

    private void register(EventSet es) {
        es.registerAction((AddEventAction) this.action);
        es.registerAction((RemoveEventAction) this.action);
    }
}
