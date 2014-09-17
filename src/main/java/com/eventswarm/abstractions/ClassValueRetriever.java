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
package com.eventswarm.abstractions;

import com.eventswarm.events.Event;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Class that joins multiple event class-specific value retrievers into a single value retriever.
 *
 * This is useful for Powersets, for example, where we want to correlate multiple streams through the powerset. Note
 * that the retriever will use the first retriever with a matching class. If an event satisfies more than one
 * class in the supplied list, then
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ClassValueRetriever<T> implements ValueRetriever<T>{
    private Map<Class, ValueRetriever<T>> retrievers;
    private static Logger logger = Logger.getLogger(ClassValueRetriever.class);

    public ClassValueRetriever(Map<Class, ValueRetriever<T>> retrievers) {
        this.retrievers = retrievers;
    }

    @Override
    public T getValue(Event event) {
        for (Class clazz : retrievers.keySet()) {
            if (clazz.isInstance(event)) {
                logger.debug("Using retriever for class " + clazz.getName());
                return retrievers.get(clazz).getValue(event);
            }
        }
        // if we get here, no retrievers match
        return null;
    }
}
