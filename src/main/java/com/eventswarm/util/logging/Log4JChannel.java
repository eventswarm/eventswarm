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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.jdo.Log4JEvent;
import com.eventswarm.util.EventTriggerDelegate;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * EventSwarm channel that implements a Log4J appender and exposes an AddEventTrigger
 *
 * Typically, you would programmatically add this appender to the root logger in any Java process, then register
 * your logging expressions against it.  If you add one through a config file, you won't have a handle for the
 * instance and thus won't be able to register any expressions against it.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class Log4JChannel extends AppenderSkeleton implements AddEventTrigger {
    EventTriggerDelegate<AddEventTrigger, AddEventAction> delegate;

    private static Logger logger = Logger.getLogger(Log4JChannel.class);

    public Log4JChannel() {
        super();
        delegate = new EventTriggerDelegate<AddEventTrigger, AddEventAction>(this);
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        logger.debug("Appending a log4j event to the logging channel");
        Log4JEvent event = new Log4JEvent(loggingEvent);
        logger.debug("Calling registered listeners with event " + event.toString());
        delegate.fire(event);
        logger.debug("Finished calling listeners");
    }

    /**
     * Might as well use the layout if provided
     *
     * @return
     */
    public boolean requiresLayout() {
        return true;
    }

    /**
     * No resources to release, really
     */
    public void close() {
        return;
    }

    public void registerAction(AddEventAction action) {
        delegate.registerAction(action);
    }

    public void unregisterAction(AddEventAction action) {
        delegate.unregisterAction(action);
    }
}
