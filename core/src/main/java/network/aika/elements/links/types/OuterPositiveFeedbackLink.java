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
package network.aika.elements.links.types;

import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.links.PositiveFeedbackLink;
import network.aika.elements.synapses.types.OuterPositiveFeedbackSynapse;
import network.aika.fields.AbstractFunction;
import network.aika.fields.Fields;


/**
 *
 * @author Lukas Molzberger
 */
public class OuterPositiveFeedbackLink extends PositiveFeedbackLink<OuterPositiveFeedbackSynapse, PatternActivation, BindingActivation> {

    private AbstractFunction inputEntropy;

    public OuterPositiveFeedbackLink(OuterPositiveFeedbackSynapse s, PatternActivation input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    public void initFromTemplate(Link template) {
        super.initFromTemplate(template);
        synapse.initDummyLink(output);
    }

    public AbstractFunction getInputEntropy() {
        return inputEntropy;
    }

    @Override
    protected void connectGradientFields() {
        if(input == null)
            return;

        inputEntropy = Fields.scale(this, "-Entropy", -1,
                input.getEntropy(),
                output.getGradient()
        );
    }
}
