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
 * Class providing implementation of standard deviation based on Knuth's algorithm.
 * It supports both mutable and resettable handling of events to be removed/reset
 * 
 * Copyright 2010 Ensift Pty Ltd
 * 
 * @author zoranm
 */
import org.apache.log4j.*;
import com.eventswarm.events.Event;
import com.eventswarm.events.NumericValue;

public class StandardDeviation
        extends MutableCalculationAbstractionImpl
        implements MutableCalculationAbstraction {
    /* private logger for log4j */

    private static Logger log = Logger.getLogger(StandardDeviation.class);
    protected String partName;
    /**
     * We apply Knuth's algorithm for calculating event-based variance
     * (see Donald E. Knuth (1998). The Art of Computer Programming, volume 2:
     * Seminumerical Algorithms, 3rd edn., p. 232. Boston: Addison-Wesley.)
     * The parameters below are variables used fo calculation
     */
    protected int n;                 // count of where we are in the population
    protected double mean;           // current mean
    protected double m2;             // curent M2 variable (see Knuth's reference)
    protected double currentVariance;

    public StandardDeviation(String partName) {
        this.partName = partName;
        this.reset();
    }

    @Override
    protected void calculate(Event event) {

        // calculate incrementaly variance according to Knuth's method
        try {
            Double newValue = ((NumericValue) event.getPart(this.partName)).getValue().doubleValue();
            if (newValue != null) {
                n++;
                double delta = newValue - mean;
                mean = mean + delta / n;
                m2 = m2 + delta * (newValue - mean);
                currentVariance = m2 / n;
                this.value = Math.sqrt(currentVariance);
            }

        } catch (ClassCastException exc) {
            log.info("Attempt to calulate standard deviation on non-numeric value");
        }
    }

    @Override
    protected void calculateRemove(Event event) {

       // calculate decremental variance according to Knuth's method
        try {
            Double newValue = ((NumericValue) event.getPart(this.partName)).getValue().doubleValue();
            if (newValue != null && n > 1 ) {
      
                double delta = newValue - mean;
                mean = mean - delta / (n-1);
                m2 = m2 - delta * (newValue - mean);
                currentVariance = m2 / (n-1);
                this.value = Math.sqrt(currentVariance);
                n--;
            }

        } catch (ClassCastException exc) {
            log.info("Attempt to calulate standard deviation on non-numeric value", exc);
        } catch (NullPointerException exc) {
            log.warn("Attempt to process null event", exc);
        }
    }

    @Override
    public void reset() {
        this.value = 0.0;
        this.n = 0;
        this.mean = 0.0;
        this.m2 = 0.0;      //
        this.currentVariance = 0.0;
    }
}
