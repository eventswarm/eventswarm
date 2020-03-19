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
 * Class that cascades ValueRetrievers, using a value returned by one ValueRetriever to choose the correct
 * value retriever to apply.
 *
 * For example, if you wanted to choose the ValueRetriever based on the source of the event, you can pass an
 * Event.SourceValueRetriever as the discriminator, and a map of source -> ValueRetriever entries to choose the
 * correct value retriever for each source.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class KeyedValueRetriever<K, T> implements ValueRetriever<T>{
    private Map<K, ValueRetriever<T>> retrievers;
    private ValueRetriever<K> discriminator;
    private static Logger logger = Logger.getLogger(KeyedValueRetriever.class);

    public KeyedValueRetriever(ValueRetriever<K> discriminator, Map<K, ValueRetriever<T>> retrievers) {
        this.retrievers = retrievers;
        this.discriminator = discriminator;
    }

    public T getValue(Event event) {
        K key = discriminator.getValue(event);
        if (key != null) {
            ValueRetriever<T> retriever = retrievers.get(key);
            if (retriever != null) {
                return retrievers.get(key).getValue(event);
            }
        }
        // return null if we fall through
        return null;
    }

    public Map<K, ValueRetriever<T>> getRetrievers() {
        return retrievers;
    }
}
