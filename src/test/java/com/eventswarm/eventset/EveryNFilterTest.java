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
package com.eventswarm.eventset;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static com.eventswarm.events.jdo.TestEvents.*;
/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class EveryNFilterTest implements AddEventAction {
    private ArrayList<Event> results = new ArrayList<Event>();

    @Test
    public void first_every1() throws Exception {
        EveryNFilter instance = new EveryNFilter(1);
        instance.registerAction(this);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, results.size());
        assertEquals(jdoEvent, results.get(0));
    }

    @Test
    public void second_every1() throws Exception {
        EveryNFilter instance = new EveryNFilter(1);
        instance.registerAction(this);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(2, results.size());
        assertEquals(jdoEvent, results.get(0));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, results.get(1));
    }

    @Test
    public void first_every2() throws Exception {
        EveryNFilter instance = new EveryNFilter(2);
        instance.registerAction(this);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(0, results.size());
    }

    @Test
    public void second_every2() throws Exception {
        EveryNFilter instance = new EveryNFilter(2);
        instance.registerAction(this);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(1, results.size());
        assertEquals(jdoEventAfterDiffSrcAfterSeq, results.get(0));
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        results.add(event);
    }
}
