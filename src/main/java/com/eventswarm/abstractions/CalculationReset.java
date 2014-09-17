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
 * Interface implemented by trade calculation abstractions that allow the
 * calculation to be reset to a starting state.
 *
 * The methods are typically used for so-called Cumulative measures that don't
 * implement remove events and thus do not require a time window.
 *
 * @author andyb
 */
import java.util.Date;

public interface CalculationReset {

    /**
     * Reset the value of the calculation to a suitable initial value.
     */
    public void reset();

    /**
     * Reset the value of the calculation to a suitable initial value, and
     * ignore subsequent events with timestamps less than the supplied timestamp.
     *
     */
    public void reset(Date time);
    
}
