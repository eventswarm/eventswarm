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

import com.eventswarm.events.ComplexExpressionPart;
import com.eventswarm.expressions.ComplexExpression;
import com.eventswarm.expressions.EventExpression;
import com.eventswarm.expressions.Expression;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoComplexExpressionPart extends JdoExpressionPart implements ComplexExpressionPart {

    private ComplexExpression expression;

    public JdoComplexExpressionPart() {
        super();
    }

    /**
     * Hide constructor using Expression rather than MultipartExpression
     *
     * @param expression
     */
    private JdoComplexExpressionPart(Expression expression) {
        super();
    }

    public JdoComplexExpressionPart(ComplexExpression expression) {
        super();
        this.setExpression(expression);
    }

    private void setExpression(ComplexExpression expression) {
        super.setExpression(expression);
    }

    public Expression getComponentExpression(int i) {
        return this.expression.getPartsAsList().get(i);
    }

    public List<EventExpression> getPartsAsList() {
        return this.expression.getPartsAsList();
    }
}
