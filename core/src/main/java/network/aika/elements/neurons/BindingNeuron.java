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
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.synapses.*;
import network.aika.visitor.operator.LinkingOperator;
import network.aika.visitor.binding.BindingVisitor;
import network.aika.visitor.binding.RelationBindingVisitor;

import java.util.List;

import static network.aika.utils.NetworkUtils.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 * @author Lukas Molzberger
 */
public class BindingNeuron extends ConjunctiveNeuron<BindingActivation> {

    public static NeuronProvider create(Model m, String label) {
        return create(m, label, false);
    }

    public static NeuronProvider create(Model m, String label, boolean abstr) {
        BindingNeuron bn = new BindingNeuron(m)
                .setLabel(label);

        if(abstr)
            makeAbstract(bn)
                    .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        return bn.getProvider(true);
    }

    public BindingNeuron(Model m) {
        super(m);
    }

    @Override
    public void startVisitor(LinkingOperator c, Activation act, Synapse startSyn) {
        BindingVisitor v = new RelationBindingVisitor(act.getThought(), c, startSyn);
        v.start(act);
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new BindingCategorySynapse();
    }

    public List<RelationInputSynapse> findLatentRelationNeurons() {
        return getInputSynapsesAsStream()
                .filter(s -> s instanceof RelationInputSynapse)
                .map(s -> (RelationInputSynapse) s)
                .toList();
    }

    @Override
    public BindingActivation createActivation(Thought t) {
        return new BindingActivation(t.createActivationId(), t, this);
    }

    @Override
    public BindingCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(BindingCategoryInputSynapse.class);
    }

    @Override
    public BindingCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(BindingCategorySynapse.class);
    }

    public double getPreNetUBDummyWeightSum() {
        return getInputSynapsesByType(BindingNeuronSynapse.class)
                .mapToDouble(BindingNeuronSynapse::getPreNetDummyWeight)
                .sum();
    }
}
