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
 * JdoEventTest.java
 * JUnit based test
 *
 * Created on May 11, 2007, 8:57 AM
 */

package com.eventswarm.events.jdo;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import com.eventswarm.events.*;

import java.util.*;
import java.util.Map;
import java.util.Set;
import java.net.URL;

// TODO review these tests now that we use the ID as the deadlock breaker when ordering events with compareTo

/**
 *
 * @author andyb
 */
public class JdoEventTest {
    
    Header header, headerA1, headerA2, headerA3, headerB1, headerB2, headerB3;
    EventURL evUrl1, evUrl2;
    RejectEvent reject;
    Set<EventPart> partsEmpty, partsSingle, partsDiffTypes, partsSameTypes;

    @Before
    public void setUp() throws Exception {
        // Various sets of EventParts
        header = new JdoHeader(new Date(), 0, new JdoSource("A"));
        partsEmpty = new HashSet<EventPart>();

                
        // create some timestamps
        Calendar cal = new GregorianCalendar(1999, 1, 1);
        Date oldTs = cal.getTime();
        Date ts = new Date();
        Date newTs = new Date(ts.getTime()+1);
        
        // various headers with diferent timestamps for comparison
        headerA1 = new JdoHeader(ts, 0, new JdoSource("A"));
        headerA2 = new JdoHeader(ts, 1, new JdoSource("A"));
        headerA3 = new JdoHeader(newTs, 0, new JdoSource("A"));
        headerB1 = new JdoHeader(oldTs, 0, new JdoSource("B"));
        headerB2 = new JdoHeader(ts, 0, new JdoSource("B"));
        headerB3 = new JdoHeader(newTs, 0, new JdoSource("B"));

                // some EventParts
        evUrl1 = new JdoEventURL(new URL("http://localhost"), "my web site");
        evUrl2 = new JdoEventURL(new URL("http://localhost:8080"), "My tomcat server");
        reject = new JdoRejectEvent("Because I can");
        
        partsSingle = new HashSet<EventPart>();
        partsSingle.add(evUrl1);
        
        partsDiffTypes = new HashSet<EventPart>();
        partsDiffTypes.add(evUrl1);
        partsDiffTypes.add(reject);
        
        partsSameTypes = new HashSet<EventPart>();
        partsSameTypes.add(evUrl1);
        partsSameTypes.add(evUrl2);
    }

    @After
    public void tearDown() throws Exception {
	// will call default tearDownOperation
        //databaseTester.onTearDown();
    }

    
    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeNull() {
        System.out.println("isBefore null");
        
        JdoEvent instance = new JdoEvent(headerA1, partsEmpty);
        
        Throwable th = null;
        try {
            boolean result = instance.isBefore(null);
        } catch (java.lang.NullPointerException ex) {
            th = ex;
        }

        assertSame(java.lang.NullPointerException.class, th.getClass());
    }


    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeSameSourceSameTSTrue() {
        System.out.println("isBefore same source true");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);
        
        assertTrue(e1.isBefore(e2));
    }

    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeSameSourceEqual() {
        System.out.println("isBefore same source false");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        
        assertFalse(e1.isBefore(e1));
    }


    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeSameSourceSameTSFalse() {
        System.out.println("isBefore same source false");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);

        assertFalse(e2.isBefore(e1));
    }


    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeSameSourceDiffTSTrue() {
        System.out.println("isBefore same source true");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA3, partsEmpty);
        
        assertTrue(e1.isBefore(e2));
    }


    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeSameSourceDiffTSFalse() {
        System.out.println("isBefore same source false");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA3, partsEmpty);

        assertFalse(e2.isBefore(e1));
    }


    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeDiffSourceTrue() {
        System.out.println("isBefore different source true");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB1, partsEmpty);
        
        assertTrue(e2.isBefore(e1));
    }


    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeDiffSourceAfter() {
        System.out.println("isBefore different source false (after)");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB1, partsEmpty);
        
        assertFalse(e1.isBefore(e2));
    }


    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBeforeDiffSourceConcurrent() {
        System.out.println("isBefore different source false (concurrent)");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB2, partsEmpty);
        
        assertFalse(e1.isBefore(e2));
    }
    
    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsAfterNull() {
        System.out.println("isAfter null");
        
        JdoEvent instance = new JdoEvent(headerA1, partsEmpty);
        
        Throwable th = null;
        try {
            boolean result = instance.isAfter(null);
        } catch (java.lang.NullPointerException ex) {
            th = ex;
        }

        assertSame(java.lang.NullPointerException.class, th.getClass());
    }


    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testisAfterSameSourceSameTSTrue() {
        System.out.println("isAfter same source true");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);
        
        assertTrue(e2.isAfter(e1));
    }

    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testisAfterSameSourceEqual() {
        System.out.println("isAfter same source false (equal)");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);

        assertFalse(e1.isAfter(e1));
    }


    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testisAfterSameSourceSameTSFalse() {
        System.out.println("isAfter same source false (before)");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);
        
        assertFalse(e1.isAfter(e2));
    }


    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testisAfterSameSourceDiffTSTrue() {
        System.out.println("isAfter same source different timestamp true");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA3, partsEmpty);
        
        assertTrue(e2.isAfter(e1));
    }


    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testisAfterSameSourceDiffTSFalse() {
        System.out.println("isAfter same source different timestamp false");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA3, partsEmpty);
        
        assertFalse(e1.isAfter(e2));
    }


    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testisAfterDiffSourceTrue() {
        System.out.println("isAfter different source true");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB1, partsEmpty);
        
        assertTrue(e1.isAfter(e2));
    }


    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testisAfterDiffSourceAfter() {
        System.out.println("isAfter different source false (after)");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB1, partsEmpty);
        
        assertFalse(e2.isAfter(e1));
    }

    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsAfterDiffSourceConcurrent() {
        System.out.println("isAfter different source false (concurrent)");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB2, partsEmpty);
        
        assertFalse(e1.isAfter(e2));
    }

    /**
     * Test of isConcurrent method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsConcurrentNull() {
        System.out.println("isConcurrent null");
        
        JdoEvent instance = new JdoEvent(headerA1, partsEmpty);
        
        Throwable th = null;
        try {
            boolean result = instance.isConcurrent(null);
        } catch (java.lang.NullPointerException ex) {
            th = ex;
        }

        assertSame(java.lang.NullPointerException.class, th.getClass());
    }


    /**
     * Test of isConcurrent method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsConcurrentSameSourceFalse() {
        System.out.println("isConcurrent same source false");
        
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);
        
        assertFalse(e1.isConcurrent(e2));
    }

    /**
     * Test of isConcurrent method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsConcurrentEquals() {
        System.out.println("isConcurrent equals (true)");
        
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        
        assertTrue(e1.isConcurrent(e1));
    }


    /**
     * Test of isConcurrent method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsConcurrentDiffSourceFalse() {
        System.out.println("isConcurrent different source false");
        
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB1, partsEmpty);
        
        assertFalse(e1.isConcurrent(e2));
    }


    /**
     * Test of isConcurrent method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsConcurrentDiffSourceTrue() {
        System.out.println("isConcurrent different source true");
        
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB2, partsEmpty);
        
        assertTrue(e1.isConcurrent(e2));
    }

    
    /**
     * Test of equals method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testEqualsNull() {
        System.out.println("equals null");
        
        Object obj = null;
        JdoEvent instance = new JdoEvent(headerA1, partsEmpty);
        
        Throwable th = null;
        try {
            boolean result = instance.equals(obj);
        } catch (java.lang.NullPointerException ex) {
            th = ex;
        }

        assertSame(java.lang.NullPointerException.class, th.getClass());
    }

    /**
     * Test of equals method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testEqualsTrueSameObject() {
        System.out.println("equals true same object");
        
        JdoEvent instance = new JdoEvent(headerA1, partsEmpty);
        
        assertTrue(instance.equals(instance));
    }


    /**
     * Test of equals method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testEqualsTrueDiffObject() {
        System.out.println("equals true different objects, same header");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA1, partsEmpty);
        
        assertTrue(e1.equals(e2));
    }

    /**
     * Test of equals method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testEqualsFalseDiffObject() {
        System.out.println("equals false different objects, different header");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);
        
        assertFalse(e1.equals(e2));
    }

    
    /**
     * Test of compareTo method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testCompareToNull() {
        System.out.println("compareTo null");
        
        JdoEvent instance = new JdoEvent(headerA1, partsEmpty);
        
        Throwable th = null;
        try {
            int result = instance.compareTo(null);
        } catch (java.lang.NullPointerException ex) {
            th = ex;
        }

        assertSame(java.lang.NullPointerException.class, th.getClass());
    }

        
    /**
     * Test of compareTo method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testCompareToEqual() {
        System.out.println("compareTo equal");
        
        JdoEvent instance = new JdoEvent(headerA1, partsEmpty);

        int expected = 0;
        int result = instance.compareTo(instance);
        
        assertSame(expected, result);
    }
        
    /**
     * Test of compareTo method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testCompareToSameSourceBefore() {
        System.out.println("compareTo same source before");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);

        int expected = -1;
        int result = e1.compareTo(e2);
        
        assertSame(expected, result);
    }

        
    /**
     * Test of compareTo method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testCompareToSameSourceAfter() {
        System.out.println("compareTo same source after");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsEmpty);

        int expected = 1;
        int result = e2.compareTo(e1);
        
        assertSame(expected, result);
    }
        
    /**
     * Test of compareTo method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testCompareToDiffSourceAfter() {
        System.out.println("compareTo different id, otherwise the same");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB2, partsEmpty);

        int result = e2.compareTo(e1);
        
        assertNotEquals(0, result);
    }

        
    /**
     * Test of compareTo method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testCompareToDiffSourceConcurrent() {
        System.out.println("compareTo different source concurrent");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB2, partsEmpty);

        int expected = e2.getHeader().getEventId().compareTo(e1.getHeader().getEventId());
        int result = e2.compareTo(e1);
        
        assertSame(expected, result);
    }
    
    /**
     * Test of getHttpHeader method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testGetNullHeader() {
        System.out.println("getHttpHeader null");
        
        JdoEvent instance = new JdoEvent(null, partsEmpty);
        
        Header result = instance.getHeader();
        assertNull(result);
     }

    /**
     * Test of getHttpHeader method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testGetHeader() {
        System.out.println("getHttpHeader not null");
        
        JdoEvent instance = new JdoEvent(header, partsEmpty);
        
        Header result = instance.getHeader();
        assertSame(header, result);
    }
        
    /**
     * Test of hashcode method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testHashcodeSameEvent() {
        System.out.println("hashcode same event");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        
        assertTrue(e1.hashCode() == e1.hashCode());
    }

    /**
     * Test of hashcode method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testHashcodeSameHeader() {
        System.out.println("hashcode same header");
        
        JdoEvent e1 = new JdoEvent(header, partsEmpty);
        JdoEvent e2 = new JdoEvent(header, partsSingle);
        
        assertTrue(e1.hashCode() == e2.hashCode());
    }

    /**
     * Test of hashcode method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testHashcodeSameSource() {
        System.out.println("hashcode same source and timestamp");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerA2, partsSingle);
        
        assertFalse(e1.hashCode() == e2.hashCode());
    }


    /**
     * Test of hashcode method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testHashcodeDiffSource() {
        System.out.println("hashcode different source, same timestamp");
        
        JdoEvent e1 = new JdoEvent(headerA1, partsEmpty);
        JdoEvent e2 = new JdoEvent(headerB2, partsSingle);
        
        assertFalse(e1.hashCode() == e2.hashCode());
    }

    /**
     * Test of getParts method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testGetParts() {
        System.out.println("getParts");
        
        JdoEvent instance = TestEvents.jdoEventPartsAll;
        
        Set<EventPart> expResult = TestEvents.partsAll;
        Set<EventPart> result = instance.getParts();
        assertEquals(expResult, result);
    }

    /**
     * Test of isBefore method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsBefore() {
        System.out.println("isBefore");
        
        JdoEvent instance = TestEvents.jdoEvent;
                
        assertEquals(false, instance.isBefore(TestEvents.eventBeforeSameSrcBeforeSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventBeforeSameSrcAfterSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventBeforeSameSrcConcSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventBeforeDiffSrcBeforeSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventBeforeDiffSrcAfterSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventBeforeDiffSrcConcSeq));
        assertEquals(true, instance.isBefore(TestEvents.eventAfterSameSrcBeforeSeq));
        assertEquals(true, instance.isBefore(TestEvents.eventAfterSameSrcAfterSeq));
        assertEquals(true, instance.isBefore(TestEvents.eventAfterSameSrcConcSeq));      
        assertEquals(true, instance.isBefore(TestEvents.eventAfterDiffSrcBeforeSeq));
        assertEquals(true, instance.isBefore(TestEvents.eventAfterDiffSrcAfterSeq));
        assertEquals(true, instance.isBefore(TestEvents.eventAfterDiffSrcConcSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventConcSameSrcBeforeSeq));
        assertEquals(true, instance.isBefore(TestEvents.eventConcSameSrcAfterSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventConcSameSrcConcSeq));       
        assertEquals(false, instance.isBefore(TestEvents.eventConcDiffSrcBeforeSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventConcDiffSrcAfterSeq));
        assertEquals(false, instance.isBefore(TestEvents.eventConcDiffSrcConcSeq));        
    }

    /**
     * Test of isAfter method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsAfter() {
        System.out.println("isAfter");
        
        JdoEvent instance = TestEvents.jdoEvent;
                
        assertEquals(true, instance.isAfter(TestEvents.eventBeforeSameSrcBeforeSeq));
        assertEquals(true, instance.isAfter(TestEvents.eventBeforeSameSrcAfterSeq));
        assertEquals(true, instance.isAfter(TestEvents.eventBeforeSameSrcConcSeq));
        assertEquals(true, instance.isAfter(TestEvents.eventBeforeDiffSrcBeforeSeq));
        assertEquals(true, instance.isAfter(TestEvents.eventBeforeDiffSrcAfterSeq));
        assertEquals(true, instance.isAfter(TestEvents.eventBeforeDiffSrcConcSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventAfterSameSrcBeforeSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventAfterSameSrcAfterSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventAfterSameSrcConcSeq));      
        assertEquals(false, instance.isAfter(TestEvents.eventAfterDiffSrcBeforeSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventAfterDiffSrcAfterSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventAfterDiffSrcConcSeq));
        assertEquals(true, instance.isAfter(TestEvents.eventConcSameSrcBeforeSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventConcSameSrcAfterSeq));
        // This test no longer holds: if sequence number and timestamp are the same, they're concurrent.
        //assertEquals(true, instance.isAfter(TestEvents.eventConcSameSrcConcSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventConcDiffSrcBeforeSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventConcDiffSrcAfterSeq));
        assertEquals(false, instance.isAfter(TestEvents.eventConcDiffSrcConcSeq));        
    }

    /**
     * Test of isConcurrent method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testIsConcurrent() {
        System.out.println("isConcurrent");
        
        JdoEvent instance = TestEvents.jdoEvent;
                
        assertEquals(false, instance.isConcurrent(TestEvents.eventBeforeSameSrcBeforeSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventBeforeSameSrcAfterSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventBeforeSameSrcConcSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventBeforeDiffSrcBeforeSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventBeforeDiffSrcAfterSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventBeforeDiffSrcConcSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventAfterSameSrcBeforeSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventAfterSameSrcAfterSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventAfterSameSrcConcSeq));      
        assertEquals(false, instance.isConcurrent(TestEvents.eventAfterDiffSrcBeforeSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventAfterDiffSrcAfterSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventAfterDiffSrcConcSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventConcSameSrcBeforeSeq));
        assertEquals(false, instance.isConcurrent(TestEvents.eventConcSameSrcAfterSeq));
        // Originally, same source was never concurrent. We've changed that. We can only assert that they're
        // not concurrent if the source has assigned sequence numbers, which we don't always get.
        // assertEquals(false, instance.isConcurrent(TestEvents.eventConcSameSrcConcSeq));
        assertEquals(true, instance.isConcurrent(TestEvents.eventConcDiffSrcBeforeSeq));
        assertEquals(true, instance.isConcurrent(TestEvents.eventConcDiffSrcAfterSeq));
        assertEquals(true, instance.isConcurrent(TestEvents.eventConcDiffSrcConcSeq));        
    }

    /**
     * Test of hasPart method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testHasPart() {
        System.out.println("hasPart");
        
        Class<?> typehas = EventURL.class;
        Class<?> typenothas = RejectEvent.class;
        JdoEvent instance = TestEvents.jdoEventAllUrls;

        assertEquals(true, instance.hasPart(typehas));
        assertEquals(false, instance.hasPart(typenothas));
    }

    /**
     * Test of equals method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        
        Object obj = TestEvents.event;
        JdoEvent instance = TestEvents.jdoEvent;
        
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);        
    }

    /**
     * Test of hashCode method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");

        JdoEvent instance = TestEvents.jdoEvent;

        int expResult = instance.getHeader().madeId().hashCode();
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        
        JdoEvent instance = TestEvents.jdoEvent;
        
        String expResult = TestEvents.event.toString();
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of setHeader method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testSetHeader() {
        System.out.println("setHeader");
        
        Header header = TestEvents.hdr;
        JdoEvent instance = TestEvents.jdoEvent;
        
        // Cheat and set the timestamp
        if (JdoHeader.class.isInstance(instance.getHeader())) {
            ((JdoHeader) instance.getHeader()).setTimestamp(header.getTimestamp());
        }
        
        instance.setHeader(header);
        assertEquals(header, instance.getHeader());
    }

    /**
     * Test of setParts method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testSetParts() {
        System.out.println("setParts");
        
        Set<EventPart> eventParts = TestEvents.partsAll;
        JdoEvent instance = TestEvents.jdoEvent;
        
        instance.setParts(eventParts);
        assertEquals(eventParts, instance.getParts());
    }

    /**
     * Test of compareTo method, of class com.eventswarm.events.jdo.JdoEvent.
     */
    @Test
    public void testCompareTo() {
        System.out.println("Refer to compareTo methods for further tests");
    }

    @Test
    public void testDupeIdWithSameTimestamp() {
        Date ts = new Date();
        Header header1 = new DupeHeader(ts, "1");
        Header header2 = new DupeHeader(ts, "1");
        Map<String,EventPart> parts = new HashMap<String,EventPart>();
        Event event1 = new JdoEvent(header1, parts);
        Event event2 = new JdoEvent(header2, parts);
        assertThat(event1, is(equalTo(event2)));
        // we have events with same id and timestamp but different sequence numbers, so the second event should be after
        assertEquals(0, event1.compareTo(event2));
    }

    @Test
    public void testDupeIdWithSameTimestampAndSequenceNumber() {
        Date ts = new Date();
        Header header1 = new JdoHeader(ts, 0, Sources.cache.getSourceByName("JdoEventTestSource"), "id1");
        Header header2 = new JdoHeader(ts, 0, Sources.cache.getSourceByName("JdoEventTestSource"), "id1");
        Map<String,EventPart> parts = new HashMap<String,EventPart>();
        Event event1 = new JdoEvent(header1, parts);
        Event event2 = new JdoEvent(header2, parts);
        assertThat(event1, is(equalTo(event2)));
        assertEquals(0, event1.compareTo(event2));
    }

    @Test
    public void testDupeIdWithDifferentTimestamp() {
        Date ts = new Date();
        Header header1 = new DupeHeader(ts, "1");
        Header header2 = new DupeHeader(new Date(ts.getTime()+1), "1");
        Map<String,EventPart> parts = new HashMap<String,EventPart>();
        Event event1 = new JdoEvent(header1, parts);
        Event event2 = new JdoEvent(header2, parts);
        assertThat(event1, is(not(equalTo(event2))));
        assertEquals(-1, event1.compareTo(event2));
    }

    @Test
    public void testNotDupeIdWithSameTimestamp() {
        Date ts = new Date();
        Header header1 = new DupeHeader(ts, "1");
        Header header2 = new DupeHeader(ts, "2");
        Map<String,EventPart> parts = new HashMap<String,EventPart>();
        Event event1 = new JdoEvent(header1, parts);
        Event event2 = new JdoEvent(header2, parts);
        assertNotSame(event1, event2);
        assertEquals("1".compareTo("2"), event1.compareTo(event2));
    }

    /**
     * Class for testing handling of externally-specified eventId values
     *
     * Creates an event with a specified date and id
     */
    private static class DupeHeader extends JdoHeader {
        static Source SOURCE = new JdoSource("DupeEventSource");
        public DupeHeader(Date date, String id) {
            super(date, SOURCE);
            this.eventId = id;
        }
    }
}
