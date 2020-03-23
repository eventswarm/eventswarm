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

import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SourceMatcherTest {
    Event event;
    Map<String, EventPart> parts = new HashMap<String, EventPart>();

    @Test
    public void matches() throws Exception {
        event = new JdoEvent(new JdoHeader(new Date().getTime(), "SourceMatcherTest"), parts);
        SourceMatcher instance = new SourceMatcher("SourceMatcherTest");
        assertEquals("SourceMatcherTest", instance.getSource());
        assertTrue(instance.matches(event));
    }

    @Test
    public void doesnt_match() throws Exception {
        event = new JdoEvent(new JdoHeader(new Date().getTime(), "SomeOtherSource"), parts);
        SourceMatcher instance = new SourceMatcher("SourceMatcherTest");
        assertEquals("SourceMatcherTest", instance.getSource());
        assertFalse(instance.matches(event));
    }
}
