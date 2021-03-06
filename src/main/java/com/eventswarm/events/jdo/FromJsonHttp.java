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
package com.eventswarm.events.jdo;

import com.eventswarm.events.Event;
import com.eventswarm.events.HttpEventPart;
import org.json.JSONObject;

/**
 * Interface implemented by classes that can create event instances from a JSON object in the context of an HTTP
 * request that provides an input stream for the request body.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface FromJsonHttp {
    public Event fromJsonHttp(JSONObject json, HttpEventPart http);
}
