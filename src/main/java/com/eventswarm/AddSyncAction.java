
package com.eventswarm;

/**
 * Action to be called by a pipeline when a new event has propogated through the
 * pipeline.
 *
 * It is often necessary to take actions against a pipline in a synchronised
 * manner, that is, after all elements of the pipeline have processed 
 * a new event.  Actions registered against the corresponding trigger can access
 * the pipeline outlets knowing that the set of values and/or events available
 * at those outlets represent a consistent cut.
 *
 * The corresponding trigger is not usually thread safe.  Read the documentation
 * for the class providing the trigger, but if thread safety is required,
 * external synchronisation or serialisation mechanisms might be necessary.
 *
 * @author andyb
 */

import com.eventswarm.Action;
import com.eventswarm.events.Event;

public interface AddSyncAction extends Action {

    public static Class<?> trigger = AddSyncTrigger.class;

    public void execute(AddSyncTrigger trigger, Event event);

}
