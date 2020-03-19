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
package com.eventswarm.util.logging;

import com.eventswarm.events.Event;
import com.eventswarm.events.LogEvent;
import com.eventswarm.expressions.Matcher;
import org.apache.log4j.Logger;

/**
 * Matches log events that have a level equal to or higher than the specified level
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class LevelThresholdMatcher implements Matcher {

    private LogEvent.Level level;
    private static Logger logger = Logger.getLogger(LevelThresholdMatcher.class);

    public LevelThresholdMatcher(LogEvent.Level level) {
        logger.debug("Matching log events with level >= " + level.name());
        this.level = level;
    }

    public boolean matches(Event event) {
        logger.debug("Checking level threshold on " + event.toString());
        if (LogEvent.class.isInstance(event)) {
            LogEvent.Level eventLevel = ((LogEvent) event).getLevel();
            return (eventLevel.isAbove(level) || eventLevel == level );
        } else {
            return false;
        }
    }
}
