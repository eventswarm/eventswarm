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
package com.eventswarm.events;

import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.JdoSource;
import com.eventswarm.events.jdo.XmlEventImpl;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class XmlEventTest {
    Header header = new JdoHeader(new Date(), new JdoSource("localhost"));
    DocumentBuilder builder;
    XPath xpath;
    String VERSION = "<?xml version=\"1.0\"?>";

    public Node parse(String str) throws Exception {
        return builder.parse(new ByteArrayInputStream((VERSION + str).getBytes()));
    }

    @Before
    public void setup() throws Exception {
        builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        xpath = XPathFactory.newInstance().newXPath();
    }

    @Test
    public void getInt() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<doc><a>1</a><b></b></doc>"));
        assertEquals(1, instance.getInt("doc/a"));
    }

    @Test
    public void getBoolean() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<doc><a>1</a><b>true</b></doc>"));
        assertEquals(true, instance.getBoolean("doc/b"));
    }

    @Test
    public void getDouble() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<doc><a>1.15</a><b>true</b></doc>"));
        assertEquals(1.15, instance.getDouble("doc/a"));
    }

    @Test
    public void getLong() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<doc><a>1.15</a><b>1038401717746111341</b></doc>"));
        assertEquals(1038401717746111341L, instance.getLong("doc/b"));
    }

    @Test
    public void getString() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<a>howzat</a>"));
        assertEquals("howzat", instance.getString("a"));
    }

    @Test
    public void xmlNode() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<doc><a>1</a><b><c><d>2</d></c></b></doc>"));
        Node result = instance.getNode("doc/b");
        assertNotNull(result);
        System.out.println(XmlEventImpl.nodeToString(result));
        assertEquals("b", result.getNodeName());
        assertEquals("2", xpath.compile("c/d").evaluate(result, XPathConstants.STRING));
    }

    @Test
    public void xmlNodeList() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<doc><a>1</a><b><c>2</c></b></doc>"));
        NodeList result = instance.getNodeList("/doc/*");
        assertNotNull(result);
        assertEquals("a", result.item(0).getNodeName());
        assertEquals("b", result.item(1).getNodeName());
    }

    @Test
    public void integerRetriever() throws Exception {
        XmlEvent.IntegerRetriever retriever = new XmlEvent.IntegerRetriever("a");
        XmlEvent instance = new XmlEventImpl(header, parse("<a>1</a>"));
        assertEquals(1, retriever.getValue(instance));
    }

    @Test
    public void longRetriever() throws Exception {
        XmlEvent.LongRetriever retriever = new XmlEvent.LongRetriever("a");
        XmlEvent instance = new XmlEventImpl(header, parse("<a>1038401717746111341</a>"));
        assertEquals(1038401717746111341L, retriever.getValue(instance));
    }

    @Test
    public void doubleRetriever() throws Exception {
        XmlEvent.DoubleRetriever retriever = new XmlEvent.DoubleRetriever("b");
        XmlEvent instance = new XmlEventImpl(header, parse("<b>3.14159</b>"));
        assertEquals(3.14159, retriever.getValue(instance));
    }

    @Test
    public void stringRetriever() throws Exception {
        XmlEvent.StringRetriever retriever = new XmlEvent.StringRetriever("b");
        XmlEvent instance = new XmlEventImpl(header, parse("<b>howzat</b>"));
        assertEquals("howzat", retriever.getValue(instance));
    }


    @Test
    public void downcaseStringRetriever() throws Exception {
        XmlEvent.DowncaseStringRetriever retriever = new XmlEvent.DowncaseStringRetriever("b");
        XmlEvent instance = new XmlEventImpl(header, parse("<b>Howzat</b>"));
        assertEquals("howzat", retriever.getValue(instance));
    }

    @Test
    public void booleanRetriever() throws Exception {
        XmlEvent.BooleanRetriever retriever = new XmlEvent.BooleanRetriever("a");
        XmlEvent instance = new XmlEventImpl(header, parse("<a>true</a>"));
        assertEquals(true, retriever.getValue(instance));
    }

    @Test
    public void hasTrue() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<a>true</a>"));
        assertEquals(true, instance.has("a"));
    }

    @Test
    public void hasFalseSingleLevel() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<a>true</a>"));
        assertEquals(false, instance.has("b"));
    }

    @Test
    public void hasFalseMultiLevel() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<a><b>true</b></a>"));
        assertEquals(false, instance.has("a/c"));
    }

    @Test
    public void isEmptyTrueEmpty() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<a></a>"));
        assertEquals(true, instance.isEmpty("a"));
    }

    @Test
    public void isEmptyTrueNotPresent() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<a></a>"));
        assertEquals(true, instance.isEmpty("b"));
    }

    @Test
    public void isEmptyFalse() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<a>a</a>"));
        assertEquals(false, instance.isEmpty("a"));
    }

    @Test
    public void xmlToString() throws Exception {
        XmlEvent instance = new XmlEventImpl(header, parse("<doc><a>1</a><b>Howzat</b></doc>"));
        Node result = parse(instance.getXmlString());
        assertEquals(2, result.getFirstChild().getChildNodes().getLength());
        assertEquals("1", xpath.evaluate("doc/a", result));
        assertEquals("Howzat", xpath.evaluate("doc/b", result));
    }
}
