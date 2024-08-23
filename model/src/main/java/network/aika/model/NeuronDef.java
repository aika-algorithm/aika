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
package network.aika.model;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.elements.typedef.*;
import network.aika.enums.direction.Direction;

import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.elements.typedef.FieldTags.*;
import static network.aika.fielddefs.inputs.ArgInputs.argLink;
import static network.aika.fielddefs.inputs.VariableInputs.varLink;
import static network.aika.fields.IdentityFunction.identity;
import static network.aika.fields.InvertFunction.invert;
import static network.aika.fields.MaxField.max;
import static network.aika.fields.Multiplication.mul;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.ThresholdOperator.Comparison.ABOVE;
import static network.aika.fields.ThresholdOperator.threshold;
import static network.aika.queue.Phase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDef extends TypeDefinitionBase {

    StateDef nonFeedbackState;

    ActivationDefinition activation;

    NeuronDefinition neuron;

    LinkDefinition link;

    SynapseDefinition synapse;

    SynapseSlotDefinition inputSlot;

    SynapseSlotDefinition outputSlot;


    public NeuronDef(TypeModel typeModel) {
        super(typeModel, null);

        nonFeedbackState = new StateDef(typeModel);
    }

    public void initNodes() {
        nonFeedbackState.init("NonFeedbackState", NON_FEEDBACK);

        activation = new ActivationDefinition(
                typeModel,
                "Activation",
                Activation.class
        )
                .addStateType(nonFeedbackState.state);

/*
        func(
                activation,
                NET_OUTER_GRADIENT,
                TOLERANCE,
                getNet(PRE_FEEDBACK),
                x -> getNeuron().getActivationFunction().outerGrad(x)
        );

        gradient;

        updateValue;

        negUpdateValue;
*/
        
        neuron = new NeuronDefinition(
                typeModel,
                "Neuron",
                Neuron.class
        )
                .setActivation(activation);
    }

    public void initRelations() {
        inputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "SynapseInputSlot",
                SynapseSlot.class
        )
                .setDirection(Direction.INPUT);

        outputSlot = new SynapseSlotDefinition(
                getTypeModel(),
                "SynapseOutputSlot",
                SynapseSlot.class
        )
                .setDirection(Direction.OUTPUT);

        link = new LinkDefinition(
                typeModel,
                "Link",
                Link.class)
                .setInputSlot(inputSlot)
                .setOutputSlot(outputSlot)
                .setInput(activation)
                .setOutput(activation);

        /*
                weightedInput = mul(
                this,
                "iAct(" + getInputKeyString() + ").value * s.weight",
                inputValue, isInputSideActive(), true,
                synapse.getWeight(), true, false
        );
         */

        synapse = new SynapseDefinition(
                typeModel,
                "Synapse",
                Synapse.class
        )
                .setLink(link)
                .setInput(neuron)
                .setOutput(neuron);


        max(inputSlot, INPUT_SLOT);

        max(outputSlot, OUTPUT_SLOT);

        sum(synapse, WEIGHT)
                .setQueued(TRAINING);

        identity(link, INPUT_VALUE);
        threshold(link, INPUT_IS_FIRED, 0.0, ABOVE)
                .in((o, p) -> o.getFieldOutput(INPUT_VALUE), argLink(0));

        invert(link, NEG_INPUT_IS_FIRED)
                .in((o, p) -> o.getFieldOutput(INPUT_IS_FIRED), argLink(0));

        mul(link, WEIGHTED_INPUT)
                .in((o, p) -> o.getFieldOutput(INPUT_VALUE), argLink(0))
                .in((o, p) -> o.getSynapse(p).getFieldOutput(WEIGHT), argLink(1))
                .out((o, p) -> o.getOutputSlot(p).getField(OUTPUT_SLOT), varLink());
    }

    public StateDefinition getNonFeedbackState() {
        return nonFeedbackState.state;
    }

    public ActivationDefinition getActivation() {
        return activation;
    }

    public NeuronDefinition getNeuron() {
        return neuron;
    }

    public SynapseSlotDefinition getInputSlot() {
        return inputSlot;
    }

    public SynapseSlotDefinition getOutputSlot() {
        return outputSlot;
    }

    public LinkDefinition getLink() {
        return link;
    }

    public SynapseDefinition getSynapse() {
        return synapse;
    }
}
