package com.eventswarm.eventset;

import com.eventswarm.*;
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A duplicate filter watches for duplicates in a window based on matching values returned by a retriever
 * (e.g. an ID retriever) and only adds new events to the window.
 *
 * This class depends on having control over the events added to the window. Events added to the window by
 * any other source will not be filtered (i.e. pass all events through the filter). Downstream classes could,
 * however, safely subscribe to remove events from the window.
 *
 * The creator supplies a window to constrain the window of time or N during which the filter should detect duplicates
 * (e.g. 1 week or the last 100 events). Those interested in the events that are discarded and the original event
 * that they duplicate can register against the DuplicateEventTrigger.
 *
 * Those
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public class DuplicateFilter<T> implements AddEventAction, AddEventTrigger, DuplicateEventTrigger, RemoveEventAction {
    private ValueRetriever<T> retriever;
    protected Map<T,Event> dupeMap;
    private EventSet window;
    protected Set<DuplicateEventAction> dupeActions;

    /**
     * Create a DuplicateFilter against the provided window using the supplied retriever to extract the
     * duplicate 'key' (e.g. Event.ID_RETRIEVER).
     *
     * If the window already contains events, this constructor will create a map of those events for
     * to detect duplicates added subsequently. Note that we allow any EventSet as the window.
     *
     * @param retriever
     * @param window
     */
    public DuplicateFilter(ValueRetriever<T> retriever, EventSet window) {
        this.retriever = retriever;
        this.window = window;
        this.dupeMap = new HashMap<T,Event>();
        this.dupeActions = new HashSet<DuplicateEventAction>();
        for (Event event : window) {
            dupeMap.put(retriever.getValue(event), event);
        }
        // listen for removes so we can update our duplicate detection map
        this.window.registerAction((RemoveEventAction) this);
    }

    /**
     * Create a DuplicateFilter against the default window (@see defaultWindow) using the suplied retriever
     * to extract the duplicate 'key'
     *
     * @param retriever
     */
    public DuplicateFilter(ValueRetriever<T> retriever) {
        this(retriever, defaultWindow());
    }

    /**
     * Use a default window of 1 hour
     *
     * @return
     */
    protected static EventSet defaultWindow() {
        return new DiscreteTimeWindow(3600);
    }


    /**
     * Add an event if it is not a duplicate, or fire the DuplicateEventTrigger if it is a duplicate
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(AddEventTrigger trigger, Event event) {
       T key = retriever.getValue(event);
       if (dupeMap.containsKey(key)) {
           // duplicate, so fire the trigger
           for (DuplicateEventAction action : dupeActions) {
               action.execute(this, dupeMap.get(key), event);
           }
       } else {
           // use the window lock to avoid concurrency issues
           window.lock.writeLock().lock();
           try {
               dupeMap.put(key, event);
               window.execute(trigger, event);
           } finally {
               window.lock.writeLock().unlock();
           }
       }
    }

    /**
     * Remove events from our duplicate map when the underlying window removes them, ensuring that our map doesn't
     * grow infinitely
     *
     * @param trigger
     * @param event
     */
    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        T key = retriever.getValue(event);
        window.lock.writeLock().lock();
        try {
            // use the window lock to avoid concurrency issues
            dupeMap.remove(key);
        } finally {
            window.lock.writeLock().unlock();
        }
    }

    /**
     * Return the retriever used for detecting duplicates
     *
     * @return
     */
    public ValueRetriever<T> getRetriever() {
        return retriever;
    }

    /**
     * Return the window against which this duplicate filter operates
     *
     * @return
     */
    public EventSet getWindow() {
        return window;
    }

    /**
     * Downstream add actions only receive events added to the window (i.e. not duplicates)
     *
     * @param action Action to be executed when add trigger fires.
     */
    @Override
    public void registerAction(AddEventAction action) {
        window.registerAction(action);
    }

    /**
     * Let the window manage unregisterAction calls
     *
     * @param action Action to be executed when add trigger fires.
     */
    @Override
    public void unregisterAction(AddEventAction action) {
        window.unregisterAction(action);
    }

    @Override
    public void registerAction(DuplicateEventAction action) {
        dupeActions.add(action);
    }

    @Override
    public void unregisterAction(DuplicateEventAction action) {
        dupeActions.remove(action);
    }
}
