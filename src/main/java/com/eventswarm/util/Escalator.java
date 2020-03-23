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
package com.eventswarm.util;

import com.eventswarm.*;
import com.eventswarm.SizeThresholdAction;
import com.eventswarm.abstractions.SizeThresholdMonitor;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to establish a configuration of size threshold monitors to implement escalation.
 *
 * To give reasonable escalation semantics, the size threshold monitors are set to reset at 0. It is assumed that
 * in most cases, an explicit reset would be used.
 *
 * Note that since each SizeThresholdMonitor counts events added/removed rather than actually monitoring
 * the size of the upstream source, accurate monitoring of an EventSet requires that the monitor is connected
 * at EventSet creation time.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class Escalator implements MutableTarget {
    private Map<Long,Set<SizeThresholdAction>> actions;
    private transient Map<Long,SizeThresholdMonitor> monitors;

    private static Logger logger = Logger.getLogger(Escalator.class);

    /**
     * Create a new Escalator and adding an initial set of size threshold monitors with the
     * defined size and action.
     *
     * Further actions can be added for a defined threshold using the addThresholdAction method.
     *
     * @param initialActions initial set of threshold/action pairs to be added, can be null
     */
    public Escalator(Map<Long, SizeThresholdAction> initialActions) {
        this.actions = new HashMap<Long, Set<SizeThresholdAction>>();
        this.monitors = new HashMap<Long,SizeThresholdMonitor>();
        addActions(initialActions);
    }

    private void addActions(Map<Long, SizeThresholdAction> actions) {
        if (actions != null) {
            for (Map.Entry<Long, SizeThresholdAction> entry: actions.entrySet()) {
                this.addThresholdAction(entry.getKey(), entry.getValue(), 0L);
            }
        }
    }

    public void execute(AddEventTrigger trigger, Event event) {
        for (AddEventAction action:monitors.values()) {
            action.execute(trigger, event);
        }
    }

    public void execute(RemoveEventTrigger trigger, Event event) {
        for (RemoveEventAction action:monitors.values()) {
            action.execute(trigger, event);
        }
    }

    /**
     * Add a new action and for the specified threshold, creating a new monitor for the threshold if this
     * threshold has not previously been added.
     *
     * The initial size associated with this monitor will be set to match the other monitors.
     *
     * @param threshold
     * @param action
     * @return this escalator, for spring-style chaining
     */
    public Escalator addThresholdAction(Long threshold, SizeThresholdAction action) {
        addThresholdAction(threshold, action, getSize());
        return this;
    }

    /**
     * Private version of method that avoids calculating size when called from the constructor
     *
     * @param threshold
     * @param action
     * @param initialSize
     */
    private void addThresholdAction(Long threshold, SizeThresholdAction action, Long initialSize) {
        if (!actions.containsKey(threshold)) {
            actions.put(threshold, new HashSet<SizeThresholdAction>());
        }
        actions.get(threshold).add(action);
        if (!monitors.containsKey(threshold)) {
            SizeThresholdMonitor monitor = new SizeThresholdMonitor(threshold, 0);
            // sync the size of the new monitor
            monitor.setSize(initialSize);
            monitors.put(threshold, monitor);
        }
        monitors.get(threshold).registerAction(action);
    }

    /**
     * Remove an action for the specified threshold, removing the monitor if no actions remain for the trheshold.
     *
     * @param threshold
     * @param action
     * @return this escalator, for spring-style chaining
     */
    public Escalator removeThresholdAction(Long threshold, SizeThresholdAction action) {
        Set<SizeThresholdAction> actionSet = actions.get(threshold);
        if (actionSet != null && actionSet.contains(action)) {
            actionSet.remove(action);
            monitors.get(threshold).unregisterAction(action);
            if (actionSet.isEmpty()) {
                actions.remove(threshold);
                monitors.remove(threshold);
            }
        } else {
            logger.warn("Attempt to remove non-existant action");
        }
        return this;
    }

    /**
     * Get the current monitored size (all monitors should be in lock-step, so just grab the first)
     */
    public long getSize() {
        if (monitors.isEmpty()) {
            return 0L;
        } else {
            return monitors.values().iterator().next().getSize();
        }
    }

    /**
     * Re-enable all monitors with caveats on reset as per SizeThresholdMonitor
     */
    public void reset() {
        for (SizeThresholdMonitor monitor : monitors.values()) {
            monitor.reset();
        }
    }

    /**
     * Clear all monitors, setting size back to zero and re-enabling
     */
    public void clear() {
        for (SizeThresholdMonitor monitor : monitors.values()) {
            monitor.clear();
        }
    }

    public Map<Long, SizeThresholdMonitor> getMonitors() {
        return monitors;
    }

    public Map<Long, Set<SizeThresholdAction>> getActions() {
        return actions;
    }
}
