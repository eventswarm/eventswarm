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
 * JdoEventURLTest.java
 * JUnit based test
 *
 * Created on 24 May 2007, 16:00
 */

package com.eventswarm.events.jdo;

import junit.framework.*;
import java.net.URL;

/**
 *
 * @author vji
 */
public class JdoEventURLTest extends TestCase {
    
    public JdoEventURLTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getURL method, of class com.eventswarm.events.jdo.JdoEventURL.
     */
    public void testGetURL() {
        System.out.println("getURL");
        
        JdoEventURL instance = TestEvents.jdoEvUrl1;
        
        URL expResult = TestEvents.url1;
        URL result = instance.getURL();
        assertEquals(expResult, result);
    }

    /**
     * Test of getURLString method, of class com.eventswarm.events.jdo.JdoEventURL.
     */
    public void testGetURLString() {
        System.out.println("getURLString");
        
        JdoEventURL instance = TestEvents.jdoEvUrl1;
        
        String expResult = TestEvents.urlStr1;
        String result = instance.getURLString();
        assertEquals(expResult, result);
    }

    /**
     * Test of setURLString method, of class com.eventswarm.events.jdo.JdoEventURL.
     */
    public void testSetURLString() {
        System.out.println("setURLString");
        
        String urlString = TestEvents.urlStr1;
        JdoEventURL instance = new JdoEventURL();
        
        instance.setURLString(urlString);
        assertEquals(urlString, instance.getURLString());
    }

    /**
     * Test of getLinkText method, of class com.eventswarm.events.jdo.JdoEventURL.
     */
    public void testGetLinkText() {
        System.out.println("getLinkText");
        
        JdoEventURL instance = TestEvents.jdoEvUrl2;
        
        String expResult = TestEvents.urlLink2;
        String result = instance.getLinkText();
        assertEquals(expResult, result);
    }

    /**
     * Test of setURL method, of class com.eventswarm.events.jdo.JdoEventURL.
     */
    public void testSetURL() {
        System.out.println("setURL");
        
        URL url = TestEvents.url1;
        JdoEventURL instance = new JdoEventURL();
        
        instance.setURL(url);
        assertEquals(url, instance.getURL());
    }

    /**
     * Test of setLinkText method, of class com.eventswarm.events.jdo.JdoEventURL.
     */
    public void testSetLinkText() {
        System.out.println("setLinkText");
        
        String linkText = TestEvents.urlLink2;
        JdoEventURL instance = new JdoEventURL();
        
        instance.setLinkText(linkText);
        assertEquals(linkText, instance.getLinkText());
    }

    /**
     * Test of toString method, of class com.eventswarm.events.jdo.JdoEventURL.
     */
    public void testToString() {
        System.out.println("toString");
        
        JdoEventURL instance = TestEvents.jdoEvUrl1;
        
        String expResult = TestEvents.jdoEvUrl1.toString();
        String result = instance.toString();
        assertEquals(expResult, result);
    }    
}
