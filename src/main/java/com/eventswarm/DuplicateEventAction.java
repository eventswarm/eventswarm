package com.eventswarm;

import com.eventswarm.events.Event;

/**
 * Action called when a DuplicateEventTrigger fires
 *
 * Downstream instances can use this to, for example, replace the previous with the duplicate or
 * update some state.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public interface DuplicateEventAction extends Action {

    public static Class<?> trigger = DuplicateEventTrigger.class;

    /**
     * Process a duplicate event
     *
     * @param trigger Upstream component identifying the duplicate
     * @param original Originally delivered event
     * @param duplicate Duplicate of the original
     */
    public void execute(DuplicateEventTrigger trigger, Event original, Event duplicate);
}
