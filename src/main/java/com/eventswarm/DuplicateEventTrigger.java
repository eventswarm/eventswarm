package com.eventswarm;

import com.eventswarm.Trigger;

/**
 * Trigger that fires when a DuplicateFilter encounters a duplicate
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public interface DuplicateEventTrigger extends Trigger {

    public static Class action = DuplicateEventAction.class;

    /**
     * Register an action against this trigger.
     *
     * Repeated registrations should be igored.
     *
     * @param action Action to be executed when trigger fires.
     */
    public void registerAction(DuplicateEventAction action);

    /**
     * Remove registration of the identified action against this trigger.
     *
     * Ignores attempts to remove an action that is not registered.
     *
     * @param action Action to be removed from registered list.
     */
    public void unregisterAction(DuplicateEventAction action);
}
