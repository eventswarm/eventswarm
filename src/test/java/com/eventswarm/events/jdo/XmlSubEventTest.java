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
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class XmlSubEventTest {
    XmlEventImpl parent;
    Node object;

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
        object = parse("<doc><a><c>1</c><d>2</d></a><b><e>lala</e><f>true</f></b></doc>");
        parent = new XmlEventImpl(new JdoHeader(new Date(), new JdoSource("XmlSubEventTest")), object);
    }

    @Test
    public void constructFromObjects() throws Exception {
        XmlSubEvent instance = new XmlSubEvent(parent, parent.getNode("doc/a"));
        assertEquals(parent, instance.getParent());
        assertEquals(object.getFirstChild().getFirstChild(), instance.getSubordinate());
        assertEquals(1, instance.getInt("c"));
        assertEquals(2, instance.getInt("d"));
    }

    @Test
    public void constructWithTimestamp() throws Exception {
        Date timestamp = new Date(9836598364139861L);
        XmlSubEvent instance = new XmlSubEvent(parent, parent.getNode("doc/a"), timestamp);
        assertEquals(parent, instance.getParent());
        assertEquals(object.getFirstChild().getFirstChild(), instance.getSubordinate());
        assertEquals(1, instance.getInt("c"));
        assertEquals(2, instance.getInt("d"));
        assertEquals(timestamp, instance.getHeader().getTimestamp());
        assertNotSame(parent.getHeader().getTimestamp(), instance.getHeader().getTimestamp());
    }
}
