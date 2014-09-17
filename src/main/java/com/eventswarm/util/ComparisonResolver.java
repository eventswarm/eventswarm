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
package com.eventswarm.util;

import com.eventswarm.eventset.EventSet;

import java.util.WeakHashMap;

/**
 * Class that returns a unique (at runtime) integer for each object submitted, providing for an arbitrary but
 * deterministic ordering of distinct objects whose sort position and (preferably) hashcode are equal.
 *
 * This class should be used as a last resort for Comparators, since the maintenance of a large number of objects
 * in the resolver could consume significant memory. A WeakHashMap is used to hold the references, so the deterministic
 * order might change for objects that are seldom used.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class ComparisonResolver {
    private int seqnr;
    private WeakHashMap<Object,Integer> clashes;

    public ComparisonResolver() {
        clashes = new WeakHashMap<Object,Integer>();
        seqnr = 0;
    }

    public int getInt(Object obj) {
        if (!clashes.containsKey(obj)) {
            seqnr++;
            clashes.put(obj, seqnr);
        }
        return clashes.get(obj);
    }
}
