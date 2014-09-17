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

import com.eventswarm.events.Event;
import com.eventswarm.events.EventPart;
import com.eventswarm.events.Header;
import com.eventswarm.events.XmlEvent;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Event class for xml events received via HTTP and deserialized using the org.xml xmlObject class
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class XmlEventImpl extends JdoEvent implements Event, XmlEvent {
    // maintain local pointers to parts for convenience
    protected transient Node xml;
    private transient XPath xpath;

    private static Logger logger = Logger.getLogger(XmlEventImpl.class);

    public XmlEventImpl() {
        super();
    }

    /**
     * Construct an Event from a header and an XML node (typically a document)
     *
     * @param header
     * @param xml
     */
    public XmlEventImpl(Header header, Node xml) {
        super();
        this.xml = xml;
        this.header = header;
        this.eventParts = new HashMap<String, EventPart>();
        eventParts.put(XML_PART_NAME, new JdoPartWrapper<Node>(xml));
    }

    public XmlEventImpl(Header header, Map<String, EventPart> eventParts) {
        super(header, eventParts);
        this.xml = ((JdoPartWrapper<Document>)eventParts.get(XML_PART_NAME)).getWrapped();
    }

    private XPath getXPath() {
        if (xpath == null) {
            xpath = XPathFactory.newInstance().newXPath();
        }
        return xpath;
    }

    @Override
    public Node getRoot() {
        return xml;
    }

    public Node getXml() {
        return xml;
    }

    private void setXml(Document xml) {
        this.xml = xml;
    }

    @Override
    public int getInt(String path) throws XPathExpressionException, ClassCastException {
        return Integer.parseInt(getXPath().compile(path).evaluate(xml));
    }

    @Override
    public boolean getBoolean(String path) throws XPathExpressionException, ClassCastException {
        Boolean result = (Boolean) getXPath().evaluate(path, xml, XPathConstants.BOOLEAN);
        return result;
    }

    @Override
    public Node getNode(String path) throws XPathExpressionException, ClassCastException {
        Node result = (Node) getXPath().evaluate(path, xml, XPathConstants.NODE);
        return result;
    }

    @Override
    public NodeList getNodeList(String path) throws XPathExpressionException, ClassCastException {
        NodeList result = (NodeList) getXPath().evaluate(path, xml, XPathConstants.NODESET);
        return result;
    }

    public boolean isEmpty(String path) throws XPathExpressionException, ClassCastException {
        Node result = (Node) getXPath().evaluate(path, xml, XPathConstants.NODE);
        return (result == null || result.getTextContent().isEmpty());
    }

    public void setEvent(Event event) {
        ((JdoEventPart) eventParts.get(XML_PART_NAME)).setEvent(event);
    }

    public boolean has(String path) {
        try {
            Node result = (Node) getXPath().compile(path).evaluate(xml, XPathConstants.NODE);
            return result != null;
        } catch (XPathExpressionException exc) {
            return false;
        }
    }

    public double getDouble(String path) throws XPathExpressionException, ClassCastException {
        Double result = (Double) getXPath().compile(path).evaluate(xml, XPathConstants.NUMBER);
        return result;
    }

    public long getLong(String path) throws XPathExpressionException, ClassCastException {
        return (Long.parseLong(getXPath().compile(path).evaluate(xml)));
    }

    public String getString(String path) throws XPathExpressionException, ClassCastException {
        String result = (String) getXPath().compile(path).evaluate(xml, XPathConstants.STRING);
        return result;
    }

    @Override
    public String getXmlString() {
        try {
            return nodeToString(xml);
        } catch (Exception exc) {
            logger.error ("Error converting XML object to XML string", exc);
            return "";
        }
    }

    public static String nodeToString(Node node) throws Exception {
        StringWriter sw = new StringWriter();
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }

}
