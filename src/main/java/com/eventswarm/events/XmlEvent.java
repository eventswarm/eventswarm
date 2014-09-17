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

import com.eventswarm.abstractions.ValueRetriever;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;

/**
 * Event interface for Json events, just allowing us to retrieve each attribute of the Json by key.
 *
 * Simple paths using the PATH_SEPARATOR between name elements must be supported
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface XmlEvent extends Event {

    /** separator between name elements in a path */
    public static String PATH_SEPARATOR="/";

    /** default part name for the JsonPart of a JsonEvent */
    public static String XML_PART_NAME="XML";

    public static Logger logger = Logger.getLogger(XmlEvent.class);

    public Node getRoot();

    public Node getNode(String path) throws XPathExpressionException;

    public NodeList getNodeList(String path) throws XPathExpressionException;

    public int getInt(String path) throws XPathExpressionException;

    public boolean getBoolean(String path) throws XPathExpressionException;

    public double getDouble(String path) throws XPathExpressionException;

    public long getLong(String path) throws XPathExpressionException;

    public String getString(String path) throws XPathExpressionException;

    public boolean has(String path) throws XPathExpressionException;

    public boolean isEmpty(String path) throws XPathExpressionException;

    public String getXmlString();

        /**
         * Retriever class for retrieving integer attribute values
         */
    public static class IntegerRetriever implements ValueRetriever<Integer> {
        private String path;

        public IntegerRetriever(String path) {
            this.path = path;
        }

        @Override
        public Integer getValue(Event event) {
            try {
                return (XmlEvent.class.isInstance(event) ? ((XmlEvent) event).getInt(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving integer value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving long attribute values
     */
    public static class LongRetriever implements ValueRetriever<Long> {
        private String path;

        public LongRetriever(String path) {
            this.path = path;
        }

        @Override
        public Long getValue(Event event) {
            try {
                return (XmlEvent.class.isInstance(event) ? ((XmlEvent) event).getLong(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving long value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving String attribute values
     */
    public static class StringRetriever implements ValueRetriever<String> {
        private String path;

        public StringRetriever(String path) {
            this.path = path;
        }

        @Override
        public String getValue(Event event) {
            try {
                return (XmlEvent.class.isInstance(event) ? ((XmlEvent) event).getString(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving string value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving String attribute values folded to lower case for case-insensitive comparisons
     */
    public static class DowncaseStringRetriever implements ValueRetriever<String> {
        private String path;

        public DowncaseStringRetriever(String path) {
            this.path = path;
        }

        @Override
        public String getValue(Event event) {
            try {
                return (XmlEvent.class.isInstance(event) ? ((XmlEvent) event).getString(path).toLowerCase() : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving downcase string value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving Double attribute values
     */
    public static class DoubleRetriever implements ValueRetriever<Double> {
        private String path;

        public DoubleRetriever(String path) {
            this.path = path;
        }

        @Override
        public Double getValue(Event event) {
            try {
                return (XmlEvent.class.isInstance(event) ? ((XmlEvent) event).getDouble(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving double value at path " + path, exc);
                return null;
            }
        }
    }

    /**
     * Retriever class for retrieving boolean attribute values
     */
    public static class BooleanRetriever implements ValueRetriever<Boolean> {
        private String path;

        public BooleanRetriever(String path) {
            this.path = path;
        }

        @Override
        public Boolean getValue(Event event) {
            try {
                return (XmlEvent.class.isInstance(event) ? ((XmlEvent) event).getBoolean(path) : null);
            } catch (Exception exc) {
                logger.warn("Error retrieving boolean value at path " + path, exc);
                return null;
            }
        }
    }
}
