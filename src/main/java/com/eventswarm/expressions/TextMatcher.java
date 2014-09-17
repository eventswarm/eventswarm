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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eventswarm.expressions;

import com.eventswarm.AddEventTrigger;
import com.eventswarm.events.Event;
import com.eventswarm.events.Text;
/**
 *
 * @author andyb
 */

// Deprecating in favour of matchers based on the retriever pattern. Use StringValueRegexMatcher instead.
@Deprecated
public class TextMatcher implements Matcher {

    private String text = null;

    private TextMatcher() {
        super();
    }

    public TextMatcher(String text) {
        super();
        this.setText(text);
    }

    /**
     * Return true if the text is contained in the text of a Text-capable event.
     *
     * @param event
     * @return
     */
    @Override
    public boolean matches(Event event) {
       return (Text.class.isInstance(event) &&
               ((Text) event).getText().contains(this.text));
    }

    public String getText() {
        return text;
    }

    private void setText(String text) {
        this.text = text;
    }
}
