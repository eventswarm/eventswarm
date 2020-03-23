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
package com.eventswarm.util;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.SizeThresholdAction;
import com.eventswarm.SizeThresholdTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.JdoActivity;
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.NavigableSet;

/**
 * AddEventAction to execute when a SizeThresholdAction occurs, passing through either a singleton event
 * or a bounded Activity containing the last N events (N == threshold || N == max) from the upstream trigger source.
 *
 * Note that if the upstream source is not an eventset (i.e. a passthru), a singleton event will always be returned.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ThresholdMatchAddAction implements SizeThresholdAction, AddEventTrigger {
    private int max;
    private transient EventTriggerDelegate<AddEventTrigger, AddEventAction> delegate;

    private static final int DEFAULT_MAX = 10;
    private static final Logger logger = Logger.getLogger(ThresholdMatchAddAction.class);


    public ThresholdMatchAddAction(int max) {
        this.max = max;
        this.delegate = new EventTriggerDelegate<AddEventTrigger, AddEventAction>(this);
    }

    public ThresholdMatchAddAction() {
        this(DEFAULT_MAX);
    }

    public void execute(SizeThresholdTrigger trigger, Event event, long size) {
        if (size == 1 || trigger == null || trigger.getSource() == null || !(EventSet.class.isInstance(trigger.getSource()))) {
            logger.debug("No upstream eventset or threshold 1, sending supplied event");
            fire(event);
        } else {
            logger.debug("Upstream eventset detected, sending set");
            NavigableSet<Event> set = ((EventSet) trigger.getSource()).getEventSet();
            if (set.size() < size) {
                logger.warn("EventSet size (" + Integer.toString(set.size()) +
                            ") is less than threshold (" + Long.toString(size) + ")");
            }
            if (set.isEmpty()) {
                // send nothing
                logger.warn("Size threshold trigger fired on empty eventset");
            } else if (set.size() == 1) {
                logger.debug("Single event in set, sending just that one");
                fire(event);
            } else if (set.size() <= max) {
                // set size is smaller than the max, fire activity containing all
                logger.debug("Below limit, sending all in set");
                fire(new JdoActivity(set));
            } else {
                // set size is larger than max, get tail with length == max
                logger.debug("Above limit, sending up to limit");
                Iterator<Event> iter = set.descendingIterator();
                Event match = set.last();
                for(int i = 0; i < max && iter.hasNext(); i++) {
                    match = iter.next();
                }
                fire(new JdoActivity(set.tailSet(match, true)));
            }
        }
    }

    public void registerAction(AddEventAction action) {
        delegate.registerAction(action);
    }

    public void unregisterAction(AddEventAction action) {
        delegate.unregisterAction(action);
    }

    public void fire(Event event) {
        delegate.fire(event);
    }
}
