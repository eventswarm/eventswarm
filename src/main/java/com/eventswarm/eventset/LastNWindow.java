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

package com.eventswarm.eventset;

import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;

/**
 * Window that includes the last N events in time order.
 *
 * Note that as with time windows, the class does not allow upstream
 * abstractions to remove events from the window. This implementation is a
 * simple override of the RemoveEventAction so that it ignores removes.

 * @author andyb
 */
public class LastNWindow extends AtMostNWindow {

    /**
     * Create a window that holds the last <code>windowSize</code> events.
     *
     * @param windowSize
     */
    public LastNWindow(int windowSize) {
        super(windowSize);
     }

    /**
     * Override parent to ignore event removal triggers.
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        // ignore
    }

}
