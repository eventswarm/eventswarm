package com.eventswarm.channels;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.Before;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public class HttpClientTest implements HttpContentHandler {
    String subs_id;
    Map<String,List<String>> headers;
    InputStream body;

    @Before
    public void setup() throws Exception {
    }

    @Test
    public void testConstructHandler() throws Exception {
        HttpClient instance = new HttpClient(this);
        assertNotNull(instance);
        assertThat(instance.getHandler(), is((HttpContentHandler) this));
        assertNull(instance.getAccept());
        assertNull(instance.getUser());
        assertNull(instance.getPassword());
    }

    @Test
    public void testConstructHandlerAccept() throws Exception {
        HttpClient instance = new HttpClient(this, "application/atom+xml");
        assertNotNull(instance);
        assertThat(instance.getHandler(), is((HttpContentHandler) this));
        assertThat(instance.getAccept(), is("application/atom+xml"));
        assertNull(instance.getUser());
        assertNull(instance.getPassword());
    }

    @Test
    public void testConstructAll() throws Exception {
        HttpClient instance = new HttpClient(this, "application/atom+xml", "andyb", "mypassword");
        assertNotNull(instance);
        assertThat(instance.getHandler(), is((HttpContentHandler) this));
        assertThat(instance.getAccept(), is("application/atom+xml"));
        assertThat(instance.getUser(), is("andyb"));
        assertThat(instance.getPassword(), is("mypassword"));
    }

    @Test
    public void testSimpleRequest() throws Exception {
        HttpClient instance = new HttpClient(this);
        String target = "http://deontik.com";
        int result = instance.getRequest(new URL(target), null);
        assertThat(result, is(200));
        assertThat(subs_id, is("http://deontik.com"));
        assertNotNull(headers);
        Document doc = Jsoup.parse(body, null, target);
        assertThat(doc.title(), is("Deontik"));
    }

    @Test
    public void testRequestWithParams() throws Exception {
        HttpClient instance = new HttpClient(this);
        String target = "http://deontik.com/blog";
        Map<String,String> params = new HashMap();
        params.put("feed", "rss2");
        int result = instance.getRequest(new URL(target), params);
        assertThat(result, is(200));
        assertThat(subs_id, is("http://deontik.com/blog?feed=rss2"));
        assertNotNull(headers);
        assertThat(headers.get("Content-Type").get(0), is("text/xml; charset=UTF-8"));
    }

    @Test
    public void testAuthenticatedPostRequest() throws Exception {
        Properties props = new Properties();
        props.load(this.getClass().getClassLoader().getResourceAsStream("superfeedr.properties"));
        HttpClient instance = new HttpClient(this, "*", props.getProperty("username"), props.getProperty("password"));
        String target = "https://push.superfeedr.com";
        Map<String,String> params = new HashMap();
        params.put("hub.mode", "subscribe");
        params.put("hub.callback", "http://pubsub.eventswarm.com");
        params.put("hub.topic", "http://deontik.com/blog/?feed=rss2");
        int result = instance.postRequest(new URL(target), params);
        assertThat(result, is(204));
    }

    @Override
    public void handle(String subs_id, InputStream body, Map<String, List<String>> headers) {
        this.subs_id = subs_id;
        this.body = body;
        this.headers = headers;
    }
}
