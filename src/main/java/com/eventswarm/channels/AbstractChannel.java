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
package com.eventswarm.channels;

import au.com.bytecode.opencsv.CSVReader;
import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.CSVEvent;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for channels that implements AddEventTrigger and maintains counts etc.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public abstract class AbstractChannel implements AddEventTrigger {
    // default logger
    protected static Logger log = Logger.getLogger(AbstractChannel.class);
    protected InputStream istr;
    protected int errorCount = 0;
    protected long count = 0;
    protected Exception exception = null;
    private Set<AddEventAction> actions = null;
    protected boolean stop = false;

    public AbstractChannel() {
        super();
        this.actions = new HashSet<AddEventAction>();
    }

    public void registerAction(AddEventAction action) {
        this.actions.add(action);
    }

    public void unregisterAction(AddEventAction action) {
        this.actions.remove(action);
    }

    /**
     * Start processing the input file, calling registered actions for each new
     * event.
     *
     * @return Returns the number of events processed
     */
    public long process() throws IOException {
        this.stop = false;
        try {
            setup();
        } catch (Exception exc) {
            log.error("Error initialising channel", exc);
            stop();
        }
        while (!stop) {
            try {
                Event event = next();
                if (!stop) {
                    fire(event);
                    count += 1;
                    if (count%10000 == 0) {
                        log.info("Processed " + Long.toString(count) + " records.");
                    }
                }
            } catch (Exception exc) {
                // ignore the line but continue
                errorCount += 1;
                log.error("Error in channel at event number " + Long.toString(count),  exc);
            }
        }
        try {
            teardown();
        } catch (Exception exc) {
            log.error("Error stopping channel after " + Long.toString(count) + " events", exc);
        }
        return count;
    }

    /**
     * Setup actions required before processing any events
     */
    public abstract void setup() throws Exception;

    /**
     * Teardown actions required when the channel is stopped
     */
    public abstract void teardown() throws Exception;

    /**
     * Per-event actions
     *
     * This method should return a new event or null if no event was created. If the end of the stream is reached,
     * implementers should call the stop() method.
     *
     */
    public abstract Event next() throws Exception;

    /**
     * Method to process the input stream in a separate thread.
     *
     * This method calls the public processing method of the class, but catches
     * exceptions and logs them in keeping with the "contract" of a Runnable
     * class.
     */
    public void run() {
        try {
            this.process();
        } catch (IOException exc) {
            // can't do anything with this, so close input stream
            errorCount += 1;
            log.error("Failed reading CSV stream after processing " + Long.toString(count) + "records. Stopping.");
        }
    }

    /**
     * Method to tell this instance to stop processing records.
     *
     * This method is intended to be called from a different thread from that used to process, and just sets a
     * flag telling the processing thread to stop.
     */
    public void stop() {
        this.stop = true;
    }

    /**
     * Iterate through registered actions and call their AddEventAction.
     *
     * @param event
     */
    protected void fire(Event event) {
        for (AddEventAction a : actions) {
            a.execute(this, event);
        }
    }

    /**
     * Return any exceptions thrown during processing.
     *
     * This method is provided to retrieve any exception thrown if the
     * processing is performed in a thread.
     *
     * @return The exception that interupted processing, or null if still running
     *   or no exceptions.
     */
    public Exception getException() {
        return this.exception;
    }

    /**
     * Return current number of events generated.
     *
     * Only valid lines are counted.
     *
     * @return number of events generated
     */
    public long getCount() {
        return count;
    }

    /**
     * Get the number of errored lines
     *
     * @return the number of errored lines
     */
    public int getErrorCount() {
        return errorCount;
    }

    /**
     * Return true if this instance is not processing
     *
     * @return
     */
    public boolean isStopped() {
        return stop;
    }
}

