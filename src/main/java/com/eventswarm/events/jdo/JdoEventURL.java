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
 * JdoEventURL.java
 *
 * Created on 27 April 2007, 12:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.eventswarm.events.jdo;

import com.eventswarm.util.*;
import com.eventswarm.events.EventURL;
import org.apache.log4j.Logger;
import java.net.URL;

/**
 * Persistable implementation of the EventURL event part type.
 *
 * @author andyb
 */
public class JdoEventURL extends JdoEventPart implements EventURL {

    protected URL url = null;
    protected String linkText = null;

    // default logger
    private static Logger log = Logger.getLogger(JdoEventURL.class);

    /** Public default constructor, required for persistence */
    public JdoEventURL() {
        super();
    }
    
    /** Creates a new instance of JdoEventURL with no link text */
    public JdoEventURL(URL url) {
        super();
        this.url = url;
    }
    
    /** Creates a new instance of JdoEventURL with supplied url and link text */
    public JdoEventURL(URL url, String linkText) {
        super();
        this.url = url;
        this.linkText = linkText;
    }
    
    public URL getURL() {
        return url;
    }
    
    /** Allow string representation of URL for persistence */
    protected String getURLString() {
        return url.toExternalForm();
    }
    
    /** 
     * Allow string representation of URL for persistence. Malformed URL 
     * exceptions are ignored
     */
    protected void setURLString(String urlString) {
        try {
            this.url = new URL(urlString);
        } catch (Exception ex) {
            log.error(ex);
        }
    }

    public String getLinkText() {
        return this.linkText;
    }
    public void setURL (URL url) {
        this.url = url;
    }
    
    public void setLinkText (String linkText) {
        this.linkText = linkText;
    }

    public String toString() {
        return "{JdoEventURL: " + 
                super.toString() + "," +
                "linkText = " + getLinkText() + 
                "URL = " + getURLString() + 
                "}";    
    }

    public boolean equals(Object obj) {
        if (JdoEventURL.class.isInstance(obj)) {
            return equals((JdoEventURL) obj);
        }
        return false;
    }
    
    private boolean equals (JdoEventURL part) {
        if (!super.equals(part))                {return false;}
        if (!Tools.equals(url, part.getURL())) {return false;}
        return true;
    }    
}
