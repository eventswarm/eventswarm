/**
 * Copyright 2020 Andrew Berry and other contributors
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

import com.eventswarm.events.Event;

/**
 * Interface for expressions that match activities (i.e. multiple events in a match)
 */
public interface ActivityExpression extends EventExpression {

  /**
   * @param event
   * @return return true if an event has been captured in a match or potential match for the expression, otherwise false
   */
  public boolean hasCaptured(Event event);
}
