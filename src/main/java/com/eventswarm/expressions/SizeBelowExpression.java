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

import com.eventswarm.*;
import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;

/**
 * Class to monitor the number of events in a nominated EventSet and generate a match whenever the number of
 * events in the EventSet is below a threshold.
 *
 * This expression matches on event removals, and the event accompanying the EventMatchTrigger used to signal
 * downstream listeners is the event that was removed from the window. Listening on removals also implies that
 * it cannot match until at least one event is removed from the set. Thus for sliding time windows, it
 * won't match until the window has filled (i.e. the window has been receiving events for longer than the window period).
 *
 * The class is intended for primarily for monitoring the rate of arrival of events, and thus is best connected
 * to a DiscreteTimeWindow or ClockedDiscreteTimeWindow.
 *
 * Note that since the EventSet to be monitored is passed into the constructor, this expression should not be
 * explicitly connected to any add or remove triggers.
 *
 * Also note that since this instance registers itself against the identified eventset, users must ensure they
 * call 'unregister' to ensure this expression can be removed if the eventset itself is not being removed.
 *
 * @see com.eventswarm.eventset.ClockedDiscreteTimeWindow
 * @see com.eventswarm.eventset.DiscreteTimeWindow
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SizeBelowExpression extends AbstractEventExpression {
    private int threshold;
    private EventSet events;

    /**
     * Create a new SizeBelowExpression using a window of the specified Interval and the nominated threshold.
     *
     * @see com.eventswarm.util.Interval
     *
     * @param events EventSet whose size is monitored
     * @param threshold Threshold for alerting (i.e. alert when the window size is < than this)
     */
    public SizeBelowExpression(int threshold, EventSet events) {
        this.threshold = threshold;
        this.events = events;
        events.registerAction((RemoveEventAction)this);
        events.registerAction((AddEventAction)this);
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        // right now, we don't do anything
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        if (isTrue()) {
            super.matches.execute((AddEventTrigger) null, event);
            fire(event);
        }
    }

    @Override
    public boolean isTrue() {
        return events.size() < threshold;
    }

    /**
     * Unregister this expression from the eventset so that this instance can be garbage collected
     */
    public void unregister() {
        events.unregisterAction((RemoveEventAction)this);
        events.unregisterAction((AddEventAction)this);
    }

    public int getThreshold() {
        return threshold;
    }

    /**
     * Change the threshold at which this expression will fire
     *
     * @param threshold
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
