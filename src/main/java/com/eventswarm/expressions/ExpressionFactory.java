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
package com.eventswarm.expressions;

/**
 * Interface implemented by classes that manage the creation of expressions, and in particular, those that allow
 * expressions to be recycled.
 *
 * Clients of ExpressionFactory instances are expected to return expressions retrieved from the Factory using the
 * recycleExpression method, otherwise the factory might hold references to expressions that are no longer being
 * used.
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public interface ExpressionFactory {

    /**
     * Get an expression from the factory, either by creating a new expression or recycling an old one.
     *
     * @return
     */
    public Expression get();

    /**
     * Give an expression back to the ExpressionFactory for re-use or garbage collection
     */
    public void recycle(Expression expr);
}
