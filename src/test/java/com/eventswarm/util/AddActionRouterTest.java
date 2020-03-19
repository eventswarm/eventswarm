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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.JsonEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.OrgJsonEvent;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class AddActionRouterTest {
    static String TARGET_FIELD = "key";
    static String TARGET1_KEY = "t1";
    static String TARGET2_KEY = "t2";

    Target target1, target2;
    AddActionRouter<String> router;

    @Before
    public void setup() throws Exception {
        target1 = new Target();
        target2 = new Target();
        router = new AddActionRouter<String>(new JsonEvent.StringRetriever(TARGET_FIELD));
        router.put(TARGET1_KEY, target1);
        router.put(TARGET2_KEY, target2);
    }

    @Test
    public void testAddWithTarget() throws Exception {
        router.execute((AddEventTrigger) null, makeEvent(TARGET1_KEY));
        assertEquals(1, target1.added.size());
        assertEquals(0, target2.added.size());
    }

    @Test
    public void testAddWithoutTarget() throws Exception {
        router.execute((AddEventTrigger) null, makeEvent("/dev/null"));
        assertEquals(0, target1.added.size());
        assertEquals(0, target2.added.size());
    }

    @Test
    public void testAddDefaultTarget() throws Exception {
        Target nullTarget = new Target();
        router.put(null, nullTarget);
        router.execute((AddEventTrigger) null, makeEvent("/dev/null"));
        assertEquals(1, nullTarget.added.size());
        assertEquals(0, target1.added.size());
        assertEquals(0, target2.added.size());
    }

    @Test
    public void testAddWithNullTarget() throws Exception {
        Target nullTarget = new Target();
        router.put(null, nullTarget);
        router.execute((AddEventTrigger) null, new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject("{'a':1}")));
        assertEquals(1, nullTarget.added.size());
        assertEquals(0, target1.added.size());
        assertEquals(0, target2.added.size());
    }

    public Event makeEvent(String target) {
        Map<String,Object> fields = new HashMap<String,Object>();
        fields.put(TARGET_FIELD, target);
        return new OrgJsonEvent(JdoHeader.getLocalHeader(), new JSONObject(fields));
    }

    public static class Target implements AddEventAction {
        public List<Event> added = new ArrayList<Event>();

        public void execute(AddEventTrigger trigger, Event event) {
            added.add(event);
        }
    }
}
