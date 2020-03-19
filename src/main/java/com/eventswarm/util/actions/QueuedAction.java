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
package com.eventswarm.util.actions;

/**
 * Interface for classes that hold an action and all of the parameters required to execute the action, then execute
 * the action when called.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface QueuedAction extends Runnable {
    /**
     * Identify the target of the action, that is, the eventset or other object that will be acted on.
     *
     * This method is intended for use in parallelisation, that is, different targets can have their actions executed
     * concurrently. For a PowersetExpression, for example, the target should be the subset (eventset) that the
     * expression will be evaluated against rather than the PowersetAddAction that will be called to initiate the
     * evaluation.
     *
     * @return
     */
    public Object getTarget();
}
