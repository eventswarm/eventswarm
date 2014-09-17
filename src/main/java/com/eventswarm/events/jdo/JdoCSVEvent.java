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
import com.eventswarm.util.Sequencer;
import org.apache.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCSVEvent extends JdoEvent implements CSVEvent {

    protected transient Map<String,String> csvMap; // keep a copy at the Event level for convenience
    private static Logger logger = Logger.getLogger(JdoCSVEvent.class);

    protected JdoCSVEvent() {
        super();
    }

    public JdoCSVEvent(Header header, Map<String,String> csvMap) {
        super();
        this.setHeader(header);
        logger.debug("Creating parts map");
        Map<String, EventPart> parts = new HashMap<String, EventPart>();
        this.setParts(parts);
        setCsvMap(csvMap);
    }

    protected void setCsvMap(Map<String,String> csvMap) {
        this.eventParts.put(MAPPART, new JdoCSVPart(csvMap));
        this.csvMap = csvMap;
    }

    @Override
    public String get(String field) {
        return csvMap.get(field);
    }

    @Override
    public Map<String,String> getCsvMap() {
        return csvMap;
    }
}
