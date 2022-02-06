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
package network.aika.neuron.activation;

import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.SwitchField;
import network.aika.neuron.conjunctive.PositiveFeedbackSynapse;

/**
 *
 * @author Lukas Molzberger
 */
public class PositiveFeedbackLink extends BindingNeuronLink<PositiveFeedbackSynapse, PatternActivation> {

    private SwitchField combinedWeight;

    public PositiveFeedbackLink(PositiveFeedbackSynapse s, PatternActivation input, BindingActivation output) {
        super(s, input, output);

        combinedWeight = new SwitchField(
                () -> input.isFinalMode(),
                s.getWeight(),
                s.getFeedbackWeight()
        );
    }

    public void setFinalMode() {
        output.updateBias(synapse.getFeedbackBias().getCurrentValue());
        output.getNet().addAndTriggerUpdate(synapse.getFeedbackWeight().getCurrentValue());
    }

    public FieldInput getWeightInput() {
        return combinedWeight;
    }

    public FieldOutput getWeightOutput() {
        return combinedWeight;
    }
}