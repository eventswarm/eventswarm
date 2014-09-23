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

public interface Event extends Comparable<Event> {

      
  public java.util.Set<EventPart> getParts();

  public java.util.Map<String,EventPart> getPartsMap();
  
  public Header getHeader();

  public boolean isBefore(Event event);

  public boolean isAfter(Event event);

  public boolean isConcurrent(Event event);

  public int order(Event event);

  public boolean hasPart(Class type);
  
  public boolean hasPart(String key);

  public EventPart getPart(Class type);
  
  public EventPart getPart(String key);
  
  public java.util.Set<EventPart> getParts(Class type);


    public static class SourceRetriever implements ValueRetriever<String> {
        @Override
        public String getValue(Event event) {
            return event.getHeader().getSource().getId();
        }
    }
}