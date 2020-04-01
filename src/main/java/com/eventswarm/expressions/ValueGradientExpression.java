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
   * and gradient direction to match a gradient of exactly `length` events.
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
   * and gradient direction and minimum events to match to match a gradient with 
   * (min <= N <= length) events. 
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
        if (sequence.size() >= this.min && isGradient()) {
          // we have a match if our sequence is full and satisfies the gradient check
          Activity match = new JdoActivity(sequence.getEventSet());
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
   * This expression is currently true if the isGradient check returns true
   */
  @Override
  public boolean isTrue() {
    return isGradient();
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
    return this.sequence.contains(event) && isGradient();
  }

  /**
   * Check to see if the current set of events matches the gradient required (up,
   * down, flat) and has the required length
   * 
   * @return true if the events in the sequence are up/down/flat
   */
  private boolean isGradient() {
    if (sequence.size() < this.min) {
      return false;
    } else {
      Iterator<Event> iter = sequence.iterator();
      T last = values.get(iter.next());
      boolean gradient = true;
      while (iter.hasNext() && gradient) {
        T next = values.get(iter.next());
        gradient = (next.compareTo(last) == direction);
        last = next;
      }
      return gradient;
    }
  }
}
