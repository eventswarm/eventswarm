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

import com.eventswarm.AddEventAction;
import com.eventswarm.AddEventTrigger;
import com.eventswarm.Clear;
import org.apache.log4j.Logger;

/**
 * Extension of PipelineExpression that supports the clearing of its components to facilitate recycling of expressions
 *
 * Created with IntelliJ IDEA.
 * User: andyb
 */
public class RecyclablePipelineExpression extends EventPipelineExpression {
    private Clear clearers[];
    private static final Logger logger = Logger.getLogger(RecyclablePipelineExpression.class);

    public RecyclablePipelineExpression(AddEventAction input, AddEventTrigger output, Clear[] clearers) {
        super(input, output);
        this.clearers = clearers;
    }

    public RecyclablePipelineExpression(AddEventAction input, AddEventTrigger output, Clear[] clearers, int limit) {
        super(input, output, limit);
        this.clearers = clearers;
    }

    @Override
    public void clear() {
        // clear out our current matches
        super.clear();

        // and clear out all of the pipeline components
        for (Clear component: clearers) {
            logger.debug("Clearing component " + component.toString());
            component.clear();
        }
    }
}
