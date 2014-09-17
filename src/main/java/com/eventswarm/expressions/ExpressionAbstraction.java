/**
 * Copyright 2007-2014 Ensift Pty Ltd as trustee for the Avaz Trust and other contributors
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
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.abstractions.MutableAbstractionImpl;
import com.eventswarm.events.Event;

/**
 * Wrapper class that makes an incremental abstraction from an expression
 *
 * This is useful when you want to evaluate an expression against events that
 * have already been received (i.e. a cache or buffer) when it is first created.
 *
 * @author andyb
 */
public class ExpressionAbstraction extends MutableAbstractionImpl {

    private Expression expr;

    public ExpressionAbstraction(Expression expr) {
        this.expr = expr;
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        expr.execute(trigger, event);
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        expr.execute(trigger, event);
    }

    /**
     * To make this recyclable, we rely on the contained expression to clear its state appropriately
     */
    public void clear() {
        expr.clear();
    }

}
