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

import com.eventswarm.Combination;
import com.eventswarm.events.Event;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoCombination extends ArrayList<Event> implements Combination {
    private static final long serialVersionUID = 1L;

    public JdoCombination() {
        super();
    }

    public JdoCombination (int size) {
        super(size);
    }

    /**
     * Create a new combination using a head event and existing combination tail
     *
     * @param head First event in new combination
     * @param tail Trailing events in new combination
     */
    public JdoCombination(Event head, Combination tail) {
        super(tail.size() + 1);
        this.add(head);
        this.addAll(tail);
    }

    /**
     *
     * @param head Leading events in new combination
     * @param tail Final event in new combination
     */
    public JdoCombination(Combination head, Event tail) {
        super(head.size() + 1);
        this.addAll(head);
        this.add(tail);
    }
}
