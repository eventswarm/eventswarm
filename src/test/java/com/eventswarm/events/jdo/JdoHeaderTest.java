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
 * JdoHeaderTest.java
 * JUnit based test
 *
 * Created on 24 May 2007, 16:00
 */

package com.eventswarm.events.jdo;

import junit.framework.*;
import com.eventswarm.events.*;

import java.util.*;

/**
 *
 * @author vji
 */
public class JdoHeaderTest extends TestCase {
    
    public JdoHeaderTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getTimestamp method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        
        JdoHeader instance = TestEvents.jdoHdr;
        
        Date expResult = TestEvents.hdrTimestamp;
        Date result = instance.getTimestamp();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSequenceNumber method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testGetSequenceNumber() {
        System.out.println("getSequenceNumber");
        
        JdoHeader instance = TestEvents.jdoHdr;
        
        int expResult = TestEvents.hdrSeqNumber;
        int result = instance.getSequenceNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSource method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testGetSource() {
        System.out.println("getSource");
        
        JdoHeader instance = TestEvents.jdoHdr;
        
        Source expResult = TestEvents.hdrSrc;
        Source result = instance.getSource();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCausality method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testGetCausality() {
        System.out.println("getCausality");
        
        JdoHeader instance = TestEvents.jdoHdr;
        
        CausalityVector expResult = TestEvents.hdrCausalityVector;
        CausalityVector result = instance.getCausality();
        assertEquals(expResult, result);
    }

    /**
     * Test of getInReplyTo method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testGetInReplyTo() {
        System.out.println("getInReplyTo");
        
        JdoHeader instance1 = TestEvents.jdoHdr;
        Event result1 = instance1.getInReplyTo();
        assertNull(result1);
        
        JdoHeader instance2 = TestEvents.jdoHdrReply;
        Event expResult = TestEvents.event;
        Event result2 = instance2.getInReplyTo();
        assertEquals(expResult, result2);
    }

    /**
     * Test of isReply method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testIsReply() {
        System.out.println("isReply");
        
        JdoHeader instance = TestEvents.jdoHdr;
        boolean expResult = false;
        boolean result = instance.isReply();
        assertEquals(expResult, result);
        
        JdoHeader instance1 = TestEvents.jdoHdrReply;
        boolean expResult1 = true;
        boolean result1 = instance1.isReply();
        assertEquals(expResult1, result1);
    }

    /**
     * Test of getReplyTo method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testGetReplyTo() {
        System.out.println("getReplyTo");
        
        JdoHeader instance1 = TestEvents.jdoHdr;
        Event result1 = instance1.getReplyTo();
        assertNull(result1);
        
        JdoHeader instance2 = TestEvents.jdoHdrReply;
        Event expResult = TestEvents.event;
        Event result2 = instance2.getReplyTo();
        assertEquals(expResult, result2);
    }

    /**
     * Test of setReplyTo method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testSetReplyTo() {
        System.out.println("setReplyTo");
        
        Event event = TestEvents.event;
        JdoHeader instance = new JdoHeader();
        
        instance.setReplyTo(event);
        assertEquals(TestEvents.event, instance.getReplyTo());
    }

    /**
     * Test of eventId method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testMadeId() {
        System.out.println("eventId");
        
        JdoHeader instance = TestEvents.jdoHdr;
        
        String expResult = instance.madeId();
        String result = instance.madeId();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testToString() {
        System.out.println("toString");
        
        JdoHeader instance = TestEvents.jdoHdr;
        
        String expResult = instance.toString();
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTimestamp method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testSetTimestamp() {
        System.out.println("setTimestamp");
        
        Date timestamp = TestEvents.hdrTimestamp;
        JdoHeader instance = new JdoHeader();
        
        instance.setTimestamp(timestamp);
        assertEquals(TestEvents.hdrTimestamp, instance.getTimestamp());
    }

    /**
     * Test of setSequenceNumber method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testSetSequenceNumber() {
        System.out.println("setSequenceNumber");
        
        int sequenceNumber = TestEvents.hdrSeqNumber;
        JdoHeader instance = new JdoHeader();
        
        instance.setSequenceNumber(sequenceNumber);
        assertEquals(TestEvents.hdrSeqNumber, instance.getSequenceNumber());
    }

    /**
     * Test of setSource method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testSetSource() {
        System.out.println("setSource");
        
        Source source = TestEvents.hdrSrc;
        JdoHeader instance = new JdoHeader();
        
        instance.setSource(source);
        assertEquals(TestEvents.hdrSrc, instance.getSource());
    }

    /**
     * Test of setCausality method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testSetCausality() {
        System.out.println("setCausality");
        
        CausalityVector causality = TestEvents.hdrCausalityVector;
        JdoHeader instance = new JdoHeader();
        
        instance.setCausality(causality);
        assertEquals(TestEvents.hdrCausalityVector, instance.getCausality());
    }

    /**
     * Test of setInReplyTo method, of class com.eventswarm.events.jdo.JdoHeader.
     */
    public void testSetInReplyTo() {
        System.out.println("setInReplyTo");
        
        Event inReplyTo = TestEvents.event;
        JdoHeader instance = new JdoHeader();
        
        instance.setInReplyTo(inReplyTo);
        assertEquals(TestEvents.event, instance.getInReplyTo());
    }

    /**
     * Test of getTimestamp method, of class com.eventswarm.events.jdo.JdoHeader.
     */
//    public void testJsonSerialise() {
//        System.out.println("JSON serialisation");
//
//        JdoHeader instance = TestEvents.jdoHdr;
//        JSONObject result = new JSONObject(instance);
//        System.out.println("JSON: " + result.toString());
//        assertFalse(result.isNull("timestamp"));
//        assertFalse(result.isNull("eventId"));
//        assertEquals(1, result.getInt("sequenceNumber"));
//        assertEquals("A", result.getJSONObject("source").getString("sourceId"));
//    }
}
