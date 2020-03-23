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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.powerset;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.RemoveEventAction;
import com.eventswarm.RemoveEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.eventset.EventSet;
import junit.framework.TestCase;
import com.eventswarm.events.jdo.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author andyb
 */
public class HashPowersetTest
        extends TestCase
        implements EventKey<String>, EventSetFactory<String>,
                NewSetAction<String>, AddEventTrigger, RemoveEventTrigger
{
    
    // Keep track of the powerset contents
    private HashMap<String,EventSet> map = new HashMap<String,EventSet>();

    // Other key generator
    private static HashPowersetTest other = new HashPowersetTest("Other");

    // Some events
    private static Event eventA1 = new JdoEvent(TestEvents.headerA1, TestEvents.partsEmptyMap);
    private static Event eventA2 = new JdoEvent(TestEvents.headerA2, TestEvents.partsEmptyMap);
    private static Event eventB1 = new JdoEvent(TestEvents.headerB1, TestEvents.partsEmptyMap);
    private static String eventAKey = TestEvents.headerA1.getSource().getId();
    private static String eventBKey = TestEvents.headerB1.getSource().getId();

    public HashPowersetTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Use the Event source identifier as the key
     *
     * @param event
     * @return
     */
    public String getKey(Event event) {
        String key = event.getHeader().getSource().getId();
        System.out.println("New event key is: '" + key  + "'");
        return (event.getHeader().getSource().getId());
    }

    /**
     * Use an EventSet so everything is captured and remove is implemented
     *
     * @param pset
     * @param key
     * @return
     */
    public EventSet createEventSet(Powerset<String> pset, String key) {
        System.out.println("Creating new eventset for key: '" + key + "'");
        return new EventSet();
    }

    /**
     * Just copy the eventset into our own hashmap
     * 
     * @param trigger
     * @param es
     * @param key
     */
    public void execute(NewSetTrigger<String> trigger, EventSet es, String key) {
        System.out.println("Adding eventset of key: '" + key + "'");
        this.map.put(key, es);
        // check that the eventset is actually empty (which it should be)
        assertTrue(es.size() == 0);
    }

    public void registerAction(AddEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterAction(AddEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void registerAction(RemoveEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterAction(RemoveEventAction action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void testEquals () {
        System.out.println("equals");
        HashPowerset<String> instance1 = new HashPowerset<String>((EventKey<String>)this);
        System.out.println("Instance 1 hashcode is " + Integer.toString(instance1.hashCode()));
        HashPowerset<String> instance2 = new HashPowerset<String>((EventKey<String>)this);
        System.out.println("Instance 2 hashcode is " + Integer.toString(instance2.hashCode()));

        assertFalse(instance1 == instance2);
        assertFalse(instance1.equals(instance2));
    }

    public void testHashcode () {
        System.out.println("hashcode");
        HashPowerset<String> instance1 = new HashPowerset<String>((EventKey<String>)this);
        System.out.println("Instance 1 hashcode is " + Integer.toString(instance1.hashCode()));
        HashPowerset<String> instance2 = new HashPowerset<String>((EventKey<String>)this);
        System.out.println("Instance 2 hashcode is " + Integer.toString(instance2.hashCode()));

        assertFalse(instance1.equals(instance2));
        assertFalse(instance1.hashCode() == instance2.hashCode());
    }

    public void testAddToSet () {
        System.out.println("Add two powersets to a set");
        HashPowerset<String> instance1 = new HashPowerset<String>((EventKey<String>)this);
        HashPowerset<String> instance2 = new HashPowerset<String>((EventKey<String>)this);
        HashSet<AddEventAction> set = new HashSet<AddEventAction>();

        System.out.println("Instance 1 hashcode is " + Integer.toString(instance1.hashCode()));
        System.out.println("Instance 2 hashcode is " + Integer.toString(instance2.hashCode()));

        set.add(instance1);
        boolean result = set.add(instance2);

        assertEquals(set.size(), 2);
        assertTrue(result);
    }


    /**
     * Check that constructor is setting the keygenerator correctly
     */
    public void testGetKeyGenerator() {
        System.out.println("getKeyGenerator");
        HashPowerset<String> instance = new HashPowerset<String>((EventKey<String>)this);
        assertEquals(this, instance.getKeyGenerator());
    }

    /**
     * Test of setKeyGenerator method, of class HashPowerset.
     */
    public void testSetKeyGenerator() {
        System.out.println("setKeyGenerator");
        EventKey<String> keyGenerator = other;
        HashPowerset<String> instance = new HashPowerset<String>((EventKey<String>)other);
        instance.setKeyGenerator(this);
        assertEquals(this, instance.getKeyGenerator());
     }

    /**
     * Test of getFactory method, of class HashPowerset.
     */
    public void testGetFactory() {
        System.out.println("getFactory");
        HashPowerset<String> instance = 
                new HashPowerset<String>((EventSetFactory<String>) this, (EventKey<String>)this);
        assertEquals(this, instance.getFactory());
    }

    /**
     * Test of setFactory method, of class HashPowerset.
     */
    public void testSetFactory() {
        System.out.println("setFactory");
        HashPowerset<String> instance =
                new HashPowerset<String>((EventSetFactory<String>) other, (EventKey<String>)this);
        instance.setFactory(this);
        assertEquals(this, instance.getFactory());
   }

    /**
     * Test of execute method, of class HashPowerset.
     */
    public void testExecute_AddEventTrigger_Event() {
        System.out.println("execute add");
        HashPowerset<String> instance = new HashPowerset<String>((EventKey<String>)this);
        instance.execute((AddEventTrigger) this, eventA1);
        EventSet eset = instance.get(eventAKey);
        assertTrue(eset != null);
        assertTrue(eset.contains(eventA1));
    }

    /**
     * Test of execute method, of class HashPowerset.
     */
    public void testExecute_AddEventTrigger_Event2_SameKey() {
        System.out.println("execute add, 2 events, same key");
        HashPowerset<String> instance = 
                new HashPowerset<String>((EventSetFactory<String>) this, (EventKey<String>)this);
        instance.execute((AddEventTrigger) this, eventA1);
        instance.execute((AddEventTrigger) this, eventA2);
        assertTrue(instance.size() == 1);
        EventSet eset = instance.get(eventAKey);
        assertTrue(eset != null);
        assertTrue(eset.size() == 2);
        assertTrue(eset.contains(eventA1));
        assertTrue(eset.contains(eventA2));
    }


    /**
     * Test of execute method, of class HashPowerset.
     */
    public void testExecute_AddEventTrigger_Event2_DiffKey() {
        System.out.println("execute add, 2 events, different keys");
        HashPowerset<String> instance =
                new HashPowerset<String>((EventSetFactory<String>) this, (EventKey<String>)this);
        instance.execute((AddEventTrigger) this, eventA1);
        instance.execute((AddEventTrigger) this, eventB1);
        assertTrue(instance.size() == 2);
        EventSet eset = instance.get(eventAKey);
        assertTrue(eset.contains(eventA1));
        eset = instance.get(eventBKey);
        assertTrue(eset.contains(eventB1));
    }

    /**
     * Test of execute method, of class HashPowerset.
     */
    public void testExecute_RemoveEventTrigger_Event() {
        System.out.println("execute remove");
        HashPowerset<String> instance =
                new HashPowerset<String>((EventSetFactory<String>) this, (EventKey<String>)this);
        instance.setPrune(false);
        instance.execute((AddEventTrigger) this, eventA1);
        instance.execute((RemoveEventTrigger) this, eventA1);
        EventSet eset = instance.get(eventAKey);
        assertTrue(eset != null);
        assertFalse(eset.contains(eventA1));
    }


    /**
     * Test of execute method, of class HashPowerset.
     */
    public void testExecute_RemoveEventTrigger_Event2_DiffKey() {
        System.out.println("execute remove, 2 events, different keys");
        HashPowerset<String> instance =
                new HashPowerset<String>((EventSetFactory<String>) this, (EventKey<String>)this);
        instance.setPrune(false);
        instance.execute((AddEventTrigger) this, eventA1);
        instance.execute((AddEventTrigger) this, eventB1);
        instance.execute((RemoveEventTrigger) this, eventA1);
        instance.execute((RemoveEventTrigger) this, eventB1);
        EventSet eset = instance.get(eventAKey);
        assertTrue(eset != null);
        assertFalse(eset.contains(eventA1));
        eset = instance.get(eventBKey);
        assertTrue(eset != null);
        assertFalse(eset.contains(eventB1));
    }


    /**
     * Test of execute method, of class HashPowerset.
     */
    public void testExecute_RemoveEventTrigger_Prune() {
        System.out.println("execute remove, leaving eventset empty so it is pruned");
        HashPowerset<String> instance =
                new HashPowerset<String>((EventSetFactory<String>) this, (EventKey<String>)this);
        instance.execute((AddEventTrigger) this, eventA1);
        instance.execute((RemoveEventTrigger) this, eventA1);
        EventSet eset = instance.get(eventAKey);
        assertTrue(eset == null);
    }

    /**
     * Test of registerAction method, of class HashPowerset.
     */
    public void testRegisterAction() {
        System.out.println("registerAction");
        HashPowerset<String> instance = new HashPowerset<String>((EventKey<String>)this);
        // register the test class to receive new set triggers
        instance.registerAction((NewSetAction<String>) this);
        instance.execute((AddEventTrigger) this, eventA1);
        // Adding an event to the powerset should create a new eventset with its key
        assertTrue(this.map.containsKey(eventAKey));
    }

    /**
     * Test of unregisterAction method, of class HashPowerset.
     */
    public void testUnregisterAction() {
        System.out.println("unregisterAction");
        HashPowerset<String> instance = new HashPowerset<String>((EventKey<String>)this);
        // register the test class to receive new set triggers and add an event
        instance.registerAction((NewSetAction<String>) this);
        instance.execute((AddEventTrigger) this, eventA1);
        // unregister the test class and add an event with a different key
        instance.unregisterAction((NewSetAction<String>) this);
        instance.execute((AddEventTrigger) this, eventB1);
        // We should have an entry for the first key but not the second
        assertTrue(this.map.containsKey(eventAKey));
        assertFalse(this.map.containsKey(eventBKey));
    }

}
