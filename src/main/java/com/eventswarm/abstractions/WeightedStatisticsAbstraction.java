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
import org.apache.log4j.Logger;

/**
 * Extension of StatisticsAbstraction to apply an integer weight to each value in the data set
 *
 * For example, a volume weighted average price of a share calculates the average price by multiplying the trade price
 * by the number of shares purchased in the trade when updating the statistics.
 *
 * Note that this is limited to an integer weight "N", since the weight is applied by effectively adding or removing
 * "N" instances of the provided data point. This ensures that we're not breaking Knuth's algorithm. For large weights,
 * the additional processing time might become and issue. A non-integer weight would most probably require a different
 * algorithm.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class WeightedStatisticsAbstraction extends StatisticsAbstraction {

    private ValueRetriever<Number> valueRetriever;
    private ValueRetriever<Integer> weightRetriever;

    private static Logger logger = Logger.getLogger(WeightedStatisticsAbstraction.class);

    /**
     * Provide access to parent constructor for any subclasses, although I can't think how they could do anything useful
     */
    protected WeightedStatisticsAbstraction() {
        super();
    }

    /**
     * Hide parent constructor: would screw us up
     *
     * @param retriever
     */
    private WeightedStatisticsAbstraction(ValueRetriever<Number> retriever) {
    }

    public WeightedStatisticsAbstraction(ValueRetriever<Number> valueRetriever, ValueRetriever<Integer> weightRetriever) {
        this.valueRetriever = valueRetriever;
        this.weightRetriever = weightRetriever;
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // calculate incremental stats according to Knuth's method with weighting
        Double newValue = valueRetriever.getValue(event).doubleValue();
        Integer weight = weightRetriever.getValue(event).intValue();
        if (newValue != null && weight != null) {
            // add the new value "weight" times: Knuth's algorithm won't work if we just multiply
            for (int i = 0; i < weight; i++) {
                calcAdd(newValue);
            }
            calcVariance();
            // let the parent class tell listeners that we've updated
            super.fire(trigger, event);
        }
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        // calculate decremental stats according to Knuth's method
        Double newValue = valueRetriever.getValue(event).doubleValue();
        Integer weight = weightRetriever.getValue(event).intValue();
        if (newValue != null && count > 0) {
            // remove the old value "weight" times: Knuth's algorithm won't work if we just multiply
            for (int i = 0; i < weight && count > 0; i++) {
                calcRemove(newValue);
            }
            if (count > 0) calcVariance();
            // let the parent class tell listeners that we've updated
            super.fire(trigger, event);
        }
    }

}
