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

package com.eventswarm.schedules;

/**
 * Enumeration of the ordering relationships between events, allowing for
 * concurrent, indistinguishable and indeterminate orderings.
 *
 * This enumeration is defined to express the partial ordering of events
 * across different sources.  In general, events from different sources are not
 * comparable unless vector clocks, clock synchronisation or a monotonic
 * distributed clock is used.
 *
 * @author andyb
 */
public enum Order {
    BEFORE, // Should only be used for directly comparable event timestamps
    AFTER,  // Should only be used for directly comparable event timestamps
    CONCURRENT, // For use when causal relationships are being maintained and
                // there is no known causal relationship between events.
    INDISTINGUISHABLE, // For use when precision is inadequate to distinguish
                       // event timestamps
    INDETERMINATE; // Should be used for distinct sources of time
}
