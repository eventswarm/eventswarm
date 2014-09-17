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
package com.eventswarm.events.jdo;

import com.eventswarm.events.*;

import java.util.*;

/**
 * Implementation of the Activity (complex event) interface suitable for database storage.
 *
 * Uses a unmodifiable view of the underlying set, so attempts to modify the set will throw an
 * UnsupportedOperationException.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoActivity extends JdoEvent implements Activity {

    public static String EVENTS="events";
    protected transient SortedSet<Event> events = null; // keep local reference for speed and convenience

    public JdoActivity(SortedSet<Event> events) {
        super();
        Map<String, EventPart> parts = new HashMap<String, EventPart>();
        parts.put(EVENTS, new JdoNestedEvents(Collections.unmodifiableSortedSet(events)));
        this.setParts(parts);
        // Use the timestamp and source of the last event
        // Set sequence number to -1 because we distinguish activities using their component events so this is not used
        Header header = new JdoHeader(events.last().getHeader().getTimestamp(), -1, events.last().getHeader().getSource());
        this.setHeader(header);
    }

    /**
     * Hide other constructors
     */
    protected JdoActivity() {
        super();
    }

    protected JdoActivity(Header header, Map<String,EventPart> eventParts) {
        this.header = header;
        this.setParts(eventParts);
    }


    /**
     * Order an atomic Event against this Activity
     *
     * An event is before an activity if it's timestamp is before the start of the activity.
     * An event is after an activity is it's timestamp is after the end of the activity.
     * An event is concurrent if it's timestamp is within the duration of the activity.
     *
     * If a non-atomic event is passed, this method will call the alternate Activity-specific ordering method
     *
     * TODO: make this causal rather than based on timestamps
     *
     * @param event
     * @return
     */
    public int order(Event event) {
        if (Activity.class.isInstance(event)) {
            // throw to the Activity implementation if not an atomic event
            return this.order((Activity) event);
        } else if (event.getHeader().getTimestamp().before(this.getStart())) {
            return -1;
        } else if (event.getHeader().getTimestamp().after(this.getEnd())) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Order another Activity against this Activity
     *
     * An activity is before another if its end time is before the start time of the other.
     * An activity is after another if its start time is after the end time of the other.
     * In all other cases, the two activities are concurrent (i.e. their durations overlap)
     *
     * TODO: make this causal rather than based on timestamps, very complex
     */
    public int order(Activity activity) {
        if (activity.getEnd().before(this.getStart())) {
            return -1;
        } else if (activity.getStart().after(this.getEnd())) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Provide an alternate compareTo method that allows us to define a total order over activities and events
     *
     * If they're strictly ordered, return the order
     * If they're concurrent and both activities, use the activity-specific comparison
     * Otherwise, order against the first event in the activity
     */
    @Override
    public int compareTo (Event event) {
        if (Activity.class.isInstance(event)) {
            // Use specific activity comparison if required
            return this.compareTo((Activity) event);
        } else {
            int order = this.order(event);
            if (order != 0) {
                return order;
            } else {
                // otherwise, return the result of comparing the first event in the activity, or put the event first
                order = this.first().compareTo(event);
                if (order != 0) {
                    return order;
                } else {
                    return -1;
                }
            }
        }
    }

    /**
     * Provide a compareTo method specifically for comparing two activities
     *
     * If they're equal, return 0
     * If they're strictly ordered, return the order
     * If they're concurrent, order by end time
     * If end times are equal, order by first differing event timestamp
     * If event times are all equal, return the ordering of the last event in each set
     */
    public int compareTo (Activity other) {
        if (this == other) {
            return 0;
        } else {
            int order = this.order(other);
            if (order != 0) {
                return order;
            } else {
                order = this.getEnd().compareTo(other.getEnd());
                if (order != 0) {
                    return order;
                } else {
                    // step through in order to identify differing timestamps
                    Iterator<Event> us = this.events.iterator();
                    Iterator<Event> them = other.getEvents().iterator();
                    while (us.hasNext() && them.hasNext()) {
                        order = us.next().getHeader().getTimestamp().compareTo(them.next().getHeader().getTimestamp());
                        if (order != 0) {
                            return order;
                        }
                    }
                    // if we get here, just return the ordering of last events
                    return(this.last().compareTo(other.last()));
                }
            }
        }
    }

    /**
     * When the parts are set, capture the set of events in a transient instance var for convenience.
     *
     * Descendant classes that do not directly hold an EVENTS part must override this method and set the
     * transient events field to hold the set of events that make up this activity before calling this
     * superclass method.
     *
     * @param eventParts
     */
    @Override
    public void setParts(Map<String, EventPart> eventParts) {
        super.setParts(eventParts);
        if (this.hasPart(EVENTS)) {
            this.events = ((JdoNestedEvents)this.getPart((EVENTS))).getEvents();
        } else {
            if (this.events == null) throw new NullPointerException("An activity must have a set of events");
        }
    }

    /**
     * Provide convenience method to return the set of events
     *
     * @return
     */
    public SortedSet<Event> getEvents() {
        return this.events;
    }

    @Override
    public Date getStart() {
        return events.first().getHeader().getTimestamp();
    }

    @Override
    public Date getEnd() {
        Event last = this.last();
        if (Activity.class.isInstance(last)) {
            return ((Activity)last).getEnd();
        } else {
            return last.getHeader().getTimestamp();
        }
    }

    @Override
    public Event first() {
        return events.first();
    }

    @Override
    public Event last() {
        return events.last();
    }
}
