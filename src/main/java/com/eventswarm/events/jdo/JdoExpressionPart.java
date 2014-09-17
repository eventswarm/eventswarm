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

import com.eventswarm.events.Event;
import com.eventswarm.events.ExpressionPart;
import com.eventswarm.expressions.Expression;

/**
 * EventPart to carry details of an expression that was responsible for the creation of the owning event
 *
 * TODO: verify serialisation of Expression objects for persistence
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class JdoExpressionPart extends JdoEventPart implements ExpressionPart {

    private Expression expression;

    public JdoExpressionPart() {
        super();
    }

    public JdoExpressionPart(Expression expression) {
        this.setExpression(expression);
    }

    protected void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Expression getExpression() {
        return expression;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
