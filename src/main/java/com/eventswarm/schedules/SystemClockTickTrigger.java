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
package com.eventswarm.schedules;

import com.eventswarm.util.Interval;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Generate clock ticks using the system clock
 *
 * Considering the ability to deal with arbitrary sized intervals, this class could be used as a simple scheduler but
 * would require a separate instance for each schedule. Where more than one schedule or more complex scheduling
 * is required, connect this to a TickScheduler instance.
 *
 * Note that this class is based on the java.util.timer class and thus does not offer any real-time guarantees.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * Date: 24/06/13
 * Time: 1:17 PM
 *
 */
public class SystemClockTickTrigger extends TimerTask implements TickTrigger {
    private static Calendar calendar = Calendar.getInstance();
    private long interval;
    private Timer timer;
    private Set<TickAction> actions;
    boolean started = false;

    private static Logger logger = Logger.getLogger(SystemClockTickTrigger.class);

    /**
     * Create tick trigger that ticks every millis milliseconds, beginning one interval from the current time
     *
     * @param millis
     */
    public SystemClockTickTrigger(long millis) {
        this.interval = millis;
        this.timer = new Timer();
        actions = new HashSet<TickAction>();
        logger.debug("Establishing system clock tick trigger to tick every " + Long.toString(interval) + " milliseconds");
        timer.scheduleAtFixedRate(this, interval, interval);
    }

    /**
     * Create a tick trigger that ticks every second, beginning one second from the current time
     */
    public SystemClockTickTrigger() {
        this(1000L);
    }

    /**
     * Create tick trigger that ticks every interval, beginning one interval from the current time
     *
     * @param interval
     */
    public SystemClockTickTrigger(Interval interval) {
        this(interval.getIntervalMillis());
    }

    /**
     * Stop this tick trigger: can't be restarted after this
     */
    public void stop() {
        timer.cancel();
    }

    /**
     * Register new tick actions
     *
     * @param action Action to be executed when trigger fires.
     */
    @Override
    public void registerAction(TickAction action) {
        actions.add(action);
    }

    /**
     * Remove a tick action
     *
     * @param action Action to be removed from registered list.
     */
    @Override
    public void unregisterAction(TickAction action) {
        actions.remove(action);
    }

    /**
     * Notify
     */
    @Override
    public void run() {
        Date now = new Date();
        for(TickAction action : actions) {
            action.execute(this, now);
        }
    }
}
