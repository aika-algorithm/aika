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

import network.aika.ActivationFunction;
import network.aika.Model;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.CategoryInputSynapse;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.fields.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveNeuron<A extends ConjunctiveActivation> extends Neuron<A> {

    private static final Logger log = LoggerFactory.getLogger(ConjunctiveNeuron.class);

    public ConjunctiveNeuron(Model m) {
        super(m);

        bias.addListener(
                "onBiasUpdate",
                (fl, nr, u) ->
                        updateSumOfLowerWeights(),
                true
        );
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
        CategorySynapse<?,?,?> cs = getCategoryOutputSynapse();
        if(cs == null)
            return false;

        CategoryInputSynapse cis = cs.getOutput().getOutgoingCategoryInputSynapse();
        if(cis == null)
            return false;

        return cis.getOutput().getId() == templateNeuron.getId();
    }

    @Override
    public void addInactiveLinks(Activation act) {
        getInputSynapsesAsStream()
                .filter(s -> !s.linkExists(act, true))
                .forEach(s ->
                        s.createAndInitLink(null, act)
                );
    }

    public ActivationFunction getActivationFunction() {
        return ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
    }

    public void updateSumOfLowerWeights() {
        ConjunctiveSynapse[] inputSynapses = sortInputSynapses();

        double sum = bias.getUpdatedValue();
        for(ConjunctiveSynapse s: inputSynapses) {
            double w = s.getWeight().getUpdatedValue();
            if(w <= 0.0)
                continue;

            s.setSumOfLowerWeights(sum);
            sum += w;

            s.setStoredAt(
                    sum < 0 ?
                            OUTPUT :
                            INPUT
            );
        }
    }

    @Override
    public void addInputSynapse(Synapse s) {
        super.addInputSynapse(s);
        s.getWeight().addListener(
                "onWeightUpdate",
                (fl, nr, u) ->
                        updateSumOfLowerWeights(),
                true
        );
    }

    private ConjunctiveSynapse[] sortInputSynapses() {
        ConjunctiveSynapse[] inputsSynapses = getInputSynapsesByType(ConjunctiveSynapse.class)
                .toList()
                .toArray(new ConjunctiveSynapse[0]);

        Arrays.sort(
                inputsSynapses,
                Comparator.comparingDouble(s -> s.getSortingWeight())
        );
        return inputsSynapses;
    }

    public double getDeltaBetweenTargetAndMax() {
        return getInputSynapsesByType(ConjunctiveSynapse.class)
                .filter(s -> !s.isNegative())
                .mapToDouble(s -> s.getWeight().getValue() + s.getSynapseBias().getValue())
                .sum();
    }
}
