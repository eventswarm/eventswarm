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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class OrgJsonSubEventTest {
    OrgJsonEvent parent;
    JSONObject object;

    @Before
    public void setup() throws Exception {
        object = new JSONObject("{a:{c:1, d:2}, b:{e:'lala', f:true}}");
        parent = new OrgJsonEvent(new JdoHeader(new Date(), new JdoSource("OrgJsonSubEventTest")), object);
    }

    @Test
    public void constructFromObjects() throws Exception {
        OrgJsonSubEvent instance = new OrgJsonSubEvent(parent, parent.getJSONObject("a"));
        assertEquals(parent, instance.getParent());
        assertEquals(object.getJSONObject("a"), instance.getSubordinate());
        assertEquals(1, instance.getInt("c"));
        assertEquals(2, instance.getInt("d"));
    }

    @Test
    public void constructWithPath() throws Exception {
        OrgJsonSubEvent instance = new OrgJsonSubEvent(parent, "a");
        assertEquals(parent, instance.getParent());
        assertEquals(object.getJSONObject("a"), instance.getSubordinate());
        assertEquals(1, instance.getInt("c"));
        assertEquals(2, instance.getInt("d"));
    }

    @Test
    public void constructWithTimestamp() throws Exception {
        Date timestamp = new Date(9836598364139861L);
        OrgJsonSubEvent instance = new OrgJsonSubEvent(parent, parent.getJSONObject("a"), timestamp);
        assertEquals(parent, instance.getParent());
        assertEquals(object.getJSONObject("a"), instance.getSubordinate());
        assertEquals(1, instance.getInt("c"));
        assertEquals(2, instance.getInt("d"));
        assertEquals(timestamp, instance.getHeader().getTimestamp());
        assertNotSame(parent.getHeader().getTimestamp(), instance.getHeader().getTimestamp());
    }
}
