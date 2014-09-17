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

/**
 * Generic CSV data stream channel
 *
 * This channel can either be extended or used as-is to process streams of CSV data, that is, records are delimited
 * by a newline character, and fieldNames within a record are delimited by commas.
 *
 * If used as-is, the events generated will be generic CSVEvent objects containing a map of field values keyed by the
 * names in the supplied fieldNames array or optionally taken from the first line of the data stream. If any record
 * contains more field values than the number of names in the fieldNames array, these values are ignored. If any record
 * contains less field values, an error will occur.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */

import com.eventswarm.AddEventAction;
import java.io.InputStream;
import java.io.InputStreamReader;
import au.com.bytecode.opencsv.CSVReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.CSVEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.Header;
import com.eventswarm.events.jdo.JdoCSVEvent;
import com.eventswarm.events.jdo.JdoHeader;
import org.apache.log4j.Logger;

public class CSVChannel extends AbstractChannel {

    /**
     * Array of field names, used as keys for the event maps.
     */
    private String fieldNames[];
    private transient CSVReader reader;
    protected static Logger logger = Logger.getLogger(CSVChannel.class);

    protected CSVChannel() {
        super();
    }

    /**
     * Create CSVChannel using the supplied stream and using the field values in the first line of the stream as
     * field names for each CSVEvent created.
     *
     * @param istr
     */
    public CSVChannel(InputStream istr) {
        this(istr, null);
    }

    /**
     * Create CSVChannel using the supplied stream and the supplied field names for each CSVEvent created.
     *
     * @param istr
     */
    public CSVChannel(InputStream istr, String fieldNames[]) {
        super();
        this.istr = istr;
        this.fieldNames = fieldNames;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    @Override
    public void setup() throws Exception {
        reader = new CSVReader(new InputStreamReader(istr));
        if (fieldNames == null) {
            this.fieldNames = reader.readNext();
        }
    }

    @Override
    public Event next() throws Exception {
        String fields[];
        if ((fields = reader.readNext()) != null) {
            return createEvent(fields);
        } else {
            // end of stream, so finalise
            stop();
            return null;
        }
    }

    @Override
    public void teardown() throws Exception {
        reader.close();
    }

    /**
     * Method to create a new CSVEvent from the current CSV record
     *
     * This method should be overridden by child classes if they wish to create distinguished events.
     *
     * @param fields
     * @return
     */
    protected CSVEvent createEvent(String fields[]) throws ParseException {
        Header header = new JdoHeader(this.getTimestamp(fields), this.getSource(fields));
        return new JdoCSVEvent(header, createMap(fields));
    }

    protected Map<String, String> createMap(String fields[]) {
        HashMap map = new HashMap<String, String>();
        for (int i=0; i < fieldNames.length; i++) {
            // for compactness, don't save empty fields
            if (fields[i] != null && !fields[i].equals("")) {
                map.put(fieldNames[i], fields[i]);
            }
        }
        return map;
    }

    /**
     * Create fields map using a whitelist to minimise memory usage
     *
     * @param fields
     * @param whitelist
     * @return
     */
    protected Map<String,String> createMap(String fields[], Set<String> whitelist) {
        HashMap map = new HashMap<String, String>();
        for (int i=0; i < fieldNames.length; i++) {
            // for compactness, don't save empty fields
            if (whitelist.contains(fieldNames[i])) {
                map.put(fieldNames[i], fields[i]);
            }
        }
        return map;
    }

    /**
     * Return a source object to be used when constructing the event header.
     *
     * This method should be overridden by child classes if desired (e.g. to extract it from the CSV). It defaults to
     * the current system name.
     *
     * @param fields the CSV field values from which the event will be created
     *
     * @return
     */
    protected String getSource(String[] fields) {
        return ManagementFactory.getRuntimeMXBean().getName();
    }

    /**
     * Return a timestamp for the event to be used when constructing the event header.
     *
     * This method should be overridden by child classes if desired (e.g. to extract it from the CSV).
     * It uses the current system timestamp by default.
     *
     * @param fields the CSV field values from which the event will be created
     */
    protected long getTimestamp(String[] fields) {
        return System.currentTimeMillis();
    }
}
