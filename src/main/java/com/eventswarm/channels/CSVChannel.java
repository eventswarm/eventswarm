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
 * CSV data stream channel that uses the optimised CSV array event implementation
 *
 * This channel can either be extended or used as-is to process streams of CSV data, that is, records are delimited
 * by a newline character, and fieldNames within a record are delimited by commas.
 *
 * The set of field names used for columns can be initialised or overridden by supplying an explicit list of
 * field names.
 *
 * The default mapping from field name to array index can also be overridden by supplying an explicit
 * map of field name to array index values. This map functions as a whitelist: the value associated with any
 * field name <strong>not</strong> appearing in the map will be discarded. It also allows the fields to be re-ordered
 * if desired. Note that whitelisting will reduce memory usage but can increase processing times because field values
 * are copied.
 *
 * This class provides spring-like set methods that return the instance.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */

import au.com.bytecode.opencsv.CSVReader;
import com.eventswarm.events.CSVEvent;
import com.eventswarm.events.Event;
import com.eventswarm.events.Header;
import com.eventswarm.events.Source;
import com.eventswarm.events.jdo.JdoCSVEvent;
import com.eventswarm.events.jdo.JdoCSVEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CSVChannel extends AbstractChannel {

    /**
     * Array of field names, used as keys for the event maps.
     */
    private String fieldNames[];
    private Map<String,Integer> fieldMap;
    private transient CSVReader reader;
    private String sourceName;
    private String sourceField;
    private int sourceIdx = -1;
    private String timestampField;
    private int timestampIdx = -1;
    private DateFormat timestampFormat;
    private transient boolean defaultMap = true;
    protected static Logger logger = Logger.getLogger(CSVChannel.class);
    private transient Map<String,Source> sourceCache;

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
        this(istr, null, null);
    }

    /**
     * Create CSVChannel using the supplied stream and the supplied field names for each CSVEvent created.
     * A default fieldmap is created that includes all of the names.
     *
     * @param istr input stream
     * @param fieldNames Array of string field names associated with CSV columns
     */
    public CSVChannel(InputStream istr, String fieldNames[]) {
        this(istr, fieldNames, null);
    }


    /**
     * Create CSVChannel using the supplied stream and the supplied fieldMap for including only necessary fields,
     * assuming the field names are supplied in the first list of the stream
     *
     * @param istr input stream
     * @param fieldMap map of
     */
    public CSVChannel(InputStream istr, Map<String, Integer> fieldMap) {
        this(istr, null, fieldMap);
    }

    /**
     * Create CSVChannel using the supplied stream, the input field names and supplied fieldMap for including
     * only necessary fields
     *
     * @param istr input stream
     * @param fieldNames Array of string field names associated with CSV columns
     *
     */
    public CSVChannel(InputStream istr, String fieldNames[], Map<String, Integer> fieldMap) {
        super();
        this.istr = istr;
        this.fieldNames = fieldNames;
        this.fieldMap = fieldMap;
        this.sourceCache = new HashMap<String,Source>();
    }

    /**
     * Verify that all of the field indices are present in the field map
     *
     * @param fieldNames
     * @param fieldMap
     * @return
     */
    private static boolean containsAll(String[] fieldNames, Map<String,Integer> fieldMap) {
        for (int i=0; i < fieldNames.length; i++) {
            if (!fieldMap.containsKey(fieldNames[i])) {
                return false;
            }
        }
        return true;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public Map<String,Integer> getFieldMap() {
        return fieldMap;
    }

    public String getSourceName() {
        return sourceName;
    }

    public boolean isDefaultMap() {
        return defaultMap;
    }

    /**
     * Use a fixed source name for all records in the CSV file
     *
     * Note that this setting is ignored if a sourceField is identified
     *
     * @return this
     */
    public CSVChannel setSourceName(String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    @Override
    public void setup() throws Exception {
        reader = new CSVReader(new InputStreamReader(istr));
        if (fieldNames == null) {
            this.fieldNames = reader.readNext();
        }
        if (fieldMap == null) {
            defaultMap = true;
            fieldMap = new HashMap<String,Integer>();
            String name;
            for (int i=0; i<fieldNames.length; i++) {
                if (fieldMap.containsKey(fieldNames[i])) {
                    logger.warn("Duplicate field name '" + fieldNames[i] + "'. Adding column number suffix.");
                    fieldNames[i] += Integer.toString(i);
                }
                fieldMap.put(fieldNames[i], i);
            }
        } else {
            defaultMap = false;
        }
        if (sourceField != null) {
            sourceIdx = fieldIndex(sourceField);
        }
        if (timestampField != null) {
            timestampIdx = fieldIndex(timestampField);
        }
    }

    @Override
    public Event next() throws Exception {
        String values[];
        if ((values = reader.readNext()) != null) {
            if (values.length < fieldNames.length) {
                logger.warn("Value array is shorter than fieldname array");
            }
            return createEvent(values);
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
     * This method ensures that values are inserted into the value array at the position defined by the fieldMap,
     * and that the array is sized to match the size of the fieldMap, thus reducing memory if whitelisting is used.
     *
     * @param values
     * @return
     */
    protected CSVEvent createEvent(String values[]) throws ParseException {
        Header header = new JdoHeader(new Date(this.getTimestamp(values)), this.getCachedSource(values));
        if (!defaultMap) {
            String[] eventValues = new String[fieldMap.size()];
            for (int i=0; i < values.length; i++) {
                Integer index = fieldMap.get(fieldNames[i]);
                if (index != null) {
                    eventValues[fieldMap.get(fieldNames[i])] = values[i];
                }
            }
            return new JdoCSVEvent(header, fieldMap, eventValues);
        } else {
            return new JdoCSVEvent(header, fieldMap, values);
        }
    }

    /**
     * Return a source object to be used when constructing the event header.
     *
     * This method can be overridden by child classes if desired (e.g. to construct it from CSV values). It defaults to
     * the current system name.
     *
     * @param fields the CSV field values from which the event will be created
     *
     * @return
     */
    protected String getSource(String[] fields) {
        if (sourceIdx >= 0) {
            return fields[sourceIdx];
        } else if (sourceName != null) {
            return sourceName;
        } else {
            return ManagementFactory.getRuntimeMXBean().getName();
        }
    }

    private Source getCachedSource(String[] fields) {
        String name = getSource(fields);
        if (!sourceCache.containsKey(name)) {
            sourceCache.put(name, new JdoSource(name));
        }
        return sourceCache.get(name);
    }

    /**
     * Return a timestamp for the event to be used when constructing the event header.
     *
     * It uses a specified field value parsed using the timestampFormat if defined, or the current system
     * timestamp otherwise.
     *
     * This method can be overridden by child classes if desired.
     *
     * @param fields the CSV field values from which the event will be created
     */
    protected long getTimestamp(String[] fields) {
        try {
            if (timestampIdx >= 0 && timestampFormat != null) {
                return timestampFormat.parse(fields[timestampIdx]).getTime();
            }
        } catch (java.text.ParseException pe) {
            logger.warn("Error parsing timestamp: ", pe);
        }
        return System.currentTimeMillis();
    }

    public CSVChannel setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    public CSVChannel setFieldMap(Map<String, Integer> fieldMap) {
        this.fieldMap = fieldMap;
        return this;
    }

    /**
     * Tell the channel to extract the event source from the named field
     *
     * This method searches for the named field in the fieldnames array and remembers the index
     * of the field. If not matched, the index is set to -1 and a default source name is used.
     *
     * @param name
     * @return this
     */
    public CSVChannel setSourceField(String name) {
        this.sourceField = name;
        return this;
    }

    /**
     * Tell the channel to extract the event timestamp from the named field using the supplied DateFormat instance
     *
     * This method searches for the named field in the fieldnames array and remembers the index
     * of the field. If not matched, the index is set to -1 and a local timestamp is used.
     *
     * @param name
     * @return this
     */
    public CSVChannel setTimestampField(String name, DateFormat timestampFormat) {
        this.timestampField = name;
        this.timestampFormat = timestampFormat;
        return this;
    }

    /**
     * Returns the index of the field name matching the supplied name or -1 if not found
     *
     * @param name
     * @return
     */
    private int fieldIndex(String name) {
        for (int i=0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }
}
