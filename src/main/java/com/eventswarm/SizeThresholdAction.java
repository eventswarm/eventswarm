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
 * Action called in response to a SizeThresholdTrigger firing, called when an
 * upstream computed abstraction updates an EventSet and the addition of
 * an event causes a pre-defined threshold to be reached.
 * 
 * The action includes the event that caused the trigger to fire and the size that was reached.
 *
 * @author andyb
 */
import com.eventswarm.events.Event;

public interface SizeThresholdAction extends Action {

    public static Class<?> trigger = SizeThresholdTrigger.class;

    public void execute(SizeThresholdTrigger trigger, Event event, long size);

}
