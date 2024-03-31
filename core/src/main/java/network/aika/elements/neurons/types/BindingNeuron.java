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
package network.aika.elements.neurons.types;

import network.aika.Model;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.RefType;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.types.BindingCategoryInputSynapse;
import network.aika.elements.synapses.types.BindingCategorySynapse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Comparator;

import static network.aika.elements.neurons.RefType.CATEGORY;


/**
 * @author Lukas Molzberger
 */

public class BindingNeuron extends ConjunctiveNeuron<BindingNeuron, BindingActivation> {

    private boolean isPrimary;

    public BindingNeuron(NeuronProvider np) {
        super(np);
    }

    public BindingNeuron(Model m, RefType rt) {
        super(m, rt);
    }

    @Override
    public void count(BindingActivation act) {
        super.count(act);
//        updateSynapseRanking();
    }

    public void updateSynapseRanking() {
        getInputSynapsesByType(ConjunctiveSynapse.class)
                .forEach(s -> s.setPropagable(false));

        getInputSynapsesByType(ConjunctiveSynapse.class)
                .filter(s -> !s.isWeak())
                .min(Comparator.comparingDouble(ConjunctiveSynapse::getAvgRelActTime))
                .ifPresent(s -> s.setPropagable(true));
    }

    public BindingNeuron setPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
        return this;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public static BindingNeuron create(Model m, String label) {
        return new BindingNeuron(m, RefType.NEURON_EXTERNAL)
                .setLabel(label)
                .setPersistent(true);
    }

    @Override
    public BindingCategoryNeuron createCategoryNeuron() {
        return new BindingCategoryNeuron(getModel(), CATEGORY)
                .setLabel(getLabel() + CATEGORY_LABEL);
    }

    @Override
    public BindingCategoryInputSynapse createCategoryInputSynapse() {
        return new BindingCategoryInputSynapse();
    }

    @Override
    public BindingCategorySynapse createCategorySynapse() {
        return new BindingCategorySynapse();
    }

    @Override
    public BindingCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(BindingCategoryInputSynapse.class);
    }

    @Override
    public BindingCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(BindingCategorySynapse.class);
    }


    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeBoolean(isPrimary);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        isPrimary = in.readBoolean();
    }
}
