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
 * ExceptionEvent implementation for exceptions thrown during processing
 *
 * While this inherits from a 'Jdo' (persistable) event, we are not really trying to make this one
 * persistable. It's really just an event wrapper around the Exception class. Since exceptions don't
 * have an error code, we use a hashcode of the exception class as the code.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ExceptionErrorEvent extends JdoErrorEvent implements ErrorEvent {
    private Exception exception;

    public ExceptionErrorEvent(Exception exception) {
        super();
        this.exception = exception;
    }

    /**
     * Since exceptions don't have corresponding codes, we just return the hashcode of the exception class.
     *
     * @return the hashcode of the exception class
     */
    @Override
    public Integer getErrorCode() {
        return exception.getClass().hashCode();
    }

    /**
     * Extract and return the exception error message
     *
     * @return exception message
     */
    @Override
    public String getErrorMessage() {
        return exception.getMessage();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * @return the wrapped exception
     */
    public Exception getException() {
        return exception;
    }
}
