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
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.Field;
import network.aika.fields.IdentityFunction;
import network.aika.fields.SumField;

import static network.aika.elements.activations.StateType.NON_FEEDBACK;
import static network.aika.fielddefs.Operators.invert;
import static network.aika.fielddefs.Operators.threshold;
import static network.aika.fields.ThresholdOperator.Type.ABOVE;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDef {

    TypeModel typeModel;

    StateDef nonFeedbackState = new StateDef(typeModel);

    ConjunctiveDef conjunctiveDef;

    DisjunctiveDef disjunctiveDef;

    ActivationTypeDefinition activation;

    NeuronTypeDefinition neuron;

    LinkTypeDefinition link;

    public static final String INPUT_VALUE = "inputValue";
    public static final String INPUT_IS_FIRED = "inputIsFired";
    public static final String NEG_INPUT_IS_FIRED = "negInputIsFired";


    SynapseTypeDefinition synapse;

    public static final String WEIGHT = "weight";

    public static final String INITIAL_CATEGORY_SYNAPSE_WEIGHT = "initialCategorySynapseWeight";


    public NeuronDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void init() {
        nonFeedbackState.init("NonFeedbackState", NON_FEEDBACK);

        activation = new ActivationTypeDefinition(
                "Activation",
                Activation.class
        );

        neuron = new NeuronTypeDefinition(
                "Neuron",
                Neuron.class
        );

        link = new LinkTypeDefinition(
                "Link",
                Link.class);

        link.addFieldDefinition(new FieldDefinition(IdentityFunction.class, link, INPUT_VALUE));
        link.inputIsFired = threshold(link, "inputIsFired", 0.0, ABOVE, link.inputValue);
        link.negInputIsFired = invert(link,"!inputIsFired", link.inputIsFired);

        synapse = new SynapseTypeDefinition(
                "Synapse",
                Synapse.class
        );

        synapse.addFieldDefinition(
                new FieldDefinition<>(SumField.class, synapse, WEIGHT, TOLERANCE)
                        .setQueued(TRAINING)
                        .addListener("onWeightModified", (r, fl, u) -> {
//                    r.checkWeight();
                            r.setModified();
                        })
        );
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public StateTypeDefinition getNonFeedbackState() {
        return nonFeedbackState.state;
    }

    public ActivationTypeDefinition getActivation() {
        return activation;
    }

    public NeuronTypeDefinition getNeuron() {
        return neuron;
    }

    public LinkTypeDefinition getLink() {
        return link;
    }

    public SynapseTypeDefinition getSynapse() {
        return synapse;
    }
}