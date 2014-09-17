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

import com.eventswarm.events.Event;
import com.eventswarm.expressions.Value;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SumAbstraction extends MutableCalculationAbstractionImpl implements Value<Number> {
    private ValueRetriever<Number> retriever;
    private static Logger logger = Logger.getLogger(SumAbstraction.class);

    /**
     * Constructor initialises sum with 0.0 (Double)
     *
     * @param retriever
     */
    public SumAbstraction(ValueRetriever<Number> retriever) {
        super();
        this.value = new Double(0.0);
        this.retriever = retriever;
    }

    @Override
    protected void calculateRemove(Event event) {
        logger.debug("Removing " + this.retriever.getValue(event).toString() + " from " + this.value.toString());
        this.value = this.value.doubleValue() - this.retriever.getValue(event).doubleValue();
    }

    @Override
    protected void calculate(Event event) {
        logger.debug("Adding " + this.retriever.getValue(event).toString() + " to " + this.value.toString());
        this.value = this.value.doubleValue() + this.retriever.getValue(event).doubleValue();
    }

    @Override
    public void reset() {
        this.value = new Double(0.0);
    }
}
