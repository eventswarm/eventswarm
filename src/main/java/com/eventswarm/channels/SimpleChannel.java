package com.eventswarm.channels;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple channel implementation that manages registered actions and delivers events on request.
 *
 * This class is intended for use as instance variables in a class wants to manage multiple channels.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * Date: 2/10/2014
 * Time: 6:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimpleChannel implements AddEventTrigger {
    private Set<AddEventAction> actions;
    private Logger logger = Logger.getLogger(SimpleChannel.class);

    public SimpleChannel() {
        this.actions = new HashSet<AddEventAction>();
    }

    public void registerAction(AddEventAction action) {
        actions.add(action);
    }

    public void unregisterAction(AddEventAction action) {
        actions.remove(action);
    }

    public void fire(Event event) {
        for (AddEventAction action : actions) {
            logger.debug("Calling action " + action.toString());
            action.execute(this, event);
            logger.debug("Action completed");
        }
    }
}
