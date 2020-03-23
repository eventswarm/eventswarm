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
 * TestEvents.java
 *
 * Created on May 13, 2007, 6:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.events.jdo;

import com.eventswarm.events.*;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;

/** Class holding a bunch of pre-configured test events, headers etc for testing.
 *
 * @author andyb
 */
public class TestEvents {
    
    // Date(s)
    public static Date date = null;
    public static Date dateBefore = null;
    public static Date dateAfter = null;
    public static Date dateConc = null;
    public static Date lastDate = null;

    // Sequence Numbers
    public static int seqNumber;
    public static int seqNumberBefore;
    public static int seqNumberAfter;
    public static int seqNumberConc;

    // Source Strings
    public static String srcStr = null;
    public static String srcStrSame = null;
    public static String srcStrDiff = null;
    
    // URL Strings
    public static String urlStr1 = "http://localhost";
    public static String urlStr2 = "http://localhost:8080";
    
    // URL Link Strings
    public static String urlLink2 = "My tomcat server";
    
    // URL(s)
    public static URL url1 = null;
    public static URL url2 = null;
    
    // Reject strings
    public static String rejectReason = "Because I can";  
    
    // JdoSource(s)
    public static JdoSource jdoSrc = null;
    public static JdoSource jdoSrcSame = null;
    public static JdoSource jdoSrcDiff = null;
    
    public static JdoSource A;
    public static JdoSource B;
    public static JdoSource C;

    // CausalityVectorImpl(s)
    public static CausalityVectorImpl impHdrVector = null;
    
    // JdoHeader(s)
    public static JdoHeader jdoHdr = null;
    public static JdoHeader jdoHdrReply = null;
    
    public static JdoHeader jdoHdrBeforeSameSrcBeforeSeq = null;
    public static JdoHeader jdoHdrBeforeSameSrcAfterSeq = null;
    public static JdoHeader jdoHdrBeforeSameSrcConcSeq = null;
    
    public static JdoHeader jdoHdrBeforeDiffSrcBeforeSeq = null;
    public static JdoHeader jdoHdrBeforeDiffSrcAfterSeq = null;
    public static JdoHeader jdoHdrBeforeDiffSrcConcSeq = null;
    
    public static JdoHeader jdoHdrAfterSameSrcBeforeSeq = null;
    public static JdoHeader jdoHdrAfterSameSrcAfterSeq = null;
    public static JdoHeader jdoHdrAfterSameSrcConcSeq = null;
    
    public static JdoHeader jdoHdrAfterDiffSrcBeforeSeq = null;
    public static JdoHeader jdoHdrAfterDiffSrcAfterSeq = null;
    public static JdoHeader jdoHdrAfterDiffSrcConcSeq = null;
    
    public static JdoHeader jdoHdrConcSameSrcBeforeSeq = null;
    public static JdoHeader jdoHdrConcSameSrcAfterSeq = null;
    public static JdoHeader jdoHdrConcSameSrcConcSeq = null;
    
    public static JdoHeader jdoHdrConcDiffSrcBeforeSeq = null;
    public static JdoHeader jdoHdrConcDiffSrcAfterSeq = null;
    public static JdoHeader jdoHdrConcDiffSrcConcSeq = null;
    
    // JdoEvent(s)
    public static JdoEventURL jdoEvUrl1 = null;
    public static JdoEventURL jdoEvUrl2 = null;
    public static JdoRejectEvent jdoReject = null;
    
    public static JdoEvent jdoEvent = null;   
    public static JdoEvent jdoEventPartsAll = null;
    
    public static JdoEvent jdoEventBeforeSameSrcBeforeSeq = null;
    public static JdoEvent jdoEventBeforeSameSrcAfterSeq = null;
    public static JdoEvent jdoEventBeforeSameSrcConcSeq = null;
    
    public static JdoEvent jdoEventBeforeDiffSrcBeforeSeq = null;
    public static JdoEvent jdoEventBeforeDiffSrcAfterSeq = null;
    public static JdoEvent jdoEventBeforeDiffSrcConcSeq = null;
    
    public static JdoEvent jdoEventAfterSameSrcBeforeSeq = null;
    public static JdoEvent jdoEventAfterSameSrcAfterSeq = null;
    public static JdoEvent jdoEventAfterSameSrcConcSeq = null;
    
    public static JdoEvent jdoEventAfterDiffSrcBeforeSeq = null;
    public static JdoEvent jdoEventAfterDiffSrcAfterSeq = null;
    public static JdoEvent jdoEventAfterDiffSrcConcSeq = null;
    
    public static JdoEvent jdoEventConcSameSrcBeforeSeq = null;
    public static JdoEvent jdoEventConcSameSrcAfterSeq = null;
    public static JdoEvent jdoEventConcSameSrcConcSeq = null;
    
    public static JdoEvent jdoEventConcDiffSrcBeforeSeq = null;
    public static JdoEvent jdoEventConcDiffSrcAfterSeq = null;
    public static JdoEvent jdoEventConcDiffSrcConcSeq = null;
    
    public static JdoEvent jdoEventAllUrls = null;
    
    // Header timestamps
    public static Date hdrTimestamp = null;
    public static Date hdrTimestampBefore = null;
    public static Date hdrTimestampAfter = null;
    public static Date hdrTimestampConc = null;
    
    // Header Sequence Numbers
    public static int hdrSeqNumber;
    public static int hdrSeqNumberBefore;
    public static int hdrSeqNumberAfter;
    public static int hdrSeqNumberConc;
    
    // Source(s)
    public static Source hdrSrc = null;
    public static Source hdrSrcSame = null;
    public static Source hdrSrcDiff = null;   

    // CausalityVector(s)
    public static CausalityVector hdrCausalityVector = null;

    // Header(s)
    public static Header header;
    public static Header hdr;
    public static Header hdrReply; 
    
    public static Header hdrBeforeSameSrcBeforeSeq = null;
    public static Header hdrBeforeSameSrcAfterSeq = null;
    public static Header hdrBeforeSameSrcConcSeq = null;
    
    public static Header hdrBeforeDiffSrcBeforeSeq = null;
    public static Header hdrBeforeDiffSrcAfterSeq = null;
    public static Header hdrBeforeDiffSrcConcSeq = null;
    
    public static Header hdrAfterSameSrcBeforeSeq = null;
    public static Header hdrAfterSameSrcAfterSeq = null;
    public static Header hdrAfterSameSrcConcSeq = null;
    
    public static Header hdrAfterDiffSrcBeforeSeq = null;
    public static Header hdrAfterDiffSrcAfterSeq = null;
    public static Header hdrAfterDiffSrcConcSeq = null;
    
    public static Header hdrConcSameSrcBeforeSeq = null;
    public static Header hdrConcSameSrcAfterSeq = null;
    public static Header hdrConcSameSrcConcSeq = null;
    
    public static Header hdrConcDiffSrcBeforeSeq = null;
    public static Header hdrConcDiffSrcAfterSeq = null;
    public static Header hdrConcDiffSrcConcSeq = null;
    
    public static Header headerA1;
    public static Header headerA2; 
    public static Header headerA3; 
    public static Header headerB1; 
    public static Header headerB2; 
    public static Header headerB3;

    // EventURL(s)
    public static EventURL evUrl1;
    public static EventURL evUrl2;
    public static RejectEvent reject;

    // Set<EventPart>(s)
    public static Set<EventPart> partsEmpty;
    public static Set<EventPart> partsSingle;
    public static Set<EventPart> partsDiffTypes;
    public static Set<EventPart> partsSameTypes;
    public static Set<EventPart> partsAll;
    public static Set<EventPart> partsAllUrls;

    // Map<String,EventPart>s
    public static Map<String,EventPart> partsEmptyMap;
    public static Map<String,EventPart> partsSingleMap;
    public static Map<String,EventPart> partsDiffTypesMap;
    public static Map<String,EventPart> partsSameTypesMap;
    public static Map<String,EventPart> partsAllMap;
    public static Map<String,EventPart> partsAllUrlsMap;
    
    // Event(s)
    public static Event event = null;
    
    public static Event eventBeforeSameSrcBeforeSeq = null;
    public static Event eventBeforeSameSrcAfterSeq = null;
    public static Event eventBeforeSameSrcConcSeq = null;
    
    public static Event eventBeforeDiffSrcBeforeSeq = null;
    public static Event eventBeforeDiffSrcAfterSeq = null;
    public static Event eventBeforeDiffSrcConcSeq = null;
    
    public static Event eventAfterSameSrcBeforeSeq = null;
    public static Event eventAfterSameSrcAfterSeq = null;
    public static Event eventAfterSameSrcConcSeq = null;
    
    public static Event eventAfterDiffSrcBeforeSeq = null;
    public static Event eventAfterDiffSrcAfterSeq = null;
    public static Event eventAfterDiffSrcConcSeq = null;
    
    public static Event eventConcSameSrcBeforeSeq = null;
    public static Event eventConcSameSrcAfterSeq = null;
    public static Event eventConcSameSrcConcSeq = null;
    
    public static Event eventConcDiffSrcBeforeSeq = null;
    public static Event eventConcDiffSrcAfterSeq = null;
    public static Event eventConcDiffSrcConcSeq = null;    
    
    public static Event eventPartsAll = null;
    public static Event eventAllUrls = null;
    
    static {
        // Calendar(s)
        Calendar cal = new GregorianCalendar(1999, 1, 1);

        // Date(s)
        date = new Date();
        dateBefore = new Date(date.getTime() - 1);
        dateAfter = new Date(date.getTime() + 1);
        dateConc = new Date(date.getTime());
        
        Date oldTs = cal.getTime();
        Date ts = new Date();
        Date newTs = new Date(ts.getTime()+1);
        lastDate = new Date();

        // URL(s)
        try {
            url1 = new URL(urlStr1);
            url2 = new URL(urlStr2);
        }
        catch (MalformedURLException ex) {
            // ignore for now. just doing this to suppress compiler errors
            url1 = null;
            url2 = null;
        }

        // Header Timestamp
        hdrTimestamp = date;
        hdrTimestampBefore = dateBefore;
        hdrTimestampAfter = dateAfter;
        hdrTimestampConc = dateConc;
        
        // Sequence Numbers
        seqNumber = 1;
        seqNumberBefore = 0;
        seqNumberAfter = 2;
        seqNumberConc = 1;
       
        // Header Sequence Numbers
        hdrSeqNumber = seqNumber;
        hdrSeqNumberBefore = seqNumberBefore;
        hdrSeqNumberAfter = seqNumberAfter;
        hdrSeqNumberConc = seqNumberConc;

        // Source strings
        srcStr = "A";
        srcStrSame = "A";
        srcStrDiff = "B";
        
        // JdoSource(s)
        jdoSrc = new JdoSource(srcStr);
        jdoSrcSame = new JdoSource(srcStrSame);
        jdoSrcDiff = new JdoSource(srcStrDiff);
        
        A = new JdoSource("A");
        B = new JdoSource ("B");
        C = new JdoSource("C");

        // Source(s)
        hdrSrc = jdoSrc;
        hdrSrcSame = jdoSrcSame;
        hdrSrcDiff = jdoSrcDiff;

        // CausalityVectorImpl(s)
        impHdrVector = new CausalityVectorImpl() ;        
        
        // CausalityVector(s)
        hdrCausalityVector = impHdrVector;

        // JdoHeader(s)
        jdoHdr = new JdoHeader(hdrTimestamp, hdrSeqNumber, hdrSrc, hdrCausalityVector, null, null, null);
        
        jdoHdrBeforeSameSrcBeforeSeq = new JdoHeader(hdrTimestampBefore, hdrSeqNumberBefore, hdrSrcSame, hdrCausalityVector, null, null, null);
        jdoHdrBeforeSameSrcAfterSeq  = new JdoHeader(hdrTimestampBefore, hdrSeqNumberAfter,  hdrSrcSame, hdrCausalityVector, null, null, null);
        jdoHdrBeforeSameSrcConcSeq   = new JdoHeader(hdrTimestampBefore, hdrSeqNumberConc,   hdrSrcSame, hdrCausalityVector, null, null, null);

        jdoHdrBeforeDiffSrcBeforeSeq = new JdoHeader(hdrTimestampBefore, hdrSeqNumberBefore, hdrSrcDiff, hdrCausalityVector, null, null, null);
        jdoHdrBeforeDiffSrcAfterSeq  = new JdoHeader(hdrTimestampBefore, hdrSeqNumberAfter,  hdrSrcDiff, hdrCausalityVector, null, null, null);
        jdoHdrBeforeDiffSrcConcSeq   = new JdoHeader(hdrTimestampBefore, hdrSeqNumberConc,   hdrSrcDiff, hdrCausalityVector, null, null, null);

        jdoHdrAfterSameSrcBeforeSeq  = new JdoHeader(hdrTimestampAfter,  hdrSeqNumberBefore, hdrSrcSame, hdrCausalityVector, null, null, null);
        jdoHdrAfterSameSrcAfterSeq   = new JdoHeader(hdrTimestampAfter,  hdrSeqNumberAfter,  hdrSrcSame, hdrCausalityVector, null, null, null);
        jdoHdrAfterSameSrcConcSeq    = new JdoHeader(hdrTimestampAfter,  hdrSeqNumberConc,   hdrSrcSame, hdrCausalityVector, null, null, null);
        
        jdoHdrAfterDiffSrcBeforeSeq  = new JdoHeader(hdrTimestampAfter,  hdrSeqNumberBefore, hdrSrcDiff, hdrCausalityVector, null, null, null);
        jdoHdrAfterDiffSrcAfterSeq   = new JdoHeader(hdrTimestampAfter,  hdrSeqNumberAfter,  hdrSrcDiff, hdrCausalityVector, null, null, null);
        jdoHdrAfterDiffSrcConcSeq    = new JdoHeader(hdrTimestampAfter,  hdrSeqNumberConc,   hdrSrcDiff, hdrCausalityVector, null, null, null);

        jdoHdrConcSameSrcBeforeSeq   = new JdoHeader(hdrTimestampConc,   hdrSeqNumberBefore, hdrSrcSame, hdrCausalityVector, null, null, null);
        jdoHdrConcSameSrcAfterSeq    = new JdoHeader(hdrTimestampConc,   hdrSeqNumberAfter,  hdrSrcSame, hdrCausalityVector, null, null, null);
        jdoHdrConcSameSrcConcSeq     = new JdoHeader(hdrTimestampConc,   hdrSeqNumberConc,   hdrSrcSame, hdrCausalityVector, null, null, null);
        
        jdoHdrConcDiffSrcBeforeSeq   = new JdoHeader(hdrTimestampConc,   hdrSeqNumberBefore, hdrSrcDiff, hdrCausalityVector, null, null, null);
        jdoHdrConcDiffSrcAfterSeq    = new JdoHeader(hdrTimestampConc,   hdrSeqNumberAfter,  hdrSrcDiff, hdrCausalityVector, null, null, null);
        jdoHdrConcDiffSrcConcSeq     = new JdoHeader(hdrTimestampConc,   hdrSeqNumberConc,   hdrSrcDiff, hdrCausalityVector, null, null, null);
                
        // Header(s)
        hdr = jdoHdr;
        header = hdr;
        
        hdrBeforeSameSrcBeforeSeq = jdoHdrBeforeSameSrcBeforeSeq;
        hdrBeforeSameSrcAfterSeq = jdoHdrBeforeSameSrcAfterSeq;
        hdrBeforeSameSrcConcSeq = jdoHdrBeforeSameSrcConcSeq;

        hdrBeforeDiffSrcBeforeSeq = jdoHdrBeforeDiffSrcBeforeSeq;
        hdrBeforeDiffSrcAfterSeq = jdoHdrBeforeDiffSrcAfterSeq;
        hdrBeforeDiffSrcConcSeq = jdoHdrBeforeDiffSrcConcSeq;

        hdrAfterSameSrcBeforeSeq = jdoHdrAfterSameSrcBeforeSeq;
        hdrAfterSameSrcAfterSeq = jdoHdrAfterSameSrcAfterSeq;
        hdrAfterSameSrcConcSeq = jdoHdrAfterSameSrcConcSeq;
        
        hdrAfterDiffSrcBeforeSeq = jdoHdrAfterDiffSrcBeforeSeq;
        hdrAfterDiffSrcAfterSeq = jdoHdrAfterDiffSrcAfterSeq;
        hdrAfterDiffSrcConcSeq = jdoHdrAfterDiffSrcConcSeq;

        hdrConcSameSrcBeforeSeq = jdoHdrConcSameSrcBeforeSeq;
        hdrConcSameSrcAfterSeq = jdoHdrConcSameSrcAfterSeq;
        hdrConcSameSrcConcSeq = jdoHdrConcSameSrcConcSeq;
        
        hdrConcDiffSrcBeforeSeq = jdoHdrConcDiffSrcBeforeSeq;
        hdrConcDiffSrcAfterSeq = jdoHdrConcDiffSrcAfterSeq;
        hdrConcDiffSrcConcSeq = jdoHdrConcDiffSrcConcSeq;

        headerA1 = new JdoHeader(ts, 0, A, null, null, null, null);
        headerA2 = new JdoHeader(ts, 1, A, null, null, null, null);
        headerA3 = new JdoHeader(newTs, 0, A, null, null, null, null);
        headerB1 = new JdoHeader(oldTs, 0, new JdoSource("B"), null, null, null, null);
        headerB2 = new JdoHeader(ts, 0, new JdoSource("B"), null, null, null, null);
        headerB3 = new JdoHeader(newTs, 0, new JdoSource("B"), null, null, null, null);

        // JdoEventURL(s)
        jdoEvUrl1 = new JdoEventURL(url1);
        jdoEvUrl2 = new JdoEventURL(url2, urlLink2);

        // EventURL(s)
        evUrl1 = jdoEvUrl1;
        evUrl2 = jdoEvUrl2;

        // JdoRejectEvent(s)
        jdoReject = new JdoRejectEvent(rejectReason);
        
        // Reject(s)
        reject = jdoReject;

        // Set<EventPart>(s)
        partsEmpty = new HashSet<EventPart>();
        partsEmptyMap = new HashMap<String,EventPart>();

        partsSingle = new HashSet<EventPart>();
        partsSingleMap = new HashMap<String,EventPart>();
        partsSingle.add(evUrl1);
        partsSingleMap.put("A", evUrl1);
        
        partsDiffTypes = new HashSet<EventPart>();
        partsDiffTypesMap = new HashMap<String,EventPart>();
        partsDiffTypes.add(evUrl1);
        partsDiffTypes.add(reject);
        partsDiffTypesMap.put("A",evUrl1);
        partsDiffTypesMap.put("B",reject);
        
        partsSameTypes = new HashSet<EventPart>();
        partsSameTypesMap = new HashMap<String,EventPart>();
        partsSameTypes.add(evUrl1);
        partsSameTypes.add(evUrl2);
        partsSameTypesMap.put("A",evUrl1);
        partsSameTypesMap.put("B",evUrl2);
        
        partsAllUrls = partsSameTypes;
        
        partsAll = new HashSet<EventPart>();
        partsAllMap = new HashMap<String,EventPart>();
        partsAll.add(evUrl1);
        partsAll.add(evUrl2);
        partsAll.add(reject);        
        partsAllMap.put("A",evUrl1);
        partsAllMap.put("B",evUrl2);
        partsAllMap.put("C",reject);
        
        // JdoEvent(s)
        jdoEvent = new JdoEvent(jdoHdr, (HashSet<EventPart>) null);

        jdoEventBeforeSameSrcBeforeSeq = new JdoEvent(jdoHdrBeforeSameSrcBeforeSeq, (HashSet<EventPart>) null);
        jdoEventBeforeSameSrcAfterSeq = new JdoEvent(jdoHdrBeforeSameSrcAfterSeq, (HashSet<EventPart>) null);
        jdoEventBeforeSameSrcConcSeq = new JdoEvent(jdoHdrBeforeSameSrcConcSeq, (HashSet<EventPart>) null);

        jdoEventBeforeDiffSrcBeforeSeq = new JdoEvent(jdoHdrBeforeDiffSrcBeforeSeq, (HashSet<EventPart>) null);
        jdoEventBeforeDiffSrcAfterSeq = new JdoEvent(jdoHdrBeforeDiffSrcAfterSeq, (HashSet<EventPart>) null);
        jdoEventBeforeDiffSrcConcSeq = new JdoEvent(jdoHdrBeforeDiffSrcConcSeq, (HashSet<EventPart>) null);

        jdoEventAfterSameSrcBeforeSeq = new JdoEvent(jdoHdrAfterSameSrcBeforeSeq, (HashSet<EventPart>) null);
        jdoEventAfterSameSrcAfterSeq = new JdoEvent(jdoHdrAfterSameSrcAfterSeq, (HashSet<EventPart>) null);
        jdoEventAfterSameSrcConcSeq = new JdoEvent(jdoHdrAfterSameSrcConcSeq, (HashSet<EventPart>) null);
        
        jdoEventAfterDiffSrcBeforeSeq = new JdoEvent(jdoHdrAfterDiffSrcBeforeSeq, (HashSet<EventPart>) null);
        jdoEventAfterDiffSrcAfterSeq = new JdoEvent(jdoHdrAfterDiffSrcAfterSeq, (HashSet<EventPart>) null);
        jdoEventAfterDiffSrcConcSeq = new JdoEvent(jdoHdrAfterDiffSrcConcSeq, (HashSet<EventPart>) null);

        jdoEventConcSameSrcBeforeSeq = new JdoEvent(jdoHdrConcSameSrcBeforeSeq, (HashSet<EventPart>) null);
        jdoEventConcSameSrcAfterSeq = new JdoEvent(jdoHdrConcSameSrcAfterSeq, (HashSet<EventPart>) null);
        jdoEventConcSameSrcConcSeq = new JdoEvent(jdoHdrConcSameSrcConcSeq, (HashSet<EventPart>) null);
        
        jdoEventConcDiffSrcBeforeSeq = new JdoEvent(jdoHdrConcDiffSrcBeforeSeq, (HashSet<EventPart>) null);
        jdoEventConcDiffSrcAfterSeq = new JdoEvent(jdoHdrConcDiffSrcAfterSeq, (HashSet<EventPart>) null);
        jdoEventConcDiffSrcConcSeq = new JdoEvent(jdoHdrConcDiffSrcConcSeq, (HashSet<EventPart>) null);
        
        jdoEventPartsAll = new JdoEvent(hdr, partsAll);
        jdoEventAllUrls = new JdoEvent(hdr, partsAllUrls);
        
        // Event(s)
        event = jdoEvent;
        
        eventBeforeSameSrcBeforeSeq = jdoEventBeforeSameSrcBeforeSeq;
        eventBeforeSameSrcAfterSeq = jdoEventBeforeSameSrcAfterSeq;
        eventBeforeSameSrcConcSeq = jdoEventBeforeSameSrcConcSeq;

        eventBeforeDiffSrcBeforeSeq = jdoEventBeforeDiffSrcBeforeSeq;
        eventBeforeDiffSrcAfterSeq = jdoEventBeforeDiffSrcAfterSeq;
        eventBeforeDiffSrcConcSeq = jdoEventBeforeDiffSrcConcSeq;

        eventAfterSameSrcBeforeSeq = jdoEventAfterSameSrcBeforeSeq;
        eventAfterSameSrcAfterSeq = jdoEventAfterSameSrcAfterSeq;
        eventAfterSameSrcConcSeq = jdoEventAfterSameSrcConcSeq;
        
        eventAfterDiffSrcBeforeSeq = jdoEventAfterDiffSrcBeforeSeq;
        eventAfterDiffSrcAfterSeq = jdoEventAfterDiffSrcAfterSeq;
        eventAfterDiffSrcConcSeq = jdoEventAfterDiffSrcConcSeq;

        eventConcSameSrcBeforeSeq = jdoEventConcSameSrcBeforeSeq;
        eventConcSameSrcAfterSeq = jdoEventConcSameSrcAfterSeq;
        eventConcSameSrcConcSeq = jdoEventConcSameSrcConcSeq;
        
        eventConcDiffSrcBeforeSeq = jdoEventConcDiffSrcBeforeSeq;
        eventConcDiffSrcAfterSeq = jdoEventConcDiffSrcAfterSeq;
        eventConcDiffSrcConcSeq = jdoEventConcDiffSrcConcSeq;
        
        eventPartsAll = jdoEventPartsAll;
        eventAllUrls = jdoEventAllUrls;

        // Reply Components
        jdoHdrReply = new JdoHeader(
                hdrTimestamp,
                hdrSeqNumber,
                hdrSrc,
                hdrCausalityVector,
                event,
                event,
                null);
        hdrReply = jdoHdrReply;        
    }
    
    /** Creates a new instance of TestEvents */
    public TestEvents() {
    }
    
    /** Create a new header, ensuring that it's different from any other we've
     * generated.
     *
     * This method is not synchronized and assumes it will only be used from a 
     * single test thread.
     */
    public static Header createHeader() {
        lastDate = new Date(lastDate.getTime() + 1);
        return new JdoHeader(lastDate, 0, C, null, null, null, null);
    }
}
