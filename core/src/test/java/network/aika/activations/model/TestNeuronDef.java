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
package network.aika.activations.model;

import network.aika.activations.Activation;
import network.aika.activations.ConjunctiveActivation;
import network.aika.activations.Link;
import network.aika.fields.defs.FieldDefinition;
import network.aika.fields.defs.FixedArgumentsFieldDefinition;
import network.aika.fields.defs.VariableArgumentsFieldDefinition;
import network.aika.neurons.ConjunctiveSynapse;
import network.aika.neurons.Neuron;
import network.aika.neurons.Synapse;
import network.aika.typedefs.*;

import static network.aika.activations.TestBSTypes.A;
import static network.aika.activations.TestBSTypes.B;
import static network.aika.activations.model.TestActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
import static network.aika.bindingsignal.Transition.of;
import static network.aika.fields.EventListener.eventListener;
import static network.aika.fields.FieldActivationFunction.actFunc;
import static network.aika.fields.Multiplication.mul;
import static network.aika.fields.SumField.sum;
import static network.aika.misc.utils.Utils.TOLERANCE;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.typedefs.ActivationDefinition.SELF;

/**
 *
 * @author Lukas Molzberger
 */
public class TestNeuronDef {

    final NodeDefinition node;
    final EdgeDefinition edge;

    FieldDefinition bias;

    VariableArgumentsFieldDefinition net;
    FieldDefinition value;
    FixedArgumentsFieldDefinition fired;

    VariableArgumentsFieldDefinition weight;
    FixedArgumentsFieldDefinition weightedInput;


    public TestNeuronDef(TestTypeModel typeModel) {
        node = new NodeDefinition(typeModel,"");
        edge = new EdgeDefinition(typeModel, "");
    }

    public void init() {
        edge.synapse.setTransition(of(A, B));

        edge.setInput(node);
        edge.setOutput(node);

        bias = sum(node.neuron, "BIAS")
                .setQueued(TRAINING);

        net = sum(node.activation, "NET");
/*
        fired = eventListener(
                node.activation,
                "FIRED",
                (fd, act) -> act.updateFiredStep(act.getFieldOutput(fd)),
                TOLERANCE)
                .in(SELF, net, 0);

        value = actFunc(node.activation, "VALUE", LIMITED_RECTIFIED_LINEAR_UNIT, TOLERANCE)
                .in(SELF, net, 0)
                .setQueued(INFERENCE);

        weight = sum(edge.synapse, "WEIGHT");
        weight.setQueued(TRAINING);

        weightedInput = mul(edge.link, "WEIGHTED_INPUT");
        weightedInput.in(LinkDefinition.INPUT, value, 0)
                .in(LinkDefinition.SYNAPSE, weight, 1)
                .out(LinkDefinition.OUTPUT, net);

 */
    }

    public NodeDefinition getNode() {
        return node;
    }

    public EdgeDefinition getEdge() {
        return edge;
    }

    public ActivationDefinition getActivation() {
        return node.activation;
    }

    public NeuronDefinition getNeuron() {
        return node.neuron;
    }

    public LinkDefinition getLink() {
        return edge.link;
    }

    public SynapseDefinition getSynapse() {
        return edge.synapse;
    }

    public FieldDefinition getBias() {
        return bias;
    }

    public FieldDefinition getWeight() {
        return weight;
    }

    public FieldDefinition getNet() {
        return net;
    }
}
