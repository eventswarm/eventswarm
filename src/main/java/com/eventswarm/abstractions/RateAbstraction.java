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
package com.eventswarm.abstractions;

import com.eventswarm.*;
import com.eventswarm.events.Event;
import com.eventswarm.events.Header;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.events.jdo.OrgJsonEvent;
import com.eventswarm.eventset.*;
import com.eventswarm.expressions.Value;
import com.eventswarm.util.Interval;
import com.eventswarm.util.IntervalUnit;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Keep track of the rate of events arriving and generate rate events for downstream components wanting to
 * act on rate thresholds etc.
 * 
 * This class uses a ClockedDiscreteTimeWindow operating off the internal system clock. The window sampling frequency
 * is controlled by the 'tickInterval' parameter. Rate events are generated for a threshold number of ticks.
 *
 * The rate will be zero (undefined) and no rate events will be generated until the time window has been filled.
 * 
 * Generated RateEvents will contain the current rate of events per second.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class RateAbstraction implements AddEventAction, WindowChangeAction, AddEventTrigger, Value<Number> {

    /**
     * Uses a discrete time window to measure the rate
     */
    private ClockedTimeWindow rateWindow;

    /**
     * Default measurement window is 1 minute
     */
    public static final Interval DEFAULT_WINDOW = new Interval(1L, IntervalUnit.MINUTES);

    /**
     * Default tickInterval is 1s (1000ms)
     */
    public static final int DEFAULT_TICK_INTERVAL = 1000;

    /**
     * Counts window shifts in the underlying window, providing a basis for generating rate events at the threshold
     */
    private int shifts;

    /**
     * Generate a rate event after every threshold window shifts
     */
    private long threshold;

    /**
     * maintain most recent calculated rate as the 'current' rate: calculating on the fly is inaccurate
     */
    private double rate;

    /**
     * Default is to generate a rate event for every ten source events
     */
    public static final int DEFAULT_THRESHOLD = 10;

    private double windowSize; // hold size of window in milliseconds, as double so we don't have to cast all the time

    private Set<AddEventAction> actions;

    private static Logger logger = Logger.getLogger(RateAbstraction.class);


    /**
     * Create a RateAbstraction using the default measurement window and generating rate events using the default threshold
     */
    public RateAbstraction() {
        setup(DEFAULT_WINDOW, DEFAULT_TICK_INTERVAL, DEFAULT_THRESHOLD);
    }

    /**
     * Create a RateAbstraction using specified measurement interval (window size) and event generation threshold
     *
     * @param interval the measurement window (e.g. 5 minutes)
     * @param tickInterval time between window shifts in milliseconds (e.g. 100ms)
     * @param threshold how many shifts between each rate event (e.g. report rate every 10 shifts)
     *                  if <= 0 then no rate events will be generated
     */
    public RateAbstraction(Interval interval, int tickInterval, int threshold) {
        setup(interval, tickInterval, threshold);
    }

    /**
     * Create a RateAbstraction using specified measurement interval in seconds and event generation threshold
     *
     * @param seconds the measurement window in seconds
     * @param tickInterval how often to 'shift' the window in milliseconds
     * @param threshold how many shifts between each rate event (e.g. report rate every 10 shifts)
     *                  if <= 0 then no rate events will be generated
     */
    public RateAbstraction(long seconds, int tickInterval, int threshold) {
        setup(new Interval(seconds, IntervalUnit.SECONDS), tickInterval, threshold);
    }

    /**
     * Setup all our variables consistently for all constructors
     *
     * @param interval
     * @param tickInterval
     * @param threshold
     */
    private void setup(Interval interval, int tickInterval, int threshold) {
        this.rateWindow = new ClockedTimeWindow(interval, 0, tickInterval);
        this.rateWindow.registerAction((WindowChangeAction) this); // listen for window change events
        this.threshold = (long) threshold;
        this.shifts = 0;
        this.rate = 0.0;
        this.actions = new HashSet<AddEventAction>();
        this.windowSize = (double) interval.getIntervalMillis();
    }

    /**
     * Add to rate window
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        rateWindow.execute(trigger, event);
    }

    /**
     *
     * @param trigger
     * @param set
     */
    @Override
    public void execute(WindowChangeTrigger trigger, EventSet set) {
        logger.debug("Executing window change trigger");
        shifts++;
        calcRate();
        if (threshold > 0 && shifts%threshold == 0) {
            fire();
            shifts = 0;
        }
    }

    @Override
    public void registerAction(AddEventAction action) {
        actions.add(action);
    }

    @Override
    public void unregisterAction(AddEventAction action) {
        actions.remove(action);
    }

    private void calcRate() {
        rate = ((double) rateWindow.size() * (double) IntervalUnit.MILLISPERSECOND)/windowSize;
    }

    /**
     * Return the most recently sampled event rate in events per second
     *
     * Note that the method returns the rate associated with the most recent sample: returning a rate calculated
     * from the current size of the window would be inaccurate because the actual time window will be windowSize + X
     * where X is the time since the last sample.
     *
     * @return
     */
    public Double getRate() {
        return rate;
    }

    /**
     * Implement the Value method so we can reference the rate in expressions
     *
     * @return
     */
    @Override
    public Number getValue() {
        return getRate();
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize(); //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Stop the system timer task associated with the underlying rate window and unregister us from it
     *
     * Note that the instance cannot be restarted.
     */
    protected void stop() {
        rateWindow.unregisterAction((WindowChangeAction) this);
        rateWindow.stop();
    }

    private void fire() {
        logger.debug("Firing new RateEvent");
        RateEvent event = new RateEvent(getRate());
        for (AddEventAction action: actions) {
            logger.debug("Calling action " + action.toString());
            action.execute(this, event);
        }
    }

    /**
     * Simple RateEvent based on OrgJsonEvent
     */
    private static class RateEvent extends OrgJsonEvent implements com.eventswarm.events.RateEvent {

        private static final String RATE_KEY = "rate";

        public RateEvent(Double rate) {
            super(makeHeader(), makeJSON(rate));
        }

        @Override
        public Double getRate() {
            return getDouble(RATE_KEY);  //To change body of implemented methods use File | Settings | File Templates.
        }

        private static Header makeHeader() {
            return new JdoHeader(new Date(), JdoSource.getLocalSource());
        }

        private static JSONObject makeJSON (Double rate) {
            return (new JSONObject()).put(RATE_KEY, rate);
        }
    }
}
