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
package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoActivity;
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Expression that if there are any events in an eventset that match a new event according to the supplied Comparator,
 * generating an activity event containing the new event and its duplicates.
 *
 * This Expression excludes the supplied event from matching (i.e. it won't match against itself) so the tested
 * event can already be in the eventset.
 *
 * Since this expression generates new activity events for each match, the RemoveEventTrigger will be ignored and
 * the 'hasMatched' method can only return true for one of the activity events, not a source event.
 *
 * Caution: this expression is likely to be processor intensive and slow against large EventSet instances.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class DuplicateEventExpression extends AbstractEventExpression implements Expression {
    private Comparator comparator;
    private EventSet events;

    private static Logger logger = Logger.getLogger(DuplicateEventExpression.class);

    /**
     * Create expression with supplied comparator and EventSet
     *
     * @param comparator
     * @param events
     */
    public DuplicateEventExpression(Comparator comparator, EventSet events) {
        this.comparator = comparator;
        this.events = events;
    }

    public DuplicateEventExpression(int limit, Comparator comparator, EventSet events) {
        super(limit);
        this.comparator = comparator;
        this.events = events;
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        SortedSet<Event>  result = new TreeSet<Event>();
        for (Event compareTo: events) {
            logger.debug("Comparing");
            if (event != compareTo && comparator.matches(event, compareTo)) {
                logger.debug("Have match");
                result.add(compareTo);
            }
        }
        if (result.size() > 0) {
            result.add(event); // include the current event, so activity contains the full set of matching events
            Activity activity = new JdoActivity(result);
            this.matches.execute(trigger,activity);
            fire(activity);
        }
    }

    public Comparator getComparator() {
        return comparator;
    }

    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    public EventSet getEvents() {
        return events;
    }

    public void setEvents(EventSet events) {
        this.events = events;
    }
}
