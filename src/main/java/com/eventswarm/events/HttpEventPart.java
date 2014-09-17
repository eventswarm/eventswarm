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

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for EventParts encapsulating the details of an HTTP request (excluding the body)
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface HttpEventPart extends EventPart {
    /** Default part name for the HTTP EventPart */
    public static String HTTP_PART_NAME="HTTP";

    InetSocketAddress getRemoteAddress();

    String getRequestMethod();

    URI getRequestUri();

    Date getRequestDate();

    List<String> getHttpHeader(String name);

    String getFirstHeaderValue(String name);

    Map<String,List<String>> getHeaders();
}
