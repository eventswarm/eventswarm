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
package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.abstractions.StatisticsAbstraction;
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

/**
 * Expression class that compares a value retrieved from an event with statistical data calculated from preceding
 * values.
 *
 * At present, this class is limited to comparing the number of standard deviations from the mean to the value with
 * a constant multiple (double). It uses the StatisticsAbstraction to calculate statistics, and compares the value
 * extracted from an event with the mean and standard deviation <strong>before</strong> adding the new event value to
 * the statistics.
 *
 * To avoid spurious matches that occur before the statistical calculations become stable, the class requires a
 * minimum number of data points and will never match if the minimum is not reached. A default MINIMUM_COUNT is
 * defined but can be overridden.
 *
 * Note that if a long series of equal values has occurred, it is possible that the statistics abstraction will have a
 * standard deviation of zero, meaning any differing value will cause a match. To avoid this behaviour, the minimum
 * number of points should be set large enough to avoid such situations.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class StatisticalThresholdExpression extends AbstractEventExpression implements EventExpression {

    public static int MINIMUM_COUNT = 10;

    private StatisticsAbstraction stats;
    private ValueRetriever<Number> retriever;
    private int minCount;
    private double multiple;

    private static Logger logger = Logger.getLogger(StatisticalThresholdExpression.class);

    /**
     * Create a statistics expression that calculates statistics on values retrieved by the retriever, firing when
     * an event has a value that is more than <code>multiple</code> standard deviations from the current mean.
     *
     * The default minimum number of data points are required before the trigger will fire.
     *
     * @param retriever
     */
    public StatisticalThresholdExpression(ValueRetriever<Number> retriever, double multiple) {
        super();
        this.retriever = retriever;
        this.multiple = multiple;
        this.stats = new StatisticsAbstraction(retriever);
        this.minCount = MINIMUM_COUNT;
    }

    /**
     * Create a statistics expression as above, but with the specified minimum number of data points required before
     * the trigger will fire.
     *
     * @param retriever
     */
    public StatisticalThresholdExpression(ValueRetriever<Number> retriever, double multiple, int minCount) {
        super();
        this.retriever = retriever;
        this.multiple = multiple;
        this.stats = new StatisticsAbstraction(retriever);
        this.minCount = minCount;
    }

    /**
     * Create a statistics expression as above, but with the specified limit on the number of matches held.
     *
     * The default limit is specified in the parent AbstractEventExpression class.
     *
     * @see AbstractEventExpression
     *
     * @param retriever
     * @param minCount
     * @param multiple
     * @param limit
     */
    public StatisticalThresholdExpression(ValueRetriever<Number> retriever, double multiple, int minCount, int limit) {
        super(limit);
        this.retriever = retriever;
        this.minCount = minCount;
        this.multiple = multiple;
    }

    /**
     * Override the parent method so we can add the event to our stats after the match comparison has occurred.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        super.execute(trigger, event);
        stats.execute(trigger, event);
    }

    /**
     * Override the parent method so we can remove the event from our stats
     *
     * The order should not be important, since our stats are not accessible to anyone outside this class.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        stats.execute(trigger, event);
        super.execute(trigger, event);
    }

    /**
     * Return true if the value retrieved from the event is greater than <code>multiple</code> standard deviations from
     * the mean AND the minimum number of data points is present
     *
     * @param trigger
     * @param event
     * @return
     */
    @Override
    protected boolean matched(AddEventTrigger trigger, Event event) {
        if (stats.getCount() >= minCount) {
            double value = retriever.getValue(event).doubleValue();
            double delta = Math.abs(stats.getMean() - value);
            logger.debug("Value: " + Double.toString(value) + ", Mean: " + Double.toString(stats.getMean()) +
                    ", Multiple: " + Double.toString(multiple) + ", Standard deviation: " + Double.toString(stats.getStdDev()));
            return (delta/stats.getStdDev() > multiple);
        } else {
            return false;
        }
    }

    public int getMinCount() {
        return minCount;
    }

    public double getMultiple() {
        return multiple;
    }

    public ValueRetriever<Number> getRetriever() {
        return retriever;
    }

    public StatisticsAbstraction getStats() {
        return stats;
    }
}
