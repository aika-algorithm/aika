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
package network.aika.elements.synapses.types;

import network.aika.Model;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.links.types.NegativeFeedbackLink;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.direction.DirectionEnum;
import network.aika.fields.Field;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.NeuronType.*;
import static network.aika.elements.activations.StateType.OUTER_FEEDBACK;
import static network.aika.enums.Transition.INPUT_INPUT;
import static network.aika.enums.Trigger.FIRED_PRE_FEEDBACK;
import static network.aika.fields.link.FieldLink.linkAndConnect;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = INHIBITORY,
        outputType = BINDING,
        transition = INPUT_INPUT,
        required = INPUT_INPUT,
        trigger = FIRED_PRE_FEEDBACK,
        outputState = OUTER_FEEDBACK,
        propagateRange = false,
        storedAt = DirectionEnum.OUTPUT
)
public class NegativeFeedbackSynapse extends ConjunctiveSynapse<
        NegativeFeedbackSynapse,
        InhibitoryNeuron,
        BindingNeuron,
        NegativeFeedbackLink,
        InhibitoryActivation,
        BindingActivation
        >
{
    Field negativeWeight;

    public NegativeFeedbackSynapse() {
        negativeWeight = scale(this, "weight", -1, weight);
    }

    /*
    @Override
    public SynapseSlot createOutputSlot(BindingActivation oAct) {
        return new AnnealingSynapseOutputSlot(oAct, this, AnnealingType.OUTER_FEEDBACK);
    }
*/

    @Override
    public Field getWeightForAnnealing() {
        return negativeWeight;
    }

    @Override
    public void initBiasInput(BindingActivation act) {
        linkAndConnect(weight, act.getNet(synapseType.outputState()))
                .setPropagateUpdates(false);

        act.registerInputSlot(this);
    }

    @Override
    protected void checkWeight() {
        if(!isNegative())
            delete();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        negativeWeight.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        negativeWeight.readFields(in);
    }
}
