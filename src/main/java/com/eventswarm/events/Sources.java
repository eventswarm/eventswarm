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

import com.eventswarm.events.jdo.JdoSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class to maintain a cache of source objects keyed by source name
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class Sources {
    public static final Sources cache = new Sources();

    private final Map<String,Source> sources = new HashMap<String, Source>();

    private Sources() {
        super();
    }

    public Source getSourceByName(String name) {
        if (!sources.containsKey(name)) {
            sources.put(name, new JdoSource(name));
        }
        return sources.get(name);
    }
}
