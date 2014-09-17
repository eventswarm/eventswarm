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
 * CannotRegisterAbstractionException.java
 *
 * Created on May 17, 2007, 3:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.abstractions;

/** Duplicate, non-shareable abstractions cannot be registered
 *
 * This exception is thrown by EventSet in situations where a non-shareable 
 * abstraction has been registered and a subsequent registration attempts to
 * register an equivalent abstraction (i.e. <code>abs1.equals(abs2)</code> 
 * returns true).
 *
 * This should only occur for poorly written abstractions.  If abstractions are 
 * not shareable, then <code>abs1.equals(abs2)</code> should always return false
 * unless <code>abs1 == abs2</code>.
 *
 * @author andyb
 */
public class DuplicateAbstractionException extends java.lang.Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new instance of <code>CannotRegisterAbstractionException</code> without detail message.
     */
    public DuplicateAbstractionException() {
        super("A duplicate, non-shareable abstraction cannot be registered");
    }
    
    
    /**
     * Constructs an instance of <code>CannotRegisterAbstractionException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DuplicateAbstractionException(String msg) {
        super(msg);
    }
}
