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

import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Activity;
import com.eventswarm.events.Event;
/**
 * Superclass for ActivityExpression implementations
 * 
 * Provides a default `hasCaptured()` method that assumes all matches are Activity objects.
 */
public class AbstractActivityExpression extends AbstractEventExpression implements ActivityExpression {

  public AbstractActivityExpression(int limit) {
    super(limit);
  }

  public AbstractActivityExpression() {
    super();
  }

  /**
   * Default implementation looks through matches and checks if event is included in any matched activities
   */
  public boolean hasCaptured(Event event) {
    for (Event match: matches) {
      Activity activity = (Activity) match;
      if (activity.contains(event)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Default implementation of a RemoveEventAction that removes any matches containing the specified event
   * 
   * This implementation may prove inefficient because iterating over EventSet instances creates a clone. 
   * 
   * TODO: consider efficiency improvements 
   * 
   */
  public void execute(RemoveEventTrigger trigger, Event event) {
    for (Event match : matches) {
      Activity activity = (Activity) match;
      if (activity.contains(event)) {
        matches.execute(trigger, event);
      }
    }
  }
}
