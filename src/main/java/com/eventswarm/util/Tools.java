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
 * Tools.java
 *
 * Created on 28 May 2007, 12:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.util;

/**
 *
 * @author vji
 */
public class Tools {
    
    /** Creates a new instance of Tools */
    private Tools() {
    }
    
    public static boolean equals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null)
            return true;
        else if (obj1 == null && obj2 != null)
            return false;
        else if (obj1 != null && obj2 == null)
            return false;
        else
            return obj1.equals(obj2);
    }        
}
