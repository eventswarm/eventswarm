package com.eventswarm.channels;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.Source;
import com.eventswarm.events.XmlEvent;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public class AtomChannelTest implements AddEventAction {
    InputStream fhir_obs;
    DocumentBuilder builder;
    AtomChannel instance;
    ArrayList<Event> events;


    @Before
    public void setup() throws Exception {
        fhir_obs = AtomChannelTest.class.getClassLoader().getResourceAsStream("fixtures/fhir_observation_result.xml");
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        events = new ArrayList<Event>();
    }

    @Test
    public void testConstruct() throws Exception {
        instance = new AtomChannel();
    }

    @Test
    public void testGetSource() throws Exception {
        instance = new AtomChannel();
        Document doc = builder.parse(fhir_obs);
        Source source = instance.getSource(doc);
        assertNotNull(source);
        assertThat(source.getSourceId(), is("fhir.healthintersections.com.au"));
    }


    @Test
    public void testGetId() throws Exception {
        instance = new AtomChannel();
        Document doc = builder.parse(fhir_obs);
        String id = instance.getId(doc.getElementsByTagName("entry").item(0));
        assertNotNull(id);
        assertThat(id, is("http://fhir.healthintersections.com.au/open/Observation/1"));
    }

    @Test
    public void testGetTimestamp() throws Exception {
        instance = new AtomChannel();
        Document doc = builder.parse(fhir_obs);
        Date timestamp = instance.getTimestamp(doc.getElementsByTagName("entry").item(0));
        assertNotNull(timestamp);
        Date localTime = new Date(114, 8, 13, 10, 49, 27);
        Date expected = new Date(localTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
        assertThat(timestamp, is(expected));
    }

    @Test
    public void testMakeEvent() throws Exception {
        instance = new AtomChannel();
        Document doc = builder.parse(fhir_obs);
        Event event = instance.makeEvent(doc.getElementsByTagName("entry").item(0), instance.getSource(doc));
        assertThat(event, instanceOf(XmlEvent.class));
        assertThat(event.getHeader().getEventId(), is("http://fhir.healthintersections.com.au/open/Observation/1"));
        assertThat(event.getHeader().getSource().getSourceId(), is("fhir.healthintersections.com.au"));
        Date localTime = new Date(114, 8, 13, 10, 49, 27);
        Date expected = new Date(localTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
        assertThat(event.getHeader().getTimestamp(), is(expected));
        XmlEvent xev = (XmlEvent) event;
        assertTrue(xev.has("content"));
        assertTrue(xev.has("content/Observation"));
    }

    @Test
    public void testHandle() throws Exception {
        instance = new AtomChannel();
        instance.registerAction(this);
        instance.handle("http://localhost", fhir_obs, null);
        assertThat(events.size(), is(50));
        assertThat(instance.getCount(), is(50L));
        assertThat(instance.getErrors(), is(0L));
    }

    /**
     * This is really an integration test, using our HttpClient class as the source
     *
     * @throws Exception
     */
    @Test
    public void testHandleWithHttpClient() throws Exception {
        instance = new AtomChannel();
        instance.registerAction(this);
        HttpClient client = new HttpClient(instance, "*");
        String target = "http://fhir-dev.healthintersections.com.au/open/Observation/_search";
        // TODO: work out why we get a 500 error when we try to send url-encoded parameters in the body
        Map<String,String> params = new HashMap();
        params.put("_count", "5");
        int result = client.getRequest(new URL(target), params);
        assertThat(result, is(200));
        System.out.println("Received " + instance.getCount() + " entries");
        assertThat(instance.getCount(), is(5L));
    }


    @Override
    public void execute(AddEventTrigger trigger, Event event) {
        if (XmlEvent.class.isInstance(event)) {
            events.add(event);
        } else {
            System.out.println("Channel created non-XML event");
        }
    }
}
