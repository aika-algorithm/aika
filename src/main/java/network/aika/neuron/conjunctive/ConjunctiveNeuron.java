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
package network.aika.neuron.conjunctive;

import network.aika.Model;
import network.aika.neuron.ActivationFunction;
import network.aika.neuron.Neuron;
import network.aika.neuron.NeuronProvider;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.fields.Field;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;
import java.util.TreeSet;

import static network.aika.neuron.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveNeuron<S extends ConjunctiveSynapse, A extends Activation> extends Neuron<S, A> {

    private volatile Field weightSum = new Field();

    public ConjunctiveNeuron() {
        super();
    }

    public ConjunctiveNeuron(NeuronProvider p) {
        super(p);
    }

    public ConjunctiveNeuron(Model model, boolean addProvider) {
        super(model, addProvider);
    }

    public Field getWeightSum() {
        return weightSum;
    }

    protected void initFromTemplate(ConjunctiveNeuron n) {
        super.initFromTemplate(n);
    }

    /**
     * If the complete bias exceeds the threshold of 0 by itself, the neuron would become constantly active. The training
     * should account for that and reduce the bias back to a level, where the neuron can be blocked again by its input synapses.
     */
    public void limitBias() {
        if(bias.getCurrentValue() > weightSum.getCurrentValue())
            bias.setAndTriggerUpdate(weightSum.getCurrentValue());
    }

    public void addInactiveLinks(Activation act) {
        inputSynapses
                .values()
                .stream()
                .filter(s -> !act.inputLinkExists(s))
                .forEach(s ->
                        new Link(s, null, act)
                );
    }

    public ActivationFunction getActivationFunction() {
        return RECTIFIED_HYPERBOLIC_TANGENT;
    }

    public void updateSynapseInputConnections() {
        TreeSet<ConjunctiveSynapse> sortedSynapses = new TreeSet<>(
                Comparator.<ConjunctiveSynapse>comparingDouble(s -> s.getWeight().getCurrentValue()).reversed()
                        .thenComparing(Synapse::getPInput)
        );

        sortedSynapses.addAll(inputSynapses.values());

        double sum = getWeightSum().getCurrentValue();
        for(ConjunctiveSynapse s: sortedSynapses) {
            if(s.getWeight().getCurrentValue() <= 0.0)
                break;

            s.setAllowPropagate(!s.isWeak(sum));

            sum -= s.getWeight().getCurrentValue();
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        weightSum.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        weightSum.readFields(in, m);
    }
}