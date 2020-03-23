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
 * JdoSourceTest.java
 * JUnit based test
 *
 * Created on 24 May 2007, 16:00
 */

package com.eventswarm.events.jdo;

import junit.framework.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 *
 * @author vji
 */
public class JdoSourceTest extends TestCase {
    
    public JdoSourceTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getSourceId method, of class com.eventswarm.events.jdo.JdoSource.
     */
    public void testGetSourceId() {
        System.out.println("getSourceId");
        
        JdoSource instance = TestEvents.jdoSrc;
        
        String expResult = TestEvents.srcStr;
        String result = instance.getSourceId();
        assertEquals(expResult, result);
    }

    /**
     * Test of getId method, of class com.eventswarm.events.jdo.JdoSource.
     */
    public void testGetId() {
        System.out.println("getId");
        
        JdoSource instance = TestEvents.jdoSrc;
        
        String expResult = TestEvents.srcStr;
        String result = instance.getId();
        assertEquals(expResult, result);
    }

    /**
     * Test of setId method, of class com.eventswarm.events.jdo.JdoSource.
     */
    public void testSetId() {
        System.out.println("setId");
        
        String id = TestEvents.srcStr;
        JdoSource instance = new JdoSource();
        
        instance.setId(id);
        assertEquals(id, instance.getId());
    }

    /**
     * Test of setSource method, of class com.eventswarm.events.jdo.JdoSource.
     */
    public void testSetSourceId() {
        System.out.println("setSource");
        
        String source = TestEvents.srcStr;
        JdoSource instance = new JdoSource();
        
        instance.setSourceId(source);
        assertEquals(source, instance.getSourceId());
    }

    /**
     * Test of equals method, of class com.eventswarm.events.jdo.JdoSource.
     */
    public void testEquals() {
        System.out.println("equals");
        
        Object object = TestEvents.hdrSrc;
        JdoSource instance = TestEvents.jdoSrc;
        
        boolean expResult = true;
        boolean result = instance.equals(object);
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class com.eventswarm.events.jdo.JdoSource.
     */
    public void testHashCode() {
        System.out.println("hashCode");
        
        JdoSource instance = TestEvents.jdoSrc;
        
        int expResult = TestEvents.srcStr.hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    public void testLocalHeader() throws Exception {
        JdoSource instance = JdoSource.getLocalSource();
        System.out.println("Local source is: " + instance.getId());
        try {
            assertEquals(InetAddress.getLocalHost().getHostAddress(), instance.getId());
        } catch (UnknownHostException exc) {
            assertTrue(UUID.class.isInstance(UUID.fromString(instance.getId())));
        }
    }
}
