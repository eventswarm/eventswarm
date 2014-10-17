package com.eventswarm.eventset;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.DuplicateEventAction;
import com.eventswarm.DuplicateEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Header;
import com.eventswarm.events.jdo.JdoEvent;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.TestEvents;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public class DuplicateFilterTest implements AddEventAction, DuplicateEventAction {
    ArrayList<Event> events;
    HashMap<Event,Event> duplicates;

    @Before
    public void setUp() throws Exception {
        events = new ArrayList<Event>();
        duplicates = new HashMap<Event,Event>();
    }

    @Test
    public void testConstruct() throws Exception {
        DuplicateFilter<String> instance = new DuplicateFilter<String>(Event.ID_RETRIEVER);
        assertThat(instance.getRetriever(), is(Event.ID_RETRIEVER));
        assertThat(instance.getWindow(), instanceOf(DiscreteTimeWindow.class));
        assertThat(instance.getWindow().size(), is(0));
        assertNotNull(instance.dupeMap);
        assertThat(instance.dupeMap.size(), is(0));
        assertNotNull(instance.dupeActions);
        assertThat(instance.dupeActions.size(), is(0));
    }

    @Test
    public void testConstructEmptyWindow() throws Exception {
        EventSet window = new EventSet();
        DuplicateFilter<String> instance = new DuplicateFilter<String>(Event.ID_RETRIEVER, window);
        assertThat(instance.getRetriever(), is(Event.ID_RETRIEVER));
        assertThat(instance.getWindow(), is(window));
        assertThat(instance.getWindow().size(), is(0));
        assertNotNull(instance.dupeMap);
        assertThat(instance.dupeMap.size(), is(0));
        assertNotNull(instance.dupeActions);
        assertThat(instance.dupeActions.size(), is(0));
    }

    @Test
    public void testConstructNonEmptyWindow() throws Exception {
        EventSet window = new EventSet();
        window.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        DuplicateFilter<String> instance = new DuplicateFilter<String>(Event.ID_RETRIEVER, window);
        assertThat(instance.getRetriever(), is(Event.ID_RETRIEVER));
        assertThat(instance.getWindow(), is(window));
        assertThat(instance.getWindow().size(), is(1));
        assertNotNull(instance.dupeMap);
        assertThat(instance.dupeMap.size(), is(1));
        assertTrue(instance.dupeMap.containsKey(TestEvents.jdoEvent.getHeader().getEventId()));
        assertNotNull(instance.dupeActions);
        assertThat(instance.dupeActions.size(), is(0));
    }

    @Test
    public void testAddNotDupe() throws Exception {
        DuplicateFilter<String> instance = new DuplicateFilter<String>(Event.ID_RETRIEVER);
        instance.registerAction((AddEventAction) this);
        instance.registerAction((DuplicateEventAction) this);
        Event original = TestEvents.jdoEvent;
        Header dupeHeader = new JdoHeader(original.getHeader().getTimestamp(), original.getHeader().getSource(), original.getHeader().getEventId());
        Event dupe = new JdoEvent(dupeHeader, (Map<String,EventPart>) null);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        assertTrue(instance.dupeMap.containsKey(TestEvents.jdoEvent.getHeader().getEventId()));
        assertThat(events.size(), is(1));
        assertSame(TestEvents.jdoEvent, events.get(0));
    }

    @Test
    public void testAddDupe() throws Exception {
        DuplicateFilter<String> instance = new DuplicateFilter<String>(Event.ID_RETRIEVER);
        instance.registerAction((AddEventAction) this);
        instance.registerAction((DuplicateEventAction) this);
        Event original = TestEvents.jdoEvent;
        Header dupeHeader = new JdoHeader(original.getHeader().getTimestamp(), original.getHeader().getSource(), original.getHeader().getEventId());
        Event dupe = new JdoEvent(dupeHeader, (Map<String,EventPart>) null);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        instance.execute((AddEventTrigger) null, dupe);
        assertThat(events.size(), is(1));
        assertSame(TestEvents.jdoEvent, events.get(0));
        assertTrue(duplicates.containsKey(TestEvents.jdoEvent));
        assertSame(dupe, duplicates.get(TestEvents.jdoEvent));
    }

    @Test
    public void testWindowShift() throws Exception {
        EventSet window = new LastNWindow(1);
        DuplicateFilter<String> instance = new DuplicateFilter<String>(Event.ID_RETRIEVER, window);
        instance.registerAction((AddEventAction) this);
        instance.registerAction((DuplicateEventAction) this);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEvent);
        instance.execute((AddEventTrigger) null, TestEvents.jdoEventAfterDiffSrcAfterSeq);
        assertFalse(instance.dupeMap.containsKey(TestEvents.jdoEvent.getHeader().getEventId()));
        assertTrue(instance.dupeMap.containsKey(TestEvents.jdoEventAfterDiffSrcAfterSeq.getHeader().getEventId()));
        assertThat(events.size(), is(2));
        assertSame(TestEvents.jdoEvent, events.get(0));
        assertSame(TestEvents.jdoEventAfterDiffSrcAfterSeq, events.get(1));
    }

    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        events.add(event);
    }

    @Override
    public void execute(DuplicateEventTrigger trigger, Event original, Event duplicate) {
        duplicates.put(original,duplicate);
    }
}
