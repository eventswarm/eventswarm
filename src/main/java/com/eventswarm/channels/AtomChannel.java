package com.eventswarm.channels;

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.Source;
import com.eventswarm.events.Sources;
import com.eventswarm.events.jdo.JdoHeader;
import com.eventswarm.events.jdo.XmlEventImpl;
import com.eventswarm.util.EventTriggerDelegate;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import javax.xml.xpath.*;


/**
 * Class to parse an ATOM feeds and create XmlEvent instances from each entry in a query response or
 * subscription notification.
 *
 * Note that this class assumes that the feed includes a &lt;link rel=self href="http://..." /&gt; element identifying
 * the source. Superfeedr, for example, has a custom status header instead, so an extension is required to work
 * with Superfeedr.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 * To change this template use File | Settings | File Templates.
 */
public class AtomChannel implements HttpContentHandler, AddEventTrigger {
    private EventTriggerDelegate delegate;
    private DocumentBuilder builder;
    private XPath xpath;
    private XPathExpression entryPath;
    private XPathExpression selfHrefPath;
    private long count;
    private long errors;


    public static final String FEED_PATH = "/feed";
    public static final String ATOM_ENTRY_PATH = FEED_PATH + "/entry";
    public static final String SELF_HREF_PATH = FEED_PATH + "/link[@rel='self']/@href";

    private static final Logger logger = Logger.getLogger(AtomChannel.class);

    /**
     * Create a new channel instance that can handle incoming ATOM via the HttpContentHandler interface
     */
    public AtomChannel() {
        this.delegate = new EventTriggerDelegate(this);
        makeBuilder();
        xpath = XPathFactory.newInstance().newXPath();
        this.count = 0;
        this.errors = 0;
    }

    private XPathExpression getEntryPath() {
        if (entryPath == null) {
            try {
                entryPath = xpath.compile(ATOM_ENTRY_PATH);
            } catch (XPathExpressionException exc) {
                logger.fatal("WTF!!? Could not parse xpath");
            }
        }
        return entryPath;
    }

    private XPathExpression getSelfHrefPath() {
        if (selfHrefPath == null) {
            try {
                selfHrefPath = xpath.compile(SELF_HREF_PATH);
            } catch (XPathExpressionException exc) {
                logger.fatal("WTF!!? Could not parse xpath");
            }
        }
        return selfHrefPath;
    }

    /**
     *
     * @return number of events created by this channel
     */
    public long getCount() {
        return count;
    }

    /**
     *
     * @return number of errors encountered by this channel
     */
    public long getErrors() {
        return errors;
    }

    /**
     * Create a builder for XML documents parsed by this channel
     */
    private void makeBuilder() {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException exc) {
            logger.error("Failed to create XML document builder", exc);
        }
    }

    /**
     * As per the interface contract, extract events from the notification or request result
     * and pass them onwards to downstream actions.
     *
     * In this implementation, HTTP headers are ignored, so only an XMLEvent with the entry content is created.
     *
     * @param subs_id ID of subscription providing events, typically just the query URL
     * @param body InputStream for reading the HTTP request/response body
     * @param headers Map of HTTP headers
     */
    public void handle(String subs_id, InputStream body, Map<String, List<String>> headers) {
        try {
            Document doc = builder.parse(body);
            Source source = getSource(doc);
            //logger.debug("Received doc: " + prettyPrint(doc, doc)); // very expensive, so comment out when not required
            NodeList nodes = (NodeList) getEntryPath().evaluate(doc, XPathConstants.NODESET);
            logger.debug("Processing " + nodes.getLength() + " entries from " + subs_id);
            for (int i = 0; i < nodes.getLength(); i++) {
                Event event = makeEvent(nodes.item(i), source);
                if (event != null) {
                    delegate.fire(event);
                } else {
                    logger.warn ("Unable to construct event from entry " + i);
                }
            }
        } catch (IOException exc) {
            logger.error("Error reading feed body", exc);
            errors++;
        } catch (SAXException exc) {
            logger.error("Error parsing feed body", exc);
            errors++;
        } catch (XPathExpressionException exc) {
            logger.error("Error evaluating entry or status path", exc);
            errors++;
        }
    }

    /**
     * Method to create an event from an entry node in the Atom feed.
     *
     * @param item
=     * @return New event or null if event could not be created
     */
    protected Event makeEvent(Node item, Source source) {
        try {
            String id = getId(item);
            Date timestamp = getTimestamp(item);
            count++;
            return new XmlEventImpl(new JdoHeader(timestamp, source, id), item);
        } catch (XPathExpressionException exc) {
            logger.error("Xpath expression error: ", exc);
            errors++;
            return null;
        }
    }

    /**
     * Extract the hostname of the ATOM feed source and return it as a source object
     *
     * @param doc
     * @return
     */
    protected Source getSource(Document doc) throws XPathExpressionException, MalformedURLException {
        String url = getSelfHrefPath().evaluate(doc);
        logger.debug("Parsing URL from " + url);
        URL sourceUrl = new URL(url);
        return Sources.cache.getSourceByName(sourceUrl.getHost());
    }
    /**
     * Method to create an id from an item node and/or the status node of the document.
     *
     * Default behaviour is to extract the 'id' element of the entry. Subclasses should override if this is not
     * appropriate.
     *
     * @param item
     * @return id for this item
     * @throws XPathExpressionException
     */
    protected String getId(Node item) throws XPathExpressionException {
        return xpath.evaluate("id", item);
    }

    /**
     * Method to extract a suitable timestamp from an item node and/or the status node of the document
     *
     * Default behaviour is to extract the 'updated' element of the entry and parse using the
     * javax.xml.bind.DatatypeConverter. Subclasses should override if this is not appropriate.
     *
     * @param item
     * @return timestamp for this item
     * @throws XPathExpressionException
     */
    protected Date getTimestamp(Node item) throws XPathExpressionException {
        return DatatypeConverter.parseDateTime(xpath.evaluate("updated", item)).getTime();
    }

    @Override
    public void registerAction(AddEventAction action) {
        delegate.registerAction(action);
    }

    @Override
    public void unregisterAction(AddEventAction action) {
        delegate.unregisterAction(action);
    }

    public static String prettyPrint(Document doc, Node node) {
        DOMImplementationLS ls = (DOMImplementationLS) doc.getImplementation().getFeature("LS", "3.0");
        LSSerializer serializer = ls.createLSSerializer();
        serializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        LSOutput lsout = ls.createLSOutput();
        lsout.setEncoding("UTF-8");
        StringWriter stringWriter = new StringWriter();
        lsout.setCharacterStream(stringWriter);
        serializer.write(node, lsout);
        return stringWriter.toString();
    }
}
