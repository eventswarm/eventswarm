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
package com.eventswarm.expressions;

import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

/**
 * Matcher that restricts matches the class of the event.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EventClassMatcher implements Matcher {
    private Class<?> clazz;
    private static Logger logger = Logger.getLogger(EventClassMatcher.class);

    public EventClassMatcher(Class<?> clazz) {
        this.clazz = clazz;
    }

    public boolean matches(Event event) {
        boolean result = clazz.isInstance(event);
        logger.debug("It is " + Boolean.toString(result) + " that the event " + event.toString() + " is a " + clazz.getName());
        return result;
    }
}
