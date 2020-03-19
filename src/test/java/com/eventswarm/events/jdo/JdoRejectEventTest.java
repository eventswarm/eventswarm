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
 * JdoRejectEventTest.java
 * JUnit based test
 *
 * Created on 24 May 2007, 16:00
 */

package com.eventswarm.events.jdo;

import junit.framework.*;

/**
 *
 * @author vji
 */
public class JdoRejectEventTest extends TestCase {
    
    public JdoRejectEventTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getReason method, of class com.eventswarm.events.jdo.JdoRejectEvent.
     */
    public void testGetReason() {
        System.out.println("getReason");
        
        JdoRejectEvent instance = TestEvents.jdoReject;
        
        String expResult = TestEvents.rejectReason;
        String result = instance.getReason();
        assertEquals(expResult, result);
    }

    /**
     * Test of setReason method, of class com.eventswarm.events.jdo.JdoRejectEvent.
     */
    public void testSetReason() {
        System.out.println("setReason");
        
        String reason = TestEvents.rejectReason;
        JdoRejectEvent instance = new JdoRejectEvent();
        
        instance.setReason(reason);
        assertEquals(reason, instance.getReason());
    }    
}
