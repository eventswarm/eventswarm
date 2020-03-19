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
import com.eventswarm.util.actions.QueuedAction;

/**
 * Interface implemented by a class that executes actions against target objects (e.g. EventSets), possibly in threads,
 * but ensuring that two actions for the same target are not executed concurrently.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface ActionExecutor {
    /**
     * Add an action, executing the action as soon as possible given the resources available
     * and ensuring that two actions for the same target are never executed concurrently.
     *
     * @param action
     */
    public void add(QueuedAction action);
}
