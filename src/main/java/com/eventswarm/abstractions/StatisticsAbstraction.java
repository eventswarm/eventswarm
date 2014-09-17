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
package com.eventswarm.abstractions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.expressions.Value;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of statistics (mean, variance, standard deviation) on a supplied event ValueRetriever
 *
 * This implementation uses Knuth's incremental algorithm for calculating mean, variance and standard deviation.
 * (see Donald E. Knuth (1998). The Art of Computer Programming, volume 2: Seminumerical Algorithms, 3rd edn.,
 * p. 232. Boston: Addison-Wesley.)
 *
 * The implementation provides a MutableCalculationSetAbstraction, although we anticipate that most actions receiving
 * update triggers will call explicit getters rather than the generic 'getValues' method.
 *
 * Note that the implementation does not track events added to or removed from abstraction: it relies on the upstream
 * event source to correctly remove events. Calling the remove trigger with an event that has not previously been
 * added will create inaccurate statistics.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class StatisticsAbstraction extends MutableAbstractionImpl implements MutableCalculationSetAbstraction {

    /**
     * Hold a reference to the value retriever method for events that will be delivered
     */
    private ValueRetriever<Number> retriever;
    private Map<String, Number> values;
    private static Logger logger = Logger.getLogger(StatisticsAbstraction.class);

    /**
     * Enumeration of keys whose names are used in the value map
     */
    public static String COUNT="Count",
                         MEAN = "Mean",
                         VARIANCE="Variance",
                         STANDARD_DEVIATION = "Standard Deviation";

    // Make these protected so we can use them in subclasses
    // getters/setters are a pain for this type of calculation
    protected int count;            // number of elements in population
    protected double mean;          // current mean
    protected double sumDSquared;  // sum of squares of deltas, almost: Knuth's "M2" variable (see Knuth's reference)
    protected double variance;      // variance
    protected double stdDev;        // standard deviation

    /**
     * No argument constructor provided for subclasses only, not for use with this implementation.
     */
    protected StatisticsAbstraction() {
        super();
    }

    /**
     * Maintain statistics for values extracted from events using the specified retriever.
     *
     * @param retriever
     */
    public StatisticsAbstraction(ValueRetriever<Number> retriever) {
        super();
        this.retriever = retriever;
        this.values = new HashMap<String, Number>();
        reset();
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // calculate incremental stats according to Knuth's method
        Number value = retriever.getValue(event);
        if (value != null) {
            Double newValue = value.doubleValue();
            if (newValue == 0.0) logger.warn("Adding zero value to statistical calculations");
            calcAdd(newValue);
            calcVariance();
            // tell listeners if we've updated
            fire(trigger, event);
        }
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        // calculate decremental stats according to Knuth's method
        Number value = retriever.getValue(event);
        if (value != null && count > 0) {
            Double oldValue = value.doubleValue();
            calcRemove(oldValue);
            if (count > 0) calcVariance();
            // tell listeners if we've updated
            fire(trigger, event);
        }
    }

    /**
     * Recalculate variance based on new count, mean and sum-of-delta's squared
     */
    protected void calcVariance() {
        if (count > 0) {
            variance = sumDSquared/(double)count;
            stdDev = Math.sqrt(variance);
        } else {
            variance = 0.0;
            stdDev = 0.0;
        }
    }

    /**
     * Add a new value to the statistical calculations
     *
     * Factored out of the AddEventAction so it can be used repeatedly in a weighted statistics subclass.
     *
     * @param newValue value to be added
     */
    protected void calcAdd(Double newValue) {
        count++;
        double delta = newValue - mean;
        mean = mean + delta /(double)count;
        sumDSquared = sumDSquared + delta * (newValue - mean);
    }

    /**
     * Remove an existing value from the statistical calculations
     *
     * Factored out of the AddEventAction so it can be used repeatedly in a weighted statistics subclass.
     *
     * @param oldValue value to be removed
     */
    protected void calcRemove(Double oldValue) {
        count--;
        if (count > 0) {
            double delta = oldValue - mean;
            mean = mean - delta/(double)count;
            sumDSquared = sumDSquared - delta * (oldValue - mean);
        } else {
            reset();
        }
    }

    /**
     * Return the statistical calculations, keyed by the name enumeration
     *
     * @return
     */
    @Override
    public Map<String, Number> getValues() {
        values.put(COUNT, getCount());
        values.put(MEAN, getMean());
        values.put(VARIANCE, getVariance());
        values.put(STANDARD_DEVIATION, getStdDev());
        return values;
    }

    /**
     * @return number of elements in the data set
     */
    public int getCount() {
        return count;
    }

    public Value<Number> getCountValue() {
        return new Value<Number>() {
            public Number getValue() { return getCount();}
        };
    }

    /**
     * @return mean of the current data set
     */
    public double getMean() {
        return mean;
    }

    public Value<Number> getMeanValue() {
        return new Value<Number>() {
            public Number getValue() { return getMean();}
        };
    }

    /**
     * @return variance of the current data set
     */
    public double getVariance() {
        return variance;
    }

    public Value<Number> getVarianceValue() {
        return new Value<Number>() {
            public Number getValue() { return getVariance();}
        };
    }

    /**
     * @return standard deviation of the current data set
     */
    public double getStdDev() {
        return stdDev;
    }

    public Value<Number> getStdDevValue() {
        return new Value<Number>() {
            public Number getValue() { return getStdDev();}
        };
    }

    /**
     * Reset the statistical calculations
     */
    public void reset() {
        this.stdDev = 0.0;
        this.count = 0;
        this.mean = 0.0;
        this.sumDSquared = 0.0;
        this.variance = 0.0;
    }

    /**
     * Makes this abstraction available for recycling, synonym for reset in this case.
     */
    public void clear() {
        reset();
    }

    /**
     * Make sure any listeners know that we've updated due to an add
     *
     * Implemented by calling the parent add action
     *
     * @param event
     */
    protected void fire(AddEventTrigger trigger, Event event) {
        super.execute(trigger, event);
    }

    /**
     * Make sure any listeners know that we've updated due to a remove
     *
     * Implemented by calling the parent remove action
     *
     * @param event
     */
    protected void fire(RemoveEventTrigger trigger, Event event) {
        super.execute(trigger, event);
    }
}
