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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class SequencerTest {

    @Before
    public void setUp() throws Exception {
        Sequencer.reset();
    }

    @Test
    public void testGetNext_first_forward() throws Exception {
        long time = 1;
        Sequencer instance = Sequencer.getInstance();
        int result = instance.getNext(time);
        assertEquals(0, result);
    }

    @Test
    public void testGetNext_first_forward_twice() throws Exception {
        long time = 1;
        Sequencer instance = Sequencer.getInstance();
        int result1 = instance.getNext(time);
        int result2 = instance.getNext(time);
        assertNotSame(result1, result2);
    }

    @Test
    public void testGetNext_next_forward() throws Exception {
        long time = 1;
        Sequencer instance = Sequencer.getInstance();
        int result = instance.getNext(time);
        assertEquals(0, result);
    }

    @Test
    public void testGetNext_next_backward() throws Exception {
        long time = 1;
        Sequencer instance = Sequencer.getInstance();
        instance.getNext(2);
        int result = instance.getNext(time);
        assertEquals(Integer.MAX_VALUE/2, result);
    }

    @Test
    public void testGetNext_next_backward_twice() throws Exception {
        long time = 1;
        Sequencer instance = Sequencer.getInstance();
        instance.getNext(2);
        instance.getNext(time);
        int result = instance.getNext(time);
        assertEquals(Integer.MAX_VALUE/2 + 1, result);
    }
}
