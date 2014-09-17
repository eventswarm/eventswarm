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

public interface Header extends EventPart {

  public java.util.Date getTimestamp();

  public Source getSource();

  public CausalityVector getCausality();

  public Event getInReplyTo();

  public boolean isReply();

  public int getSequenceNumber();

  @Deprecated
  public String madeId();

  public String getEventId();

  public Event getReplyTo();
  /* {null=Events, and in particular system generated events, can identify a preceding event that should be referenced when replying to this event. This field should always be set. In most cases, it will refer to <code>this</code>. Think of this like forwarding: if something is forwarded to you by an intermediary, you reply to the original source, not the intermediary. 

There is a risk that this field could be mis-used and we possibly need to define rules around its use in subsequent iterations.}*/


  public void setReplyTo(Event event);
}