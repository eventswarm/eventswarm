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

import java.util.List;

/**
 * Parent class for matchers that implement conjunction of other matchers (i.e.
 * AND or OR).
 *
 * @author andyb
 */
public abstract class ComplexMatcher implements Matcher {
    
    protected List<Matcher> parts;

    protected ComplexMatcher() {
        super();
    }

    public ComplexMatcher(List<Matcher> parts) {
        this.parts = parts;
    }

    public List<Matcher> getParts() {
        return parts;
    }

    private void setParts(List<Matcher> parts) {
        this.parts = parts;
    }
}
