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
 * This action is called by an upstream clock or other time source to intitialise
 * the time in processing components that depend upon it.
 *
 * Note that time for event processing purposes is not always based on a real
 * time clock.  For example, EventClock gets its time from event timestamps.
 *
 * @author andyb
 */
import com.eventswarm.Action;
import java.util.Date;

public interface FirstTickAction extends Action {

    public static Class<?> trigger = FirstTickTrigger.class;
    
    public void execute(FirstTickTrigger trigger, Date time);

}
