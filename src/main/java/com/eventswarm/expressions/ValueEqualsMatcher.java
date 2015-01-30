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

import com.eventswarm.abstractions.ValueRetriever;
import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

/**
 * Expression implementing an equals comparison between a value retrieved from a new event and a value held in an abstraction
 * or other predefined source of values.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ValueEqualsMatcher implements Matcher {
    private Value value;
    private ValueRetriever retriever;
    private static Logger logger = Logger.getLogger(ValueEqualsMatcher.class);

    public ValueEqualsMatcher(Value value, ValueRetriever retriever) {
        this.value = value;
        this.retriever = retriever;
    }

    @Override
    public boolean matches(Event event) {
        Object compare = value.getValue();
        logger.debug("Comparing comparison value " + compare + " with event value " + retriever.getValue(event));
        if (compare == null) {
            return retriever.getValue(event) == null;
        } else {
            return value.getValue().equals(retriever.getValue(event));
        }
    }

    public Value getValue() {
        return value;
    }

    public ValueRetriever getRetriever() {
        return retriever;
    }
}
