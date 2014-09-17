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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.expressions;

import com.eventswarm.events.Event;

/**
 * Matcher to match the event source id
 *
 * @author andyb
 */
public class SourceMatcher implements Matcher {

    private String source = null;

    private SourceMatcher() {
        super();
    }

    public SourceMatcher(String source) {
        super();
        this.setSource(source);
    }

    /**
     * Return true if the source string matches the source id in the event header
     *
     * @param event
     * @return
     */
    public boolean matches(Event event) {
       return (event.getHeader().getSource().getSourceId().equals(this.source));
    }

    public String getSource() {
        return source;
    }

    private void setSource(String source) {
        this.source = source;
    }
}
