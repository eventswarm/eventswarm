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
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static com.eventswarm.events.jdo.TestEvents.*;
import static com.eventswarm.events.jdo.TestEvents.jdoEventAfterDiffSrcAfterSeq;
import static com.eventswarm.events.jdo.TestEvents.jdoEventAfterDiffSrcConcSeq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class LastNWindowTest implements AddEventAction, RemoveEventAction, WindowChangeAction {
    private ArrayList<Event> added = new ArrayList<Event>();
    private ArrayList<Event> removed = new ArrayList<Event>();
    private int changeCount = 0;
    LastNWindow instance = new LastNWindow(2);

    @Before
    public void setUp() throws Exception {
        instance.registerAction((AddEventAction) this);
        instance.registerAction((RemoveEventAction) this);
        instance.registerAction((WindowChangeAction) this);
    }
    @Test
    public void remove() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        assertEquals(1, instance.size());
        assertEquals(jdoEvent, instance.first());
        assertEquals(0, removed.size());
        assertEquals(1, changeCount);
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        added.add(event);
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        removed.add(event);
    }

    @Override
    public void execute(WindowChangeTrigger trigger, EventSet set) {
        changeCount++;
    }
}