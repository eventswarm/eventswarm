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

import org.apache.log4j.spi.LoggingEvent;

import java.util.Map;

/**
 * Event part to hold a map of CSV field values
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCSVPart extends JdoEventPart {

    private Map<String, String> csvMap;

    /**
     * private constructor for persistence
     */
    private JdoCSVPart() {
        super();
    }

    /**
     * Create a JdoCSVPart using the supplied map of field values
     *
     * @param csvMap
     */
    public JdoCSVPart(Map csvMap) {
        this.csvMap = csvMap;
    }

    /**
     * Retrieve the csv field map
     *
     * @return
     */
    public Map<String, String> getCsvMap() {
        return csvMap;
    }

    /**
     * Private setter for persistence
     *
     * @param csvMap
     */
    private void setCsvMap(Map<String, String> csvMap) {
        this.csvMap = csvMap;
    }
}
