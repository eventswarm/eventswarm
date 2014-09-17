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
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * An extension to the DiscreteTimeWindow class that forces the time window to shift even when events
 * do not arrive.
 *
 * The normal DiscreteTimeWindow shifts the window when new events arrive. This can be problematic for
 * low-rate event flows, particularly when using expressions that fire on event removal. This class adds a
 * clock source as a fallback, shifting the window using the clock time less a latency allowance whenever
 * a clock tick is received. The latency allowance avoids issues arising from events that are delayed or
 * delivered in batches.
 *
 * Note that the time window is still moved in response to event arrival as well.
 *
 * This class uses a SystemClockTickTrigger (TimerTask) unless you use an external clock source.
 * It is preferable to explicitly stop the TimerTask using the stop method when you are finished with it.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ClockedDiscreteTimeWindow extends DiscreteTimeWindow implements TickAction {
    SystemClockTickTrigger ticker;
    int latency;
    int tickInterval;

    private static Logger logger = Logger.getLogger(ClockedDiscreteTimeWindow.class);

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
    public ClockedDiscreteTimeWindow(long windowSize, int latency, int tickInterval) {
        super(windowSize);
        setup(latency, tickInterval);
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
    public ClockedDiscreteTimeWindow(Interval interval, int latency, int tickInterval) {
        super(interval);
        setup(latency, tickInterval);
    }

    private void setup(int latency, int tickInterval) {
        this.latency = latency;
        this.tickInterval = tickInterval;
        if (tickInterval > 0) {
            logger.info("Creating a system clock tick source");
            ticker = new SystemClockTickTrigger(tickInterval);
            ticker.registerAction(this);
        }
    }

    /**
     * Remove all events from the window that are less than (time - this.latency)
     *
     * @param trigger Source of the tick trigger
     * @param time Time associated with the tick
     */
    @Override
    public void execute(TickTrigger trigger, Date time) {
        // remove all events from the window that are before (time - latency)
        this.lock.writeLock().lock();
        try {
            logger.debug("Adjusting time window in response to clock tick");
            this.adjustTimeWindow(time.getTime() - latency);
            this.fire(); // fire the window change trigger, in case anyone is listening
        } finally {
            this.lock.writeLock().unlock();
        }
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

    @Override
    protected void finalize() throws Throwable {
        stop();
        super.finalize();
    }
}
