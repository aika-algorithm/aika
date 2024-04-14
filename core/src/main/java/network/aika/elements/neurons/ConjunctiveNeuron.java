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
import network.aika.elements.activations.Activation;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.fields.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveNeuron extends Neuron {

    private static final Logger LOG = LoggerFactory.getLogger(ConjunctiveNeuron.class);

    public ConjunctiveNeuron(NeuronProvider np) {
        super(np);
    }

    public ConjunctiveNeuron(Model m, RefType rt) {
        super(m, rt);
    }

    @Override
    public double getCurrentCompleteBias() {
        return getBias().getUpdatedValue() +
                getSynapseBiasSynapses()
                        .map(ConjunctiveSynapse::getSynapseBias)
                        .mapToDouble(Field::getUpdatedValue)
                        .sum();
    }

    public Stream<ConjunctiveSynapse> getSynapseBiasSynapses() {
        return getInputSynapsesByType(ConjunctiveSynapse.class)
                .filter(s -> !s.isOptional());
    }

    public boolean isInstanceOf(ConjunctiveNeuron templateNeuron) {
        Neuron tn = getTemplate();
        if (tn == null)
            return false;

        return tn.getId() == templateNeuron.getId();
    }

    public Neuron getTemplate() {
        CategorySynapse cs = getCategoryOutputSynapse();
        if(cs == null)
            return null;

        CategoryInputSynapse cis = cs.getOutput().getOutgoingCategoryInputSynapse();
        if(cis == null)
            return null;

        return cis.getOutput();
    }

    @Override
    public void addInactiveLinks(Activation act) {
        getInputSynapsesByType(ConjunctiveSynapse.class)
                .filter(s ->
                        !s.linkExists(act, true)
                )
                .filter(s -> !s.isOptional())
                .forEach(s ->
                        s.createAndInitLink(null, act)
                );
    }
}
