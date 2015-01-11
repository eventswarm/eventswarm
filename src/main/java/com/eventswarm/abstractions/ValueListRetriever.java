package com.eventswarm.abstractions;

import com.eventswarm.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Retriever that returns a list of values returned by a supplied set of subordinate retrievers
 *
 * This retriever allows us to construct a value from a set of values in an event. It can be used, for example,
 * with the DuplicateFilter to detect duplicates on a set of field values rather than just a single field value.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ValueListRetriever<T> implements ValueRetriever<List<T>> {
    List<ValueRetriever<T>> retrievers;

    public ValueListRetriever(List<ValueRetriever<T>> retrievers) {
        this.retrievers = retrievers;
    }

    @Override
    public List<T> getValue(Event event) {
        List<T> result = new ArrayList<T>(retrievers.size());
        for (ValueRetriever<T> r : retrievers) {
            result.add(r.getValue(event));
        }
        return result;
    }
}
