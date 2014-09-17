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

import java.util.Map;

/**
 * ValueRetriever that maps from an event value to another value based on supplied map and retriever object
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class MapValueRetriever<R, T> implements ValueRetriever<T> {
    Map<R, T> map;
    ValueRetriever<R> retriever;

    /**
     * Create a map value retriever using the supplied map and retriever
     *
     * @param map
     * @param retriever
     */
    public MapValueRetriever(Map<R, T> map, ValueRetriever<R> retriever) {
        this.map = map;
        this.retriever = retriever;
    }

    @Override
    public T getValue(Event event) {
        return map.get(retriever.getValue(event));
    }

    public Map<R, T> getMap() {
        return map;
    }
}
