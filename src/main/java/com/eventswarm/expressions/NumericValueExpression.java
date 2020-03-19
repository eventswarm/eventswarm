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
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

/**
 * Expression implementing a numeric comparison between a value retrieved from a new event and a value held in an
 * abstraction or other predefined source of values.
 *
 * This expression works when at least one of the two values is a Double, Float, Long or Integer. It will not match
 * if both values are alternate implementations of Number.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class NumericValueExpression extends AbstractEventExpression {

    public static enum Comparator {
        EQUAL,
        GREATER,
        LESS,
        GREATEROREQUAL,
        LESSOREQUAL;

        @Override
        public String toString() {
            switch (this) {
                case EQUAL:
                case GREATEROREQUAL:
                case LESSOREQUAL:
                    return ("IS " + this.name() + " TO");
                default:
                    return ("IS " + this.name() + " THAN");
            }
        }
    };

    private Value<Number> value;
    private ValueRetriever<Number> retriever;
    private Comparator comparator;
    private static Logger log = Logger.getLogger(NumericValueExpression.class);

    /**
     * Create comparator expression with specified comparator, value and retriever.
     *
     * @param comparator
     * @param value
     * @param retriever
     */
    public NumericValueExpression(Comparator comparator, Value<Number> value, ValueRetriever<Number> retriever) {
        this.value = value;
        this.retriever = retriever;
        this.comparator = comparator;
    }

    /**
     * Constructor with explicit match limit
     *
     * @param limit
     * @param comparator
     * @param value
     * @param retriever
     */
    public NumericValueExpression(int limit, Comparator comparator, Value<Number> value, ValueRetriever<Number> retriever) {
        super(limit);
        this.value = value;
        this.retriever = retriever;
        this.comparator = comparator;
    }

    /**
     * Returns true if retriever.getValue(event) COMPARATOR value
     *
     * @param trigger
     * @param event
     * @return
     */
    @Override
    protected boolean matched(AddEventTrigger trigger, Event event) {
        return evaluate(retriever.getValue(event), value.getValue());
    }


    /**
     * Determine if this price expression is matched by the event when the
     * measure is cast to a Double.
     *
     * @param value
     * @param compareValue
     * @return
     */
    private boolean evaluate(Double value, Number compareValue) {
        //log.debug("Comparing Double value");
        switch (this.comparator) {
            case GREATEROREQUAL:
                return (value >= compareValue.doubleValue());
            case LESSOREQUAL:
                return (value <= compareValue.doubleValue());
            case EQUAL:
                return (value == compareValue.doubleValue());
            case LESS:
                return (value < compareValue.doubleValue());
            case GREATER:
                return (value > compareValue.doubleValue());
            default:
                return false;
        }
    }


    /**
     * Determine if this price expression is matched by the event when the
     * measure is cast to a Float.
     *
     * @param value
     * @param compareValue
     * @return
     */
    private boolean evaluate(Float value, Number compareValue) {
        //log.debug("Comparing Float value");
        switch (this.comparator) {
            case GREATEROREQUAL:
                return (value >= compareValue.floatValue());
            case LESSOREQUAL:
                return (value <= compareValue.floatValue());
            case EQUAL:
                return (value == compareValue.floatValue());
            case LESS:
                return (value < compareValue.floatValue());
            case GREATER:
                return (value > compareValue.floatValue());
            default:
                return false;
        }
    }

    /**
     * Determine if this price expression is matched by the event when the
     * measure is cast to a Long.
     *
     * @param value
     * @param compareValue
     * @return
     */
    private boolean evaluate(Long value, Number compareValue) {
        //log.debug("Comparing Long value");
        switch (this.comparator) {
            case GREATEROREQUAL:
                return (value >= compareValue.longValue());
            case LESSOREQUAL:
                return (value <= compareValue.longValue());
            case EQUAL:
                return (value == compareValue.longValue());
            case LESS:
                return (value < compareValue.longValue());
            case GREATER:
                return (value > compareValue.longValue());
            default:
                return false;
        }
    }


    /**
     * Determine if this price expression is matched by the event when the
     * measure is cast to an Integer.
     *
     * @param value
     * @param compareValue
     * @return
     */
    private boolean evaluate(Integer value, Number compareValue) {
        //log.debug("Comparing Integer value");
        switch (this.comparator) {
            case GREATEROREQUAL:
                return (value >= compareValue.intValue());
            case LESSOREQUAL:
                return (value <= compareValue.intValue());
            case EQUAL:
                return (value == compareValue.intValue());
            case LESS:
                return (value < compareValue.intValue());
            case GREATER:
                return (value > compareValue.intValue());
            default:
                return false;
        }
    }


    /**
     * If the value is not one of our known types, compare using the type of the event value
     *
     * @param value
     * @param compareValue
     * @return
     */
    private boolean evaluate(Number value, Number compareValue) {
        if (Double.class.isInstance(compareValue)) {
            return (this.evaluate(value.doubleValue(), compareValue));
        } else if (Long.class.isInstance(compareValue)) {
            return (this.evaluate(value.longValue(), compareValue));
        } else if (Float.class.isInstance(compareValue)) {
            return (this.evaluate(value.floatValue(), compareValue));
        } else if (Integer.class.isInstance(compareValue)) {
            return (this.evaluate(value.intValue(), compareValue));
        } else {
            log.warn("Attempt to evaluate a measure with an unsupported type");
            return false;
        }
    }
}
