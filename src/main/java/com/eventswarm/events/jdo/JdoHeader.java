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
 * CastorHeader.java
 *
 * Created on April 22, 2007, 10:48 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.events.jdo;

import java.util.*;
import com.eventswarm.events.*;
import com.eventswarm.util.Sequencer;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.NameBasedGenerator;


/**
 * Implementation of the Header interface suitable for persistence.
 *
 * @author andyb
 */
public class JdoHeader extends JdoEventPart implements Header {

    public static Random random = new Random();
    public static NameBasedGenerator generator = Generators.nameBasedGenerator();

    protected Date            timestamp      = null;
    protected int             sequenceNumber = 0;
    protected Source          source         = null;
    protected String          eventId        = null;
    protected CausalityVector causality      = null;
    protected Event           inReplyTo      = null;
    protected Boolean         isReply        = Boolean.FALSE;
    protected Event           replyTo        = null;
    protected UUID            uuid           = null;

    /**
     * Create a header for error and other events using the default local source, the current time and an auto-incremented
     * sequence number
     *
     * Note that this is not a constructor because the default constructor is used by factories etc. Note that because
     * we assign auto-incremented sequence numbers, events created with these headers will <strong>always</strong>
     * be ordered in order of header creation.
     *
     * @return a new header with local source, current time and an auto-incremented sequence number
     */
    public static JdoHeader getLocalHeader() {
        Date ts = new Date();
        return new JdoHeader(new Date(), Sequencer.getInstance().getNext(ts.getTime()), JdoSource.getLocalSource());
    }

    /**
     * Default constructor for use by factories, subclasses and persistence frameworks
     */
    public JdoHeader() {
        super();
    }

    /**
     * Creates a new instance of JdoHeader
     *
     * Primary constructor with all fields.
     */
    public JdoHeader(Date timestamp, int sequenceNumber, Source source, CausalityVector causality, Event inReplyTo, Event replyTo, String eventId) {
        
        // Let parent do it's thing
        super();
        
        // Store passed parameters
        setTimestamp(timestamp);
        setSequenceNumber(sequenceNumber);
        setSource(source);
        setCausality(causality);
        setInReplyTo(inReplyTo);
        setReplyTo(replyTo);
        setUuid(makeUuid());
        if (eventId == null) {
            setEventId(this.uuid.toString());
        } else {
            setEventId(eventId);
        }
    }
    
    /**
     * Creates a new instance of JdoHeader with a specific timestamp, sequence number and source
     *
     * Note that allowing the ID to be automatically assigned increases the risk of duplicates (i.e. there
     * is no easy way to detect duplicates. If you can extract an ID, then you should.
     *
     * Also note that sequence numbers are used to order events with the same timestamp in an EventSet, but events
     * with the same source and timestamp are considered concurrent.
     */
    public JdoHeader(Date timestamp, int sequenceNumber, Source source) {
        this(timestamp, sequenceNumber, source, null, null, null, null);
    }

    /**
     * Creates an event with the nominated millisecond timestamp and source, but using a default ID (UUID)
     *
     * An ID will be assigned using a UUID generator and a default sequence number of 0 will be assigned.
     *
     * Note that events created with the same timestamp and source using this header constructor will be
     * considered concurrent and have an arbitrary but deterministic order in an EventSet.
     */
    public JdoHeader (long timestamp, String source) {
        this(new Date(timestamp), 0, new JdoSource(source));
    }

    /**
     * Same as the previous constructor, but taking a Java Date instead of a millisecond timestamp.
     *
     * @param timestamp
     * @param source
     */
    public JdoHeader (Date timestamp, Source source) {
        this(timestamp, 0, source, null, null, null, null);
    }

    /**
     * Constructor with an externally-provided id, timestamp and source identifier
     *
     * IDs must be unique across all event sources: typically a fully qualified URL or UUID is required.
     *
     * A sequence number of 0 will be assigned, so events with the same timestamp will be ordered using the
     * lexical order of their IDs in an EventSet.
     *
     * @param timestamp
     * @param source
     * @param eventId
     */
    public JdoHeader (Date timestamp, Source source, String eventId) {
        this(timestamp, 0, source, null, null, null, eventId);
    }

    /**
     * Construct an event with id, timestamp and source identifier that uses the supplied sequencer
     * to assign sequence numbers.
     *
     * IDs must be unique across all event sources: typically a fully qualified URL or UUID is required. Since the
     * Sequencer class is a singleton, there isn't much choice about which one to use unless you create a subclass.
     *
     * This constructor should only be used for events where there is no chance of duplicates or you want duplicates
     * treated as distinct events, or you use a duplicate filter downstream.
     *
     * @param timestamp Event timestamp, indicating when the 'event' was observed
     * @param source Source of the event, typically a domain name
     * @param eventId A unique ID for the event, a UUID will be assigned if this is null
     * @param sequencer A sequencer for assigning sequence numbers to events with the same timestamp
     */
    public JdoHeader (Date timestamp, Source source, String eventId, Sequencer sequencer) {
        this(timestamp, sequencer.getNext(timestamp.getTime()), source, null, null, null, eventId);
    }

    /**
     * Constructor with an externally-provided id and sequence number
     *
     * <strong>Do not</strong> use this constructor unless are sure that duplicates will <strong>always</strong>
     * be assigned the same sequence number, or that there are no duplicates. Otherwise, you can end up with
     * ordering anomalies.
     *
     * @param timestamp
     * @param source
     * @param eventId
     */
    public JdoHeader (Date timestamp, int sequenceNumber, Source source, String eventId) {
        this(timestamp, sequenceNumber, source, null, null, null, eventId);
    }

    // Getters
    public Date            getTimestamp()      { return timestamp; }
    public int             getSequenceNumber() { return sequenceNumber; }
    public Source          getSource()         { return source;    }
    public CausalityVector getCausality()      { return causality; }
    public Event           getInReplyTo()      { return inReplyTo; }   
    public boolean         isReply()           { return isReply;   }
    public Event           getReplyTo()        { return replyTo;   }
    public UUID            getUuid()           { return uuid;      }
    public String          getEventId()        { return eventId;   }

    // Setters
    public void     setTimestamp(Date timestamp)            { this.timestamp = timestamp; }
    public void     setSequenceNumber(int sequenceNumber)   { this.sequenceNumber = sequenceNumber; }
    public void     setSource(Source source)                { this.source = source;    }
    public void     setCausality(CausalityVector causality) { this.causality = causality; }
    public void     setInReplyTo(Event inReplyTo)           {
        this.inReplyTo = inReplyTo;
        this.isReply   = (inReplyTo != null);
    }
    public void setUuid(UUID uuid) {this.uuid = uuid; this.eventId = uuid.toString();}
    public void setEventId(String eventId) {this.eventId = eventId;}

    /**
     * We need to allow the setting of the "replyTo" header after event part 
     * creation, because the factory creates the header event part first and 
     * we often want to reference the current event.  This call is ignored
     * once the ReplyTo is set (not null), so it can only be set once.
     */
    public void            setReplyTo(Event event) {
        if (this.replyTo == null) {
            this.replyTo = event;
        } 
        return;
    }

    /**
     * Generate a string representation of this event's ID
     *
     * Now that we use UUIDs, this is just the UUID string or the eventId provided at construction
     *
     * @return
     */
    public String madeId() {
        // create an ID if we don't have one. Use source + timestamp + sequence number + random
        if (eventId == null) {
            eventId = uuid.toString();
        }
        return eventId;
    }

    /**
     * Construct a new UUID using source, timestamp, sequence number and a random long
     *
     * @return
     */
    private UUID makeUuid() {
        return generator.generate(source.getSourceId() + Long.toString(timestamp.getTime()) +
                                  Integer.toString(sequenceNumber) + Long.toString(random.nextLong()));
    }

    public String toString() {
        
        // Prepare buffer where we can build up the string
        StringBuffer string = new StringBuffer();
        
        // Start output
        string.append("{JdoHeader: ");
        
        // Output the fields
        string.append("eventId = ");         string.append(madeId().toString());     string.append(", ");
        string.append("timestamp = ");      string.append(getTimestamp().toString());  string.append(", ");
        string.append("sequenceNumber = "); string.append(getSequenceNumber());        string.append(", ");
        string.append("source = ");         string.append(getSource().getSourceId());  string.append(", ");
        string.append("isReply = ");        string.append(isReply());                  string.append(", ");
        
        // Adequately handle inReplyTo event
        if (isReply()) {
            string.append("inReplyTo = ");
            string.append(getInReplyTo().getHeader().getEventId());
        }
        else {
            string.append("inReplyTo = null");
        }
        
        // Match up braces
        string.append("}");
        
        return string.toString();
    }

    public boolean equals(Object obj) {
        if (JdoHeader.class.isInstance(obj)) {
            return equals((JdoHeader) obj);
        }
        return false;
    }
    
    private boolean equals (JdoHeader part) {
        if (!super.equals(part)) {return false;}
        else  {return uuid.equals(part.uuid);}
    }
}
