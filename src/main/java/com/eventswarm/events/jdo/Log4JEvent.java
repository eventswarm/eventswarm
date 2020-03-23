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

import com.eventswarm.events.EventPart;
import com.eventswarm.events.Header;
import com.eventswarm.events.LogEvent;
import com.eventswarm.util.Sequencer;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * LogEvent implementation for Log4J events
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class Log4JEvent extends JdoEvent implements LogEvent {

    public String LOG4J_PART = "LOG4J";
    public transient LoggingEvent logEvent;
    public static Map<org.apache.log4j.Level, Log4JEvent.Level> mapper;
    private static Logger logger = Logger.getLogger(Log4JEvent.class);

    static {
        logger.debug("Creating map for log4j to EventSwarm log event levels");
        mapper = new HashMap<org.apache.log4j.Level, Log4JEvent.Level>();
        mapper.put(org.apache.log4j.Level.TRACE, Log4JEvent.Level.trace);
        mapper.put(org.apache.log4j.Level.DEBUG, Log4JEvent.Level.debug);
        mapper.put(org.apache.log4j.Level.INFO, Log4JEvent.Level.info);
        mapper.put(org.apache.log4j.Level.WARN, Log4JEvent.Level.warn);
        mapper.put(org.apache.log4j.Level.ERROR, Log4JEvent.Level.error);
        mapper.put(org.apache.log4j.Level.FATAL, Log4JEvent.Level.fatal);
    }

    private Log4JEvent() {
        super();
    }

    public Log4JEvent(LoggingEvent logEvent) {
        // create header, using RunTimeMXBean name as the source
        logger.debug("Creating new log event header");
        Header header = new JdoHeader(new Date(logEvent.timeStamp),
                                      Sequencer.getInstance().getNext(logEvent.timeStamp),
                                      new JdoSource(ManagementFactory.getRuntimeMXBean().getName()));
        super.setHeader(header);
        logger.debug("Creating parts map");
        Map<String, EventPart> parts = new HashMap<String, EventPart>();
        parts.put(LOG4J_PART, new JdoLog4JPart(logEvent));
        this.setParts(parts);
        super.setHeader(header);
    }

    @Override
    public void setParts(Map<String, EventPart> eventParts) {
        super.setParts(eventParts);
        // maintain synonym for readability
        this.logEvent = ((JdoLog4JPart) eventParts.get(LOG4J_PART)).getLogEvent();
    }

    /**
     * Returns a mapping of log level to our enumeration of log levels.
     *
     * There is a direct mapping from log4j event levels to our level enumeration.
     *
     * @return
     */
    public Level getLevel() {
        return mapper.get(logEvent.getLevel());
    }

    public String getShortMessage() {
        return logEvent.getMessage().toString();
    }

    public String[] getStackTrace() {
        return logEvent.getThrowableStrRep();
    }

    public String getText() {
        String result = getShortMessage();
        String other = join(logEvent.getThrowableStrRep());
        if (other.length() > 0) result += "\n" + other;
        return result;
    }

    public String getClassifier() {
        return logEvent.getLoggerName();
    }

    public String getContext() {
        String result = logEvent.getThreadName();
        String ndc = logEvent.getNDC();
        if (ndc != null) {
            result += ": " + ndc;
        }
        return result;
    }

    /**
     * Join an array of strings using a newline as a separator, without a trailing newline.
     *
     * @param lines
     * @return
     */
    private String join(String lines[]) {
        if (lines == null) return "";
        String result = "";
        for (int i=0; i<lines.length; i++) {
            result += lines[i];
            if (i+1 < lines.length) {
                result += "\n";
            }
        }
        return result;
    }
}
