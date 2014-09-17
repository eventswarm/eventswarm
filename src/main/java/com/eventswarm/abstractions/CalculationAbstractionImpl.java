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
 * calculation abstractions.
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
import java.util.Date;
import com.eventswarm.schedules.Clock;

public abstract class CalculationAbstractionImpl
        extends IncrementalAbstractionImpl 
        implements CalculationAbstraction
{
    /** action sets for listeners */
    protected Set<ValueAddAction> valueActions = new HashSet<ValueAddAction>();
    protected Set<NumericValueAddAction> numValueActions = 
            new HashSet<NumericValueAddAction>();
    
    /** Current value of calculation */
    protected Number value = null;

    /** last reset date is the EPOCH date (1 Jan 1970) */
    protected Date lastReset = Clock.EPOCH;
    
    /* private logger for log4j */
    private static Logger log = Logger.getLogger(CalculationAbstractionImpl.class);

    /**
     * 
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        if (this.lastReset.compareTo(event.getHeader().getTimestamp()) <= 0) {
            Number prev = value;
            this.calculate(event);
            this.fire(event);
            if (value != prev) {
                // if the calculated value has changed, fire the AbstractionAddTrigger
                super.execute(trigger, event);
            }
        }
    }

    /**
     * Method to calculate the new value after receiving an event.
     * 
     * Subclasses of this method should update the parent value attribute when 
     * the calculation is complete.
     * 
     * @param event
     */
    protected abstract void calculate (Event event);

    /**
     * Return the current value associated with the calculation.
     *
     * @return
     */
    public Number getValue () {
        return this.value;
    }

    /**
     * Subclasses must implement a method to reset the value of the calculation
     * to the initial value.
     */
    public abstract void reset();

    /**
     * For calculation abstractions, clear is equivalent to reset (i.e. prepares it for recycling)
     */
    public void clear() {
        reset();
    }

    /**
     * Default implementation of reset with a time parameter sets the last
     * reset time and calls the reset() method of the class.
     *
     * Null time values are ignored but the reset is still executed.
     *
     * @param time
     */
    public void reset(Date time) {
        if (time != null) {
            this.lastReset = time;
        }
        this.reset();
    }


    public void registerAction(ValueAddAction action) {
        this.valueActions.add(action);
    }

    public void unregisterAction(ValueAddAction action) {
        this.valueActions.remove(action);
    }

    public void registerAction(NumericValueAddAction action) {
        this.numValueActions.add(action);
    }

    public void unregisterAction(NumericValueAddAction action) {
        this.numValueActions.remove(action);
    }

    protected void fire(Event event) {
        // notify all of the ValueAddTrigger listeners
        for (ValueAddAction action : this.valueActions) {
            action.execute(this, event);
        }
        // notify NumericValueRemoveTrigger listeners
        for (NumericValueAddAction action : this.numValueActions) {
            action.execute(this, event, this.value);
        }
    }
}
