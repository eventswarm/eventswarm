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

package com.eventswarm.powerset;

/**
 * Interface implemented by all Powerset implementations.
 *
 * Mathematically, a powerset is a set of subsets. In EventSwarm, we use this
 * concept to split a stream of events into subordinate streams according to
 * some rule, and allow processing applications/configurations to be registered
 * against each subordinate stream.  For example, you might split a stock market
 * feed into subordinate streams based on the stock code, allowing applications
 * to monitor each stock for certain patterns of price movement or trading.
 *
 * Powersets may also push a single event into multiple subsets. This is less
 * usual than having disjoint sets, but is not prohibited.  
 *
 * Powersets also provide a basis for distribution of processing.  You can
 * distribute processing to multiple nodes by creating a powerset that implements
 * reflects distribution function.  For example, if you wanted to distribute
 * billable events based on customer identifier, you could use the customer ID
 * to split streams and register to receive events from particular streams on
 * remote nodes.
 *
 * Since each EventSet must be identifiable, Powerset implementations must
 * implement a Map interface that associates a key with each EventSet.
 * It is anticipated that most implementations will extend one of the java.util
 * implementations of the Map interface.
 *
 * Powersets are parameterised for the Map key type, allowing the creator of a
 * Powerset to constrain the types of values used as keys.
 *
 * @author andyb
 */
import com.eventswarm.schedules.TickTrigger;
import com.eventswarm.*;
import com.eventswarm.eventset.EventSet;
import java.util.Map;

public interface Powerset<Keytype>
        extends AddEventAction, RemoveEventAction, PowersetAddEventTrigger, PowersetRemoveEventTrigger,
                NewSetTrigger<Keytype>, RemoveSetTrigger<Keytype>, Map <Keytype,EventSet>, TickTrigger
{
    /**
     * Return the EventSetFactory for the powerset.
     *
     * @return
     */
    public EventSetFactory<Keytype> getFactory ();

    /**
     * Set the EventSetFactor for the powerset.
     * @param factory
     */
    public void setFactory (EventSetFactory<Keytype> factory);


}
