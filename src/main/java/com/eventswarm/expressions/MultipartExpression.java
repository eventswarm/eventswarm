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

/**
 * Interface for expressions that are composed from more than one part.
 *
 * @author andyb
 */
import java.util.Collection;

public interface MultipartExpression extends Expression {

    /**
     * Return the expression parts.
     *
     * Generics are used to allow more specific sets to be returned. If the order of expressions is important,
     * the implementer should return an object instance that implements java.util.List.
     * 
     * @return
     */
    public Collection<? extends Expression> getParts();

}
