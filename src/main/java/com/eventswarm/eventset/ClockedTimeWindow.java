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
package com.eventswarm.eventset;

import com.eventswarm.schedules.SystemClockTickTrigger;
import com.eventswarm.schedules.TickAction;
import com.eventswarm.schedules.TickTrigger;
import com.eventswarm.util.Interval;
import com.eventswarm.util.IntervalUnit;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * A time window that shifts using an external or system clock tick source, with an allowance for latency.
 *
 * The normal DiscreteTimeWindow shifts the window when new events arrive. This can be problematic for
 * low-rate event flows, particularly when using expressions that fire on event removal. This class uses a
 * tick source instead, shifting the window using the tick time less a latency allowance whenever
 * a tick is received. The latency allowance avoids issues arising from events that are delayed or
 * delivered in batches.
 *
 * A default tick source using the system clock is used if a non-zero tickInterval is specified.
 *
 * This class uses a SystemClockTickTrigger (TimerTask) unless you use an external clock source.
 * It is preferable to explicitly stop the TimerTask using the stop method when you are finished with it.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ClockedTimeWindow extends AbstractTimeWindow implements TickAction {
    SystemClockTickTrigger ticker;
    int latency;
    int tickInterval;
    long filled;
    Interval interval;

    private static Logger logger = Logger.getLogger(ClockedTimeWindow.class);

    /**
     * Create a ClockedDiscreteTimeWindow of windowSize seconds, backdating ticks by latency milliseconds to
     * allow for event arrival latencies.
     *
     * If a tickInterval > 0 is specified, an internal tick source based on the system clock is created. Otherwise,
     * the caller must connect this instance to a suitable tick source to force window shifts.
     *
     * @param windowSize Window size in seconds
     * @param latency Latency allowance in milliseconds
     * @param tickInterval Interval between clock ticks in milliseconds, if zero, no tick source is configured
     */
    public ClockedTimeWindow(long windowSize, int latency, int tickInterval) {
        super(windowSize);
        setup(windowSize*IntervalUnit.MILLISPERSECOND, latency, tickInterval);
    }

    /**
     * Create a ClockedDiscreteTimeWindow of windowSize seconds, backdating ticks by latency milliseconds to
     * allow for event arrival latencies.
     *
     * If a tickInterval > 0 is specified, an internal tick source based on the system clock is created. Otherwise,
     * the caller must connect this class to a suitable tick source.
     *
     * @param interval Window size as a com.eventswarm.util.Interval object
     * @param latency Latency allowance in milliseconds
     * @param tickInterval Interval between clock ticks in milliseconds, if zero, no tick source is configured
     */
    public ClockedTimeWindow(Interval interval, int latency, int tickInterval) {
        super(interval);
        setup(interval.getIntervalMillis(), latency, tickInterval);
    }

    protected void init() {

    }

    private void setup(long sizeInMillis, int latency, int tickInterval) {
        this.latency = latency;
        this.tickInterval = tickInterval;
        this.filled = (new Date()).getTime() + sizeInMillis; // remember time for first window shift
        if (tickInterval > 0) {
            logger.info("Creating a system clock tick source");
            ticker = new SystemClockTickTrigger(tickInterval);
            ticker.registerAction(this);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Since this class indirectly starts a timer thread, make sure we can stop it
     */
    public void stop() {
        if (ticker != null) {
            ticker.unregisterAction(this);
            ticker.stop();
        }
    }

    /**
     * Remove all events from the window that are less than (time - this.latency)
     *
     * @param trigger Source of the tick trigger
     * @param time Time associated with the tick
     */
    public void execute(TickTrigger trigger, Date time) {
        // remove all events from the window that are before (time - latency)
        this.lock.writeLock().lock();
        try {
            // update filling flag
            if (isFilling()) {
                if (time.getTime() >= filled) {
                    logger.debug("Window is now 'filled', clearing filling flag");
                    this.setFilled();
                } else {
                    logger.debug("Window is not filled yet, ignoring");
                }
            }
            if (!isFilling()) {
                // only move the window if we've filled
                logger.debug("Adjusting time window in response to clock tick");
                this.adjustTimeWindow(time.getTime() - latency);
                this.fire(); // fire the window change trigger, in case anyone is listening
            }
        } finally {
            this.lock.writeLock().unlock();
        }
    }
}
