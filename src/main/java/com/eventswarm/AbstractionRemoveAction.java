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

import com.eventswarm.events.Event;

/**
 * Action to be called by a triggering object when a AbstractionRemoveTrigger is fired.
 *
 * @see AbstractionRemoveTrigger
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface AbstractionRemoveAction {

    public static Class<?> trigger = AbstractionRemoveTrigger.class;

    public void execute(AbstractionRemoveTrigger trigger, Event event);

}
