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
package com.eventswarm.events.jdo;

import com.eventswarm.events.CSVEvent;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Header;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Optimised implementation of a CSVEvent that uses an array to hold the values and a separate, re-usable map
 * to map field names to array entries.
 *
 * Note that this Event does not put its components into EventParts: they are held as attributes on the class.
 *
 * By default, the getCsvMap method returns a compact map, that is, null values are not included in the map.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCSVEvent extends JdoEvent implements CSVEvent {
    private Map<String, Integer> fields;
    private String[] values;
    private boolean compact = true;
    private transient Map<String,String> csvMap;

    private static Logger logger = Logger.getLogger(JdoCSVEvent.class);
    /**
     *
     * @param header EventSwarm header instance
     * @param fields Mapping from field name to array index (e.g. Name => 0, Address1 => 1 etc)
     * @param values Array containing values corresponding to field mapping
     */
    public JdoCSVEvent(Header header, Map<String, Integer> fields, String[] values) {
        super(header, (Map<String,EventPart>) null);
        this.fields = fields;
        this.values = values;
        if (this.fields.size() > this.values.length) {
            logger.warn("More fields than values in record");
        } else if (this.fields.size() < this.values.length) {
            logger.warn("More values than fields in record");
        }
    }

    public String get(String field) {
        if (fields.containsKey(field)) {
            Integer index = fields.get(field);
            if (index == null || index >= values.length || index < 0) {
                return null;
            } else {
                return values[fields.get(field)];
            }
        } else {
            return null;
        }
    }

    public Map<String, String> getCsvMap() {
        if (csvMap == null) {
            csvMap = new HashMap<String,String>();
            for (String field:fields.keySet()) {
                String value = get(field);
                if (compact && value == null) {
                    // skip nulls if compact enabled
                } else {
                    csvMap.put(field, value);
                }
            }
        }
        return csvMap;
    }

    public JdoCSVEvent setCompact(boolean compact) {
        if (compact != this.compact) {
            csvMap = null; // clear CSV map if we're changing this
        }
        this.compact = compact;
        return this;
    }
}
