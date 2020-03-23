
package com.eventswarm;

import com.eventswarm.Trigger;

/**
 * Trigger that fires when an event removal has propogated through the pipeline
 * to the outlets.
 *
 * Note that this trigger only fires if the inlet of the pipeline implements
 * RemoveEventAction.
 * 
 * @author andyb
 */
public interface RemoveSyncTrigger extends Trigger {

    public static Class<?> action = RemoveSyncAction.class;
    
    /**
     * Register an action against this trigger.
     *
     * Repeated registrations should be igored.
     *
     * @param action Action to be executed when trigger fires.
     */
    public void registerAction(RemoveSyncAction action);

    /**
     * Remove registration of the identified action against this trigger.
     *
     * Ignores attempts to remove an action that is not registered.
     *
     * @param action Action to be removed from registered list.
     */
    public void unregisterAction(RemoveSyncAction action);

}
