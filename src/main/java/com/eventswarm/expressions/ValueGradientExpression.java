/**
 * Copyright 2020 Andrew Berry
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
package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
import com.eventswarm.events.jdo.*;
import com.eventswarm.eventset.LastNWindow;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Expression that matches when the sequence of values extracted from ordered
 * events is strictly increasing, decreasing or flat
 * 
 * Where a minimum and maximum length is specified, the expression is true only 
 * when the current `tail` having the required gradient is at least `min` long. 
 * 
 * This expression re-evaluates the entire sequence whenever a new event is
 * added rather than just checking the next transition because events can
 * possibly be added out-of-order. Not super-efficient for long sequences, but
 * simple and reliable. 
 * 
 * Note that any value retriever returning a Comparable value can be used,
 * although we anticipate this will mostly be used with numeric values.
 * 
 * Events that return a null value for the retriever are ignored.
 * 
 */
public class ValueGradientExpression<T extends Comparable<T>> extends AbstractActivityExpression {

  private static Logger logger = Logger.getLogger(ValueGradientExpression.class);

  private LastNWindow sequence; // a LastNWindow holds the last N events in time order
  private ValueRetriever<T> retriever;
  private int direction;
  private Map<Event, T> values;
  private int min; // minimum number of events to have a match

  /**
   * Create a ValueGradientExpression with the specified length, value retriever
   * and gradient direction, to match a gradient of exactly `length` events.
   * 
   * Doesn't validate params, so if you give a length < 2 or a direction that
   * doesn't equal -1, 0 or 1, then you're on your own.
   * 
   * @param length    length of the sequence to match the gradient (e.g. 5 in a row)
   * @param retriever event value retriever to use for gradient calculation
   * @param direction gradient direction == -1 (down), 0 (flat) or 1 (up)
   */
  public ValueGradientExpression(int length, ValueRetriever<T> retriever, int direction) {
    // create an instance with min == length
    this(length, retriever, direction, length);
  }

  /**
   * Create a ValueGradientExpression with the specified length, value retriever
   * gradient direction and minimum events, to match a gradient with (min <= N <= length) events. 
   * 
   * Doesn't validate params, so if you give a length or min < 2 or a direction that
   * doesn't equal -1, 0 or 1, then you're on your own.
   * 
   * @param length    maximum length of the sequence to match the gradient
   * @param retriever event value retriever to use for gradient calculation
   * @param direction gradient direction == -1 (down), 0 (flat) or 1 (up)
   * @param min       minimum length of sequence to match the gradient
   */
  public ValueGradientExpression(int length, ValueRetriever<T> retriever, int direction, int min) {
    sequence = new LastNWindow(length);
    this.retriever = retriever;
    this.direction = direction;
    this.min = min;
    sequence.registerAction((RemoveEventAction) this);
    values = new HashMap<Event, T>();
  }

  /**
   * Add a new event to the sequence
   * 
   * Ignores events that do not return a value using the retriever
   */
  @Override
  public void execute(AddEventTrigger trigger, Event event) {
    T value = retriever.getValue(event);
    if (value != null) { // ignore events with no value
      sequence.execute(trigger, event);
      if (sequence.contains(event)) {
        values.put(event, value); // cache the value in case the retrieval is non-trivial
        if (isTrue()) {
          // generate a match event containing our gradient `tail` if the expression is true
          Activity match = new JdoActivity(tail());
          this.matches.execute((AddEventTrigger) this, match);
          this.fire(match);
        }
      }
    }
  }

  /**
   * Update the value map and remove matches containing the event when an event is removed
   */
  @Override
  public void execute(RemoveEventTrigger trigger, Event event) {
    if (trigger == sequence) {
      // if our N-length sequence removes an event, we no longer need its value to evaluate the gradient
      values.remove(event);
    }
    // use superclass method to remove matches including the event
    super.execute(trigger, event);
  }

  /**
   * This expression is currently true if we have enough events and the isGradient check returns true
   */
  @Override
  public boolean isTrue() {
    return sequence.size() >= min && isGradient();
  }

  /**
   * True if the specified event is part of the sequence and we currently have a gradient
   * 
   * Note that hasMatched is used in the context of `event` being processed (i.e. usually no 
   * other events are being processed concurrently) so will not generally be called after the
   * event has fallen out of our match window. 
   */
  @Override
  public boolean hasMatched(Event event) {
    return this.sequence.contains(event) && isTrue();
  }

  /**
   * Check to see if the current set of events matches the gradient required (up,
   * down, flat) and has the required length
   * 
   * @return true if the gradient `tail()` is larger than `min`
   */
  private boolean isGradient() {
    return tail().size() >= min;
  }

  /**
   * Return the gradient "tail" set
   * 
   * Iterates from the last event in the set until an event is found that *does not* 
   * match the required gradient or the first event if the entire set is a gradient.
   * 
   * @return longest tail set that matches gradient or null if the sequence is empty
   */
  private SortedSet<Event> tail() {
    if (sequence.isEmpty()) return null;
    else {
      NavigableSet<Event> iterableSet = sequence.getEventSet();
      Iterator<Event> iter = iterableSet.descendingSet().iterator(); // iterate in reverse
      Event current = iter.next();
      Event previous;
      T value = values.get(current);
      while (iter.hasNext()) {
        previous = iter.next();
        T prev_value = values.get(previous);
        if (value.compareTo(prev_value) != direction) {
          return iterableSet.tailSet(current, true);
        }
        current = previous;
      }
      return iterableSet;
    }
  }
}
