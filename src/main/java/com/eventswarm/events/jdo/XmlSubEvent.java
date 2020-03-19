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

import com.eventswarm.events.EventPart;
import com.eventswarm.events.Header;
import com.eventswarm.events.NestedEvent;
import com.eventswarm.events.SubEvent;
import org.w3c.dom.Node;

import javax.xml.xpath.XPathExpressionException;
import java.util.Date;
import java.util.Map;

/**
 * SubEvent class for OrgJsonEvent instances, allowing us to split a received JSON object into a set of subevents
 * for further processing.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class XmlSubEvent extends XmlEventImpl implements SubEvent<Node> {
    protected transient XmlEventImpl parent;

    /**
     * Create a sub-event with the specified parent and subordinate XML node.
     *
     * It's up to the caller to ensure that the subordinate XML node actually belongs to the parent.
     * This constructor is intended for use where the subevent factory method is iterating through a set of
     * subordinate objects that are to become subevents.
     *
     * @param parent
     * @param xml
     */
    public XmlSubEvent(XmlEventImpl parent, Node xml) {
        super(createSubHeader(parent, parent.getHeader().getTimestamp()), xml);
        this.parent = parent;
        this.eventParts.put(PARENT_EVENTPART, new JdoNestedEvent(parent));
    }

    /**
     * Create a sub-event with the specified parent and subordinate XML node with the specified timestamp.
     *
     * It's up to the caller to ensure that the subordinate XML node actually belongs to the parent.
     * This constructor is intended for use where the subevent factory method is iterating through a set of
     * subordinate objects that are to become subevents.
     *
     * @param parent
     * @param xml
     */
    public XmlSubEvent(XmlEventImpl parent, Node xml, Date timestamp) {
        super(createSubHeader(parent, timestamp), xml);
        this.parent = parent;
        this.eventParts.put(PARENT_EVENTPART, new JdoNestedEvent(parent));
    }

    /**
     * Create a sub-event with the specified parent and the subordinate object at the specified path.
     *
     * @param parent
     * @param path
     */
    public XmlSubEvent(XmlEventImpl parent, String path) throws XPathExpressionException {
        super(createSubHeader(parent, parent.getHeader().getTimestamp()), parent.getNode(path));
        this.parent = parent;
        this.eventParts.put(PARENT_EVENTPART, new JdoNestedEvent(parent));
    }

    /**
     * Create an OrgJsonSubEvent with the specified parts.
     *
     * This method is primarily intended for subclasses that want to add other things to the SubEvent and should
     * not generally be used.
     *
     * @param header
     * @param eventParts
     */
    public XmlSubEvent(Header header, Map<String, EventPart> eventParts) {
        super(header, eventParts);
        this.parent = (XmlEventImpl) ((NestedEvent) eventParts.get(PARENT_EVENTPART)).getNestedEvent();
    }

    private static Header createSubHeader(XmlEventImpl parent, Date timestamp) {
        return new JdoHeader(timestamp, parent.getHeader().getSource());
    }

    public XmlEventImpl getParent() {
        return this.parent;
    }

    public Node getSubordinate() {
        return this.xml;
    }
}
