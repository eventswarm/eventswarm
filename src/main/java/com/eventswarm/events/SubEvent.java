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
package com.eventswarm.events;

/**
 * Interface for events that are sub-events of a parent
 *
 * We use SubEvents in situations where an event has an arbitrary set of complex, subordinate attributes
 * that need to be processed independently (e.g. clinical observations in a diagnostic report). The subevent
 * will typically have the same timestamp and source as the parent but a distinguished ID. Typically, SubEvents will
 * be created by providing a reference to a subordinate Object within the parent, although some kind of path
 * expression might also be used.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface SubEvent<Type> extends Event {
    public static String PARENT_EVENTPART = "PARENT";

    public Event getParent();
    public Type getSubordinate();
}
