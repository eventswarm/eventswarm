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
import com.eventswarm.eventset.EventSet;
import org.apache.log4j.Logger;

/**
 * Class to monitor the size of an eventset or window for logging purposes, generating a log event whenever
 * the size of the upstream eventset is a multiple of the specified size (e.g. 1000, 2000, 3000 if size is 1000).
 *
 * A pair of escalation points can also be specified to increase the default (INFO) level of log messages.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SizeMonitor implements AddEventAction {
    private static Logger logger = Logger.getLogger(SizeMonitor.class);

    private EventSet es;
    private int size;
    private String name;
    private int lastMultiple;
    private int warnAt = 0;
    private int errorAt = 0;

    /**
     * Create a size monitor that generates log message whenever the size is a multiple of the specified size and
     * increasing.
     *
     * @param es EventSet or window to monitor
     * @param size generate log events whenever the upstream eventset reaches this size or a multiple of this size
     * @param name Includes this name in all log messages so that the EventSet can be distinguished from others
     */
    public SizeMonitor(EventSet es, int size, String name) {
        this(es, size, name, 0, 0);
    }

    /**
     * Create a size monitor that generates log message whenever the size is a multiple of the specified size and
     * increasing.
     *
     * @param es EventSet or window to monitor
     * @param size generate log events whenever the upstream eventset reaches this size or a multiple of this size
     * @param name Includes this name in all log messages so that the EventSet can be distinguished from others
     * @param warnAt Increase the logging level to WARN once the size reaches this value
     * @param errorAt Increase the logging level to ERROR once the size reaches this value
     */
    public SizeMonitor(EventSet es, int size, String name, int warnAt, int errorAt) {
        this.es = es;
        this.size = size;
        this.name = name;
        this.warnAt = warnAt;
        this.errorAt = errorAt;
        es.registerAction(this);
    }

    /**
     * Log a message if we've hit a new, higher multiple, escalating if we've exceeded warning or error thresholds
     *
     * @param trigger
     * @param event
     */
    public void execute(AddEventTrigger trigger, Event event) {
        int currentSize = es.size();
        if (currentSize % size == 0 && currentSize > lastMultiple) {
            lastMultiple = currentSize;
            if (errorAt > 0 && currentSize >= errorAt) {
                logger.error(message(currentSize));
            } else if (warnAt > 0 && currentSize >= warnAt){
                logger.warn(message(currentSize));
            } else {
                logger.info(message(currentSize));
        }
        }
    }

    private String message(int size) {
        return "Eventset '" + name + "' now contains " + Integer.toString(size) + " events";
    }
}
