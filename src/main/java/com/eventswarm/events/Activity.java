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
package com.eventswarm.events;

import java.util.Date;
import java.util.SortedSet;

/**
 * An Activity is a non-atomic behaviour comprising multiple events.
 *
 * An activity is represented as a set of events. Implementations and subclasses might apply additional
 * structure to reflect their purpose (e.g. sequencing, graphs, complex expression matching).
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface Activity extends Event {
    /**
     * Returns the start date/time of the activity, which is always equal to the timestamp of the
     * first event in the activity.
     *
     * @return first().header().getTimeStamp()
     */
    public Date getStart();

    /**
     * Returns the end date/time of the activity, which is always equal to the end time of the last event in the
     * activity, or the timestamp of the last event if it is not an activity.
     *
     * @return if (Activity.class.isInstance(last()) last().getEnd() else last.header().getTimestamp()
     */
    public Date getEnd();

    /**
     * Return the sorted set of events that constitutes this activity
     *
     * @return sorted set of events
     */
    public SortedSet<Event> getEvents();

    /**
     * Return the first event in the activity, ordered as defined by Event.compareTo()
     */
    public Event first();

    /**
     * Return the last event in the activity, ordered as defined by Event.compareTo()
     */
    public Event last();

    /**
     * Return true if the activity contains the specified event
     */
    public boolean contains(Event event);
}
