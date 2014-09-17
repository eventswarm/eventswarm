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
package com.eventswarm.util.logging;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.schedules.TickAction;
import com.eventswarm.schedules.TickTrigger;
import org.apache.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Date;

/**
 * Class to monitor memory usage, generating log messages both periodically (at INFO level) or according to thresholds
 * (at WARN or ERROR level)
 *
 *
 * This class is intended to be paired with an EveryNFilter or a scheduler to cause periodic log messages for memory
 * usage. Typically, it is better to pair with an EveryNFilter since memory usage is typically tied to event consumption.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MemoryMonitor implements AddEventAction, TickAction {
    private static Logger logger = Logger.getLogger(MemoryMonitor.class);
    /**
     * Array of thresholds being monitored, expressed as a whole number percentage of heap memory that is in-use, e.g.
     * a threshold of 70 means that a warning message will be generated when 70% of the available heap memory is in-use.
     */
    private int thresholds[];
    private int lastThreshold = -1;
    private long BYTES_PER_MB = 1000000;
    private static int DEFAULT_THRESHOLDS[] = {50, 70, 90};

    /**
     * Create a memory monitor that generates periodic log message for heap memory usage at INFO level and WARN level
     * messages for each identified threshold except the last, which generates an ERROR level log messages. If
     * thresholds is empty, then the default thresholds are used {50, 70, 90}
     *
     * @param thresholds Array of whole number percentages at which a WARN or ERROR (last) should be generated
     */
    public MemoryMonitor(int thresholds[]) {
        if (thresholds == null || thresholds.length == 0)
            this.thresholds = DEFAULT_THRESHOLDS;
        else {
            this.thresholds = thresholds;
        }
    }

    /**
     * Create a memory monitor using the default thresholds
     */
    public MemoryMonitor() {
        this(DEFAULT_THRESHOLDS);
    }


    @Override
    public void execute(TickTrigger trigger, Date time) {
        logMemoryUsage(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        logMemoryUsage(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage());
    }

    public void logMemoryUsage(MemoryUsage usage) {
        if (aboveHigherThreshold(usage)) {
            if (lastThreshold == thresholds.length-1) {
                logger.error(infoMemoryUsage(usage));
            } else {
                logger.warn(infoMemoryUsage(usage));
            }
        } else {
            logger.info(infoMemoryUsage(usage));
        }
    }

    /**
     * Checks to see if the percentage is above a higher-than-previous threshold, updating the lastThreshold to reflect
     * the highest threshold reached.
     *
     * In effect, this method will return true at most once for each threshold. Sorry about the side-effect ...
     *
     * @param usage current memory usage object
     *
     * @return true if a higher-than-previous threshold has been reached
     */
    private boolean aboveHigherThreshold(MemoryUsage usage) {
        int percentage = getPercentage(usage);
        int prev = lastThreshold;
        while ((lastThreshold+1) < thresholds.length && percentage > thresholds[lastThreshold+1]) {
            lastThreshold++;
        }
        return (lastThreshold > prev);
    }

    public int getPercentage(MemoryUsage usage) {
        return (int) ((double)usage.getUsed()/ (double)usage.getMax() * 100.0);
    }

    private String infoMemoryUsage(MemoryUsage usage) {
        return "Have used " + getPercentage(usage) + "% of available memory" +
                " (" + Long.toString(usage.getUsed()/BYTES_PER_MB) + " of " + Long.toString(usage.getMax()/BYTES_PER_MB) + " MB)";
    }
}
