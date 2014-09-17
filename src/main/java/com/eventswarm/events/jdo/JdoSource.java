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
/*
 * CastorSource.java
 *
 * Created on April 22, 2007, 10:56 AM
 */

package com.eventswarm.events.jdo;

import com.eventswarm.events.*;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Implementation of the Source interface suitable for persistence.
 *
 * @author andyb
 */
public class JdoSource implements Source {
    
    protected String sourceId;
    protected String id;

    private static Logger logger = Logger.getLogger(JdoSource.class);
    private static JdoSource localSource;

    /** Creates a new instance of JdoSource 
     *
     * For a simple Source object, id = source name
     */
    public JdoSource(String source) {
        this.setSourceId(source);
        this.setId(source);
    }

    /** Creates a new instance of JdoSource */
    public JdoSource() {
        super();
    }

    /**
     * Public class method to return a Source object for the current host based on the IP address.
     *
     * The value is cached so that the object is only created once. If an the retrieval of host address fails,
     * a random UUID is used instead.
     *
     * @return
     */
    public static JdoSource getLocalSource() {
        if (localSource == null) {
            try {
                localSource = new JdoSource(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
                localSource = new JdoSource(UUID.randomUUID().toString());
            }
        }
        return localSource;
    }

    public String getId() { return this.id; }
    public void setId(String id) { this.id = id; }
    
    public String getSourceId() {
        return(this.sourceId);
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public boolean equals(Object object) {
        // use a cast and catch the exception
        try {
            return this.id.equals(((Source) object).getId());
        } catch (ClassCastException ex) {
            return false;
        }
    }
    
    public int hashCode() {
        return this.getId().hashCode();
    }
}
