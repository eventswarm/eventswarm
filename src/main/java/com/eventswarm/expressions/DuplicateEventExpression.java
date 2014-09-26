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
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoActivity;
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Expression that if there are any preceding events that match a new event according to the supplied Comparator,
 * generating an activity event containing the new event and its duplicates.
 *
 * This Expression excludes the supplied event from matching (i.e. it won't match against itself) so the tested
 * event can be a duplicate. This class implements RemoveEventAction by removing the identified event from the
 * (internal) EventSet that is being monitored.
 *
 * Since this expression generates new activity events for each match the 'hasMatched' method can only return
 * true for one of the activity events, not a source event.
 *
 * Caution: this expression is likely to be processor intensive and slow against large EventSet instances because
 * it compares each new event with all preceding events. You should use upstream filters and powersets to minimise
 * the number of preceding events held.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class DuplicateEventExpression extends AbstractEventExpression implements Expression {
    private Comparator comparator;
    private EventSet events;

    private static Logger logger = Logger.getLogger(DuplicateEventExpression.class);

    /**
     * Create expression with supplied comparator creating an internal eventset to hold incoming
     * events.
     *
     * To ensure this eventset is bounded, this component should be connected to both an AddEventTrigger and
     * a RemoveEventTrigger associated with a bounded time or other window.
     *
     * @param comparator
     */
    public DuplicateEventExpression(Comparator comparator) {
        this.comparator = comparator;
        this.events = new EventSet();
    }

    /**
     * Create expression with supplied comparator and EventSet and match limit
     *
     * @param limit Maximum number of matches to hold
     * @param comparator
     */
    public DuplicateEventExpression(int limit, Comparator comparator) {
        super(limit);
        this.comparator = comparator;
        this.events = new EventSet();
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        this.events.execute(trigger, event);
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

    /**
     * Remove the supplied event from the monitored eventset
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        this.events.execute(trigger, event);
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
