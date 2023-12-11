/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.aika.elements.links;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.InstantiationFeedbackSynapse;
import network.aika.fields.Field;
import network.aika.fields.InputField;
import network.aika.fields.MaxField;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class InstantiationFeedbackLink<S extends InstantiationFeedbackSynapse, IA extends Activation<?>, OA extends ConjunctiveActivation<?>> extends
        PositiveFeedbackLink<S, IA, OA> {

    protected Field feedbackTrigger;

    public InstantiationFeedbackLink(S s, IA input, OA output) {
        super(s, input, output);
    }

    @Override
    protected void initInputValue() {
        feedbackTrigger = new InputField(this, "instantiation feedback trigger", 1.0);
        inputValue = new MaxField(this, "input-value-ft", TOLERANCE);

        linkAndConnect(feedbackTrigger, 0, inputValue);
    }

    public Field getFeedbackTrigger() {
        return feedbackTrigger;
    }
}
