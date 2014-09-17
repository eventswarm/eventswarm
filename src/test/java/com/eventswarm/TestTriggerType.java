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

package com.eventswarm;

/**
 *
 * Copyright 2008 Ensift Pty Ltd
 * 
 * @author andyb
 */

import org.apache.log4j.*;

public class TestTriggerType extends Object implements AddEventTrigger {

    /* private logger for log4j */
    private static Logger log = Logger.getLogger(TestTriggerType.class);

    public void registerAction(AddEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterAction(AddEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public static void main (String argv[]) {
        Trigger trig = new TestTriggerType();

        System.out.println("Action class is " + trig.action.toString());
    }
}
