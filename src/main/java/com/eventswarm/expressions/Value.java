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

/**
 * Interface for passing around references to dynamic values managed by an object without requiring knowledge of the
 * means by which the value is retrieved or calculated.
 *
 * This interface is used in value comparison expressions to avoid reference to specific objects or interfaces. We
 * expect that many implementations will be anonymous instances created to retrieve a specific value from an
 * abstraction.
 *
 * Note that this is similar to the ValueRetriever interface, except that a ValueRetriever is associated with an event
 * context. This allows a ValueRetriever to be used in an AddEventAction, for example.
 *
 * The Value interface requires a fixed context (e.g. an abstraction).
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface Value<T extends Object> {

    /**
     * Retrieve the current value
     *
     * @return
     */
    public T getValue();
}
