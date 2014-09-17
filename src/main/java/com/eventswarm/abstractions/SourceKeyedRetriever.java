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

import java.util.Map;

/**
 * A keyed value retriever that uses the source of the event to choose a ValueRetriever.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SourceKeyedRetriever<T> extends KeyedValueRetriever<String,T> {

    /**
     * Create a retriever that uses the event source id string to choose a retriever from the supplied map.
     *
     * @param retrievers
     */
    public SourceKeyedRetriever(Map<String,ValueRetriever<T>> retrievers) {
        super(ValueRetriever.sourceRetriever, retrievers);
    }
}
