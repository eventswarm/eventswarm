package com.eventswarm.expressions;

import com.eventswarm.events.Event;

import java.util.List;

/**
 * Comparator implementation that returns true if all comparators in a supplied list return true for a pair of events
 *
 * This class implements short-circuit evaluation, that is, if any comparator returns false then the
 * <code>matches</code> function immediately returns false. It is preferable to order the list of comparators
 * to minimise the number of comparisons required.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public class ANDComparator implements Comparator {
    private List<Comparator> comparators;

    public ANDComparator(List<Comparator> comparators) {
        this.comparators = comparators;
    }

    public boolean matches(Event event1, Event event2) {
        for (Comparator comparator : comparators) {
            if (!comparator.matches(event1, event2)) {
                return false;
            }
        }
        return true;
    }

    public List<Comparator> getComparators() {
        return comparators;
    }
}
