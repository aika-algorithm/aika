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
import network.aika.elements.typedef.*;

import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.fields.IdentityFunction.identity;
import static network.aika.fields.InvertFunction.invert;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.ThresholdOperator.Type.ABOVE;
import static network.aika.fields.ThresholdOperator.threshold;
import static network.aika.queue.Phase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDef {

    TypeModel typeModel;

    StateDef nonFeedbackState = new StateDef(typeModel);

    ConjunctiveDef conjunctiveDef;

    DisjunctiveDef disjunctiveDef;

    ActivationDefinition activation;

    NeuronDefinition neuron;

    LinkDefinition link;

    public static final String INPUT_IS_FIRED = "inputIsFired";
    public static final String NEG_INPUT_IS_FIRED = "negInputIsFired";


    SynapseDefinition synapse;

    public static final String INITIAL_CATEGORY_SYNAPSE_WEIGHT = "initialCategorySynapseWeight";


    public NeuronDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        nonFeedbackState.init("NonFeedbackState", NON_FEEDBACK);

        activation = new ActivationDefinition(
                "Activation",
                Activation.class
        );

        neuron = new NeuronDefinition(
                "Neuron",
                Neuron.class
        );

        link = new LinkDefinition(
                "Link",
                Link.class);

        link.setInputValue(identity(link, "inputValue"));
        link.setInputIsFired(
                threshold(link, "inputIsFired", 0.0, ABOVE)
                        .in(0, (o, p) -> o.getInputValue())
        );
        link.setNegInputIsFired(
                invert(link,"!inputIsFired")
                        .in(0, (o, p) -> o.getInputIsFired())
        );

        synapse = new SynapseDefinition(
                "Synapse",
                Synapse.class
        );

        synapse.setWeight(
                sum(synapse, "weight")
                        .setQueued(TRAINING)
        );
    }

    public TypeModel getTypeModel() {
        return typeModel;
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

    public LinkDefinition getLink() {
        return link;
    }

    public SynapseDefinition getSynapse() {
        return synapse;
    }
}
