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
import static network.aika.elements.typedef.SynapseDefinition.WEIGHT;
import static network.aika.fields.IdentityFunction.identity;
import static network.aika.fields.InvertFunction.invert;
import static network.aika.fields.SumField.sum;
import static network.aika.fields.ThresholdOperator.Comparison.ABOVE;
import static network.aika.fields.ThresholdOperator.threshold;
import static network.aika.queue.Phase.TRAINING;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronDef {

    public static final String INPUT_VALUE = "inputValue";
    public static final String INPUT_IS_FIRED = "inputIsFired";
    public static final String NEG_INPUT_IS_FIRED = "negInputIsFired";
    public static final String INITIAL_CATEGORY_SYNAPSE_WEIGHT = "initialCategorySynapseWeight";
    public static final String NET_OUTER_GRADIENT = "netOuterGradient";
    public static final String GRADIENT = "gradient";
    public static final String UPDATE_VALUE = "updateValue";
    public static final String NEG_UPDATE_VALUE = "negUpdateValue";

    TypeModel typeModel;

    StateDef nonFeedbackState = new StateDef(typeModel);

    ConjunctiveDef conjunctiveDef;

    DisjunctiveDef disjunctiveDef;

    ActivationDefinition activation;

    NeuronDefinition neuron;

    LinkDefinition link;

    SynapseDefinition synapse;

    public NeuronDef(TypeModel typeModel) {
        this.typeModel = typeModel;
    }

    public void initNodes() {
        nonFeedbackState.init("NonFeedbackState", NON_FEEDBACK);

        activation = new ActivationDefinition(
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
                "Neuron",
                Neuron.class
        );
    }

    public void initRelations() {
        link = new LinkDefinition(
                "Link",
                Link.class);

        identity(link, INPUT_VALUE);
        threshold(link, INPUT_IS_FIRED, 0.0, ABOVE)
                .in(0, (o, p) -> o.getFieldOutput(INPUT_VALUE));

        invert(link,NEG_INPUT_IS_FIRED)
                .in(0, (o, p) -> o.getFieldOutput(INPUT_IS_FIRED));

        synapse = new SynapseDefinition(
                "Synapse",
                Synapse.class
        );

        sum(synapse, WEIGHT)
                .setQueued(TRAINING);
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
