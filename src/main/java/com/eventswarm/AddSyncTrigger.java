
package com.eventswarm;

import com.eventswarm.Trigger;

/**
 * Trigger that fires when a new event added to a pipeline has
 * propogated through the pipeline to the outlets.
 *
 * @author andyb
 */
public interface AddSyncTrigger extends Trigger {

    public static Class action = AddSyncAction.class;
    
    /**
     * Register an action against this trigger.
     *
     * Repeated registrations should be igored.
     *
     * @param action Action to be executed when trigger fires.
     */
    public void registerAction(AddSyncAction action);

    /**
     * Remove registration of the identified action against this trigger.
     *
     * Ignores attempts to remove an action that is not registered.
     *
     * @param action Action to be removed from registered list.
     */
    public void unregisterAction(AddSyncAction action);

}
