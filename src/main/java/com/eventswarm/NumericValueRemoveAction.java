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
package com.eventswarm;

/**
 * Action called in response to a NumericValueRemoveTrigger firing, called when  
 * a computed abstraction updates a numeric value due to the removal of 
 * an event. 
 * 
 * The action includes the event that caused the trigger to fire and the new
 * computed numeric value.  
 *
 * @author andyb
 */
import com.eventswarm.events.Event;

public interface NumericValueRemoveAction extends Action {

    public static Class trigger = NumericValueRemoveTrigger.class;

    public void execute(NumericValueRemoveTrigger trigger, Event event, Number number);

}
