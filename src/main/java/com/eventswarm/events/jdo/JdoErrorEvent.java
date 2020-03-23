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

import com.eventswarm.events.ErrorEvent;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoErrorEvent extends JdoEvent implements ErrorEvent {
    Integer errorCode;
    String errorMessage;

    /**
     * Create an error event with the supplied code and message
     *
     * @param errorCode
     * @param errorMessage
     */
    public JdoErrorEvent(Integer errorCode, String errorMessage) {
        this();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Create an empty event and add the default local header
     */
    public JdoErrorEvent() {
        super();
        this.setHeader(JdoHeader.getLocalHeader());
    }

    // getters implementing the interface
    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // private setters for persistence

    private void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }


    private void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
