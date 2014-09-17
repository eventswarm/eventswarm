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

import java.util.Map;

/**
 * Abstraction that maintains a set of related calculations derived from a stream of events, for example
 * statistical mean, variance and standard deviation on a value captured in those events.
 *
 * This interface is implemented by abstractions that want to avoid maintaining separate NumericValueAbstractions
 * for each value it calculates. Implementers would typically offer specific methods for retrieving calculated values.
 *
 * Implementations must ensure that all calculations are updated before the AbstractionAddTrigger or
 * AbstractionRemoveTrigger fire. This distinguishes such classes from a ProcesssingTree, which allows listeners to
 * register against individual "Outlets" which implement their own Triggers.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface MutableCalculationSetAbstraction extends MutableAbstraction
{
    /**
     * Return a set of name/value pairs for a calculation set.
     *
     * Implementers can specialise the types of values returned if desired (e.g. all numeric).
     *
     * @return a map of name/value pairs for the calculations
     */
    public Map<String, ? extends Object> getValues();
}
