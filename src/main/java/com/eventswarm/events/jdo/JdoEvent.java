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
 * CastorEvent.java
 *
 * Created on April 22, 2007, 10:37 AM
 *
 */

package com.eventswarm.events.jdo;

import com.eventswarm.events.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Implementation of the Event interface suitable for persistence.
 *
 * @author andyb
 */

public class JdoEvent implements Event {
          
    protected Header         header     = null;
    protected Map<String, EventPart> eventParts = null;
    
    private Long id; // for use by persistence only

    private static Logger logger = Logger.getLogger(JdoEvent.class);
    
    /** Creates a new instance of JdoEvent */
    protected JdoEvent() {
        super();
    }
    
    
    /**
     * Creates a new JdoEvent from a set of component event parts and a header.
     * 
     * EventParts are held in a hashtable nowadays, so this constructor uses
     * the position in the set as a hash key.  This constructor has been 
     * deprecated and is included only for backward compatibility.
     * 
     * @param header  Header for the event
     * @param eventParts Set of EventParts to include in the event
     */
    @Deprecated
    public JdoEvent(Header header, Set<EventPart> eventParts) {
        this.header     = header;
        this.eventParts = makeMap(eventParts);
    }
    
    public JdoEvent(Header header, Map<String,EventPart> eventParts) {
        this.header = header;
        this.eventParts = eventParts;
    }
    
    /**
     * Return the set of eventparts in an event, not including the header.
     * 
     * EventParts are held in a hashtable nowadays.  This method is deprecated 
     * is included only for backward compatibility.
     * 
     * @return Set of EventParts held in the Event
     * @deprecated
     */
    @Deprecated
    public Set<EventPart> getParts()  { 
        return new HashSet<EventPart>(eventParts.values()); 
    }
    
    
    public Map<String, EventPart> getPartsMap() {
        return this.eventParts;
    }

    public Header         getHeader() { return header;     }

    public boolean isBefore (Event event) {
        return (this.order(event) == -1);
    }
    
    public boolean isAfter (Event event) { 
        return (this.order(event) == 1);
    }
    
    public boolean isConcurrent(Event event) {
        return (this.order(event) == 0);
    }
    
    /** Return indication of ordering 
     *
     * @return -1 if this event is before, 0 if this event is concurrent, 1 if this event is after
     */
    public int order (Event event) {
        // return 0 if they are equal (same id and timestamp)
        if (this.equals(event)) return 0;

        if (Activity.class.isInstance(this)) {
            // If we're an activity, return the result of the activity specific method
            return((Activity) this).order(event);
        }
        if (Activity.class.isInstance(event)) {
            // If the other is an activity, return the negation of the activity specific method
            return(-((Activity) event).order(this));
        }

        // pre-compare timestamps
        boolean before = (header.getTimestamp().before(event.getHeader().getTimestamp()));
        boolean after = (header.getTimestamp().after(event.getHeader().getTimestamp()));

        // logic is different for same/different source
        if (header.getSource().equals(event.getHeader().getSource())) {
            // same source, never concurrent
            if (before) return -1;
            if (after) return 1;
            // same timestamp, compare sequence numbers
            return sequenceOrder(event);
            
        } else {
            // different source. Should compare causality vectors, but for now, just use timestamp
            if (before) return -1;
            if (after) return 1;
            
            // Different sources could potentially be on different locations, so
            // sequence numbers are not used to determine concurrency.
            
            // neither before nor after, so concurrent
            return(0);
        }
    }

    /**
     * Return true if the event has a part of the specified type.
     *
     * This method is of less use now that events are stored using a Map.  It is 
     * preferable to check for the existence of parts using the key.  The method
     * has not been deprecated because it is still useful in situations where 
     * the type of each EventPart is significant.
     * 
     * @param type Class or Interface that you're looking for.
     */
    public boolean hasPart(Class<?> type) {
        if (this.eventParts == null) return false;
        Iterator<EventPart> iter = this.eventParts.values().iterator();
        boolean hasPart = false;
        while (iter.hasNext() && !hasPart) {
            hasPart = type.isInstance(iter.next());
        }
        return hasPart;
    }

    
    public boolean hasPart(String key) {
        return this.eventParts.containsKey(key);
    }

    /**
     * Return a single EventPart object of the specified type.
     *
     * There are many times when an Event contains only one EventPart of a
     * particular type.  When this happens it is handy to return just the one,
     * rather than return a set containing just one.
     *
     * This method will return the single EventPart for the given type.  If
     * more than one EventPart of the given type is in the event, then an
     * an IllegalArgumentException will be thrown.
     */
    public EventPart getPart(Class<?> type) {
        
        // Routine variables
        Set<EventPart>      parts = null; // Set of EventPart of given type
        Iterator<EventPart> iter  = null; // Iterator
        
        // Return the set of EventPart for this type.
        parts = getParts(type);
        if (parts == null || parts.size() == 0) {
            return null;
        }
        
        // Insure there is only one
        if (parts.size() > 1) {
            throw new IllegalArgumentException(
                "There are " + parts.size() + " " + type + " event parts in " +
                "the event");
        }
        
        // Otherwise retrieve just the one
        iter = parts.iterator();
        for (EventPart part : parts) {
            return iter.next();
        }
        
        // Should never get to here
        return null;
    }

    public EventPart getPart(String key) {
        return(this.eventParts.get(key));
    }
    
    /**
     * Return a set of EventPart objects implementing the specified type.
     * 
     * Initial implementation just iterates.  We should possibly index, but 
     * the size of the type hierarchy might make this more expensive than 
     * iterating on demand.
     * 
     * This method is of less use now that EventParts are stored in a map but 
     * has not been deprecated because it is still useful when EventPart type is 
     * significant.
     * 
     */
    public Set<EventPart> getParts(Class<?> type) {
        if (this.eventParts == null) return null;
        Iterator<EventPart> iter = this.eventParts.values().iterator();
        Set<EventPart> parts = new HashSet<EventPart>();
        while (iter.hasNext()) {
            EventPart part = iter.next();
            if (type.isInstance(part)) {
                parts.add(part);
            }
        }
        return parts;
    }

    /**
     * Only events with same ID, timestamp and sequence number are equal, otherwise our compareTo is dodgy
     *
     * Channels should filter duplicates if there is a risk of the same event having different timestamps.
     * If sequence numbers are used, channels are responsible for ensuring the same event cannot be created
     * with the same timestamp and different sequence numbers.
     */
    @Override
    public boolean equals(Object obj) {
        // try a cast 
        try {
            Event event = (Event) obj;
            if (idEquals(event)) {
                if (timeEquals(event)) {
                    if (seqnrEquals(event)) {
                        return true;
                    } else {
                        logger.error("Events have same id (" + header.getEventId() +
                                ") and timestamp (" + getHeader().getTimestamp().toString() +
                                ") but different sequence numbers: " +
                                Integer.toString(getHeader().getSequenceNumber()) + "," +
                                Integer.toString(event.getHeader().getSequenceNumber()));
                        return false;
                    }
                } else {
                    logger.info("Events have same id " + header.getEventId() +
                            " but different timestamps: " +
                            getHeader().getTimestamp().toString() + "," +
                            event.getHeader().getTimestamp().toString());
                    return false;
                }
            } else {
                return false;
            }
        } catch (ClassCastException ex) {
            return false;
        }
    }

    private boolean seqnrEquals(Event event) {
        return this.getHeader().getSequenceNumber() == event.getHeader().getSequenceNumber();
    }

    private boolean idEquals(Event event) {
        return this.getHeader().madeId().equals(event.getHeader().madeId());
    }

    private boolean timeEquals(Event event) {
        return this.getHeader().getTimestamp().equals(event.getHeader().getTimestamp());
    }
    
    /** Use ID string as basis for hashcode */
    @Override
    public int hashCode() {
        return this.header.madeId().hashCode();
    }

    @Override
    public String toString() {
        
        // Prepare buffer where we can build up the string
        StringBuffer string = new StringBuffer();
        
        // Output the class name
        string.append("{JdoEvent: ");
        
        // Output the header
        string.append("header = "); string.append(getHeader().toString()); string.append(", ");
        
        // Output the event parts
        string.append("eventParts = [");
        if (eventParts != null) {
            Iterator<Map.Entry<String,EventPart>> iterator = 
                    eventParts.entrySet().iterator();
            while (iterator.hasNext()) {
            
                // Get the next event part
                Map.Entry<String,EventPart> entry = iterator.next();
           
                // Output the event part
                string.append(entry.getKey());
                string.append(" => ");
                EventPart value = entry.getValue();
                if (value != null) {
                    string.append(value.toString());
                } else {
                    string.append("null");
                }
            
                // Determine whether we want to store a comma or not
                if (iterator.hasNext()) {
                    string.append(",");
                }
            }
        }
        string.append("]");
        
        // Match up braces
        string.append("}");
        
        return string.toString();
    }

    /** Get persistence id.  Should not be used except by persistence */
    protected Long getId() {
        return this.id;
    }

    /** Set persistence id.  Should not be used except by persistence */
    protected void setId(Long id) {
        this.id = id;
    }
    
    public void setHeader(Header header) {
        this.header = header;
    }
    
    @Deprecated
    public void setParts(Set<EventPart> eventParts) {
        this.eventParts = makeMap(eventParts);
    }


    public void setParts(Map<String,EventPart> eventParts) {
        this.eventParts = eventParts;
    }

    /**
     * Compare this event with another to define a total order that is consistent with timestamps and (hopefully)
     * causal order.
     *
     * Implements the compareTo<Event> method of Comparable, that is returns -1, 0, 1 if this event is before, equal
     * or after.
     */
    public int compareTo(Event other) {
        if (this.equals(other)) {
            // they're equal, return 0
            return 0; 
        } else {
            // check if they are strictly ordered
            int order = this.order(other);
            if (order == 0) {
                // concurrent, distinguish by other means
                if (Activity.class.isInstance(this)) {
                    logger.debug("Using activity/activity order for compareTo");
                    // If we're an activity, return the result of the activity specific method
                    return((Activity) this).compareTo(other);
                } else if (Activity.class.isInstance(other)) {
                    logger.debug("Using event/activity order for compareTo");
                    // If the other is an activity, return the negation of the activity specific method
                    return(-((Activity) other).compareTo(this));
                } else {
                    // it is possible that timestamps are not equal (if we're allowing for clock skew)
                    long thisTime = this.getHeader().getTimestamp().getTime();
                    long otherTime = other.getHeader().getTimestamp().getTime();
                    if (thisTime == otherTime) {
                        order = sequenceOrder(other);
                        if (order == 0) {
                            // sequence numbers don't help, so use ID order
                            logger.debug("Using ID order for compareTo");
                            return header.getEventId().compareTo(other.getHeader().getEventId());
                        } else {
                            // these events are sequenced, so use the sequence order
                            logger.debug("Using sequence order for compareTo");
                            return order;
                        }
                    } else {
                        // different timestamps, so use their order
                        logger.debug("Using timestamp order for compareTo");
                        return thisTime < otherTime ? -1 : 1;
                    }
                }
            } else {
                // not concurrent, so return order
                logger.debug("Using result of concurrency order() for compareTo");
                return(order);
            }
        }
    }

    /**
     * Implements compareTo based on sequence numbers
     *
     * @param other Other event to compare with
     * @return -1, 0, 1 if this sequence number is before, same or after the other sequence number
     */
    private int sequenceOrder(Event other) {
        int mySeq = header.getSequenceNumber();
        int otherSeq = other.getHeader().getSequenceNumber();
        if (mySeq < otherSeq) return -1;
        if (mySeq == otherSeq) return 0;
        return 1;
    }


    /**
     * Method to convert a Set into a Map using position in set as key.
     * 
     * Used for creating default Map instances when deprecated methods are used.
     * 
     * @param parts
     * @return
     */
    private static Map<String,EventPart> makeMap(Set<EventPart> parts) {
        if (parts == null) { return null; }
        Map<String, EventPart> map = new HashMap<String,EventPart>(parts.size());
        Iterator<EventPart> iter = parts.iterator();
        for (int i = 0; iter.hasNext(); i++) {
            map.put(Integer.toString(i), iter.next());
        }
        return map;
    }
}
