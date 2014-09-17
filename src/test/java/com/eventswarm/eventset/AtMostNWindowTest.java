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

import static org.junit.Assert.*;
import static com.eventswarm.events.jdo.TestEvents.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class AtMostNWindowTest implements AddEventAction, RemoveEventAction, WindowChangeAction {
    private ArrayList<Event> added = new ArrayList<Event>();
    private ArrayList<Event> removed = new ArrayList<Event>();
    private int changeCount = 0;
    AtMostNWindow instance = new AtMostNWindow(2);

    @Before
    public void setUp() throws Exception {
        instance.registerAction((AddEventAction) this);
        instance.registerAction((RemoveEventAction) this);
        instance.registerAction((WindowChangeAction) this);
    }

    @Test
    public void add_first() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(1, instance.size());
        assertEquals(jdoEvent, instance.first());
        assertEquals(jdoEvent, added.get(0));
        assertEquals(1, changeCount);
    }

    @Test
    public void add_belowN() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        assertEquals(2, instance.size());
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, instance.first());
        assertTrue(instance.contains(jdoEvent));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, added.get(0));
        assertEquals(jdoEvent, added.get(1));
        assertEquals(2, changeCount);
    }

    @Test
    public void add_atN() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(2, instance.size());
        assertEquals(jdoEvent, instance.first());
        assertTrue(instance.contains(jdoEventAfterDiffSrcAfterSeq));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, added.get(0));
        assertEquals(jdoEvent, added.get(1));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, added.get(2));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, removed.get(0));
        assertEquals(3, changeCount);
    }

    @Test
    public void add_lots_atN() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEventBeforeSameSrcBeforeSeq);
        instance.execute((AddEventTrigger) null, jdoEventBeforeSameSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEventAfterSameSrcBeforeSeq);
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(2, instance.size());
        assertEquals(jdoEventAfterSameSrcBeforeSeq, instance.first());
        assertTrue(instance.contains(jdoEventAfterDiffSrcAfterSeq));
        assertEquals(6, changeCount);
    }

    @Test
    public void add_aboveN_by_one() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        // be naughty and add a few extras through the back door
        instance.eventSet.add(jdoEventAfterDiffSrcBeforeSeq);
        assertEquals(3, instance.size());
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(2, instance.size());
        assertEquals(jdoEventAfterDiffSrcBeforeSeq, instance.first());
        assertTrue(instance.contains(jdoEventAfterDiffSrcAfterSeq));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, added.get(0));
        assertEquals(jdoEvent, added.get(1));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, removed.get(0));
        assertEquals(jdoEvent, removed.get(1));
        assertEquals(3, changeCount);
    }

    @Test
    public void add_aboveN_by_two() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        // be naughty and add a few extras through the back door
        instance.eventSet.add(jdoEventAfterDiffSrcBeforeSeq);
        instance.eventSet.add(jdoEventAfterDiffSrcConcSeq);
        assertEquals(4, instance.size());
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(2, instance.size());
        assertEquals(jdoEventAfterDiffSrcConcSeq, instance.first());
        assertTrue(instance.contains(jdoEventAfterDiffSrcAfterSeq));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, added.get(0));
        assertEquals(jdoEvent, added.get(1));
        assertEquals(jdoEventAfterDiffSrcAfterSeq, added.get(2));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, removed.get(0));
        assertEquals(jdoEvent, removed.get(1));
        assertEquals(jdoEventAfterDiffSrcBeforeSeq, removed.get(2));
        assertEquals(3, changeCount);
    }

    @Test
    public void async_add_2_nearN() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        AsyncAdder adder1 = new AsyncAdder(jdoEvent, instance, 1);
        AsyncAdder adder2 = new AsyncAdder(jdoEventAfterDiffSrcAfterSeq, instance, 1);
        (new Thread(adder1)).start();
        (new Thread(adder2)).start();
        waitFor(10);
        assertEquals(2, instance.size());
        assertFalse(instance.contains(jdoEventBeforeDiffSrcAfterSeq));
        assertTrue(instance.contains(jdoEvent));
        assertTrue(instance.contains(jdoEventAfterDiffSrcAfterSeq));
        assertEquals(3, changeCount);
    }

    /**
     * This test is unreliable because threading can cause events to be removed unexpectedly
     *
     * @throws Exception
     */
    @Test
    public void async_add_lots_nearN() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        Thread thread1 = new Thread(new AsyncAdder(jdoEventBeforeSameSrcBeforeSeq, instance, 1));
        Thread thread2 = new Thread(new AsyncAdder(jdoEventBeforeSameSrcAfterSeq, instance, 2));
        Thread thread3 = new Thread(new AsyncAdder(jdoEvent, instance, 2));
        Thread thread4 = new Thread(new AsyncAdder(jdoEventAfterSameSrcBeforeSeq, instance, 1));
        Thread thread5 = new Thread(new AsyncAdder(jdoEventAfterSameSrcAfterSeq, instance, 2));
        thread1.start(); thread2.start(); thread3.start(); thread4.start(); thread5.start();
        waitFor(500);
        // Can't know for sure which events will be in the set, so just count events and changes
        System.out.println("This test is unreliable due to threading, so will fail sometimes");
        assertEquals(2, instance.size());
        assertEquals(6, changeCount);
    }

    @Test
    public void remove_first_leaving_empty() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        assertEquals(0, instance.size());
        assertFalse(instance.contains(jdoEvent));
        assertEquals(jdoEvent, removed.get(0));
        assertEquals(2, changeCount);
    }

    @Test
    public void remove_first_leaving_one() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((RemoveEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        assertEquals(1, instance.size());
        assertTrue(instance.contains(jdoEvent));
        assertFalse(instance.contains(jdoEventBeforeDiffSrcAfterSeq));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, removed.get(0));
        assertEquals(3, changeCount);
    }

    @Test
    public void fill_then_remove_then_add() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        assertEquals(2, instance.size());
        assertTrue(instance.contains(jdoEventBeforeDiffSrcAfterSeq));
        assertTrue(instance.contains(jdoEventAfterDiffSrcAfterSeq));
        assertFalse(instance.contains(jdoEvent));
        assertEquals(jdoEvent, removed.get(0));
        assertEquals(4, changeCount);
    }

    @Test
    public void fill_then_add_then_remove() throws Exception {
        instance.execute((AddEventTrigger) null, jdoEventBeforeDiffSrcAfterSeq);
        instance.execute((AddEventTrigger) null, jdoEvent);
        instance.execute((AddEventTrigger) null, jdoEventAfterDiffSrcAfterSeq);
        instance.execute((RemoveEventTrigger) null, jdoEvent);
        assertEquals(1, instance.size());
        assertFalse(instance.contains(jdoEventBeforeDiffSrcAfterSeq));
        assertFalse(instance.contains(jdoEvent));
        assertTrue(instance.contains(jdoEventAfterDiffSrcAfterSeq));
        assertEquals(jdoEventBeforeDiffSrcAfterSeq, removed.get(0));
        assertEquals(jdoEvent, removed.get(1));
        assertEquals(4, changeCount);
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        waitFor(1);
        added.add(event);
    }

    @Override
    public void execute(RemoveEventTrigger trigger, Event event) {
        waitFor(1);
        removed.add(event);
    }

    @Override
    public void execute(WindowChangeTrigger trigger, EventSet set) {
        changeCount++;
    }

    private void waitFor(int millis) {
        synchronized(this) { try { wait(millis); } catch (Exception exc) { exc.printStackTrace(); } }
    }

    class AsyncAdder implements Runnable {
        Event event;
        AddEventAction target;
        int wait;

        AsyncAdder(Event event, AddEventAction target, int wait) {
            this.event = event;
            this.target = target;
            this.wait = wait;
        }

        @Override
        public void run() {
            // wait for a millisecond, just to give the others a chance to get ready and interfere
            synchronized(this) { try {this.wait(wait);} catch (InterruptedException exc) {/* do nothing */} }
            target.execute((AddEventTrigger) null, event);
        }
    }
}
