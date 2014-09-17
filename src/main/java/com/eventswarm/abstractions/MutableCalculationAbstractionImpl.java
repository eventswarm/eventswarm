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
 * Abstract class providing a set of base methods for implementing numeric 
 * calculation abstractions that need to include removal of events in their
 * calculations.
 *
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import org.apache.log4j.*;
import com.eventswarm.*;
import com.eventswarm.events.Event;
import java.util.Set;
import java.util.HashSet;

public abstract class MutableCalculationAbstractionImpl
        extends CalculationAbstractionImpl
        implements MutableCalculationAbstraction
{
    /** action sets for listeners */
    protected Set<ValueRemoveAction> valueRemoveActions = 
            new HashSet<ValueRemoveAction>();
    protected Set<NumericValueRemoveAction> numValueRemoveActions = 
            new HashSet<NumericValueRemoveAction>();
    protected Set<AbstractionRemoveAction> absRemoveActions =
            new HashSet<AbstractionRemoveAction>();

   
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(MutableCalculationAbstractionImpl.class);


    /**
     * Call the calculation remove method with the identified event, provided
     * that event has a timestamp >= the last reset date.
     * 
     * @param trigger
     * @param event
     */
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (this.lastReset.compareTo(event.getHeader().getTimestamp()) <= 0) {
            Number prev = this.value;
            this.calculateRemove(event);
            this.fireRemove(event, (prev != this.value));
        }
    }

    /**
     * Subclasses must provide a method to recalculate when remove is called.
     * 
     * @param event
     */
    protected abstract void calculateRemove(Event event);
    
    public void registerAction(ValueRemoveAction action) {
        this.valueRemoveActions.add(action);
    }

    public void unregisterAction(ValueRemoveAction action) {
        this.valueRemoveActions.remove(action);
    }

    public void registerAction(NumericValueRemoveAction action) {
        this.numValueRemoveActions.add(action);
    }

    public void unregisterAction(NumericValueRemoveAction action) {
        this.numValueRemoveActions.remove(action);
    }

    public void registerAction(AbstractionRemoveAction action) {
        this.absRemoveActions.add(action);
    }

    public void unregisterAction(AbstractionRemoveAction action) {
        this.absRemoveActions.remove(action);
    }


    protected void fireRemove(Event event, boolean changed) {
        // notify all of the ValueRemoveTrigger listeners
        for (ValueRemoveAction action : this.valueRemoveActions) {
            action.execute(this, event);
        }
        // notify NumericValueRemoveTrigger listeners
        for (NumericValueRemoveAction action : this.numValueRemoveActions) {
            action.execute(this, event, this.value);
        }
        // notify AbstractionRemoveTrigger listeners
        if (changed) {
            for (AbstractionRemoveAction action : this.absRemoveActions) {
                action.execute(this, event);
            }
        }
    }
}
