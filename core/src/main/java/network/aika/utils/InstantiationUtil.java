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
package network.aika.utils;

import network.aika.elements.neurons.*;
import network.aika.elements.synapses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class InstantiationUtil {

    private static final Logger log = LoggerFactory.getLogger(InstantiationUtil.class);


    public static PatternNeuron instantiatePatternWithBindingNeurons(PatternNeuron tpn, OuterInhibitoryNeuron tInhibN, String label) {
        Map<NeuronProvider, Neuron> templateMapping = new TreeMap<>();

        PatternNeuron pn = instantiatePatternNeuron(tpn, label);
        templateMapping.put(tpn.getProvider(), pn);

        OuterInhibitoryNeuron inhibN = instantiateInhibitoryNeuron(tInhibN, label);
        templateMapping.put(tInhibN.getProvider(), inhibN);

        List<BindingNeuron> templateBindingNeurons = tpn.getInputSynapsesByType(PatternSynapse.class)
                .map(Synapse::getInput)
                .toList();

        templateBindingNeurons.forEach(tn -> {
                    BindingNeuron[] bn = instantiateBindingNeuron(tn, label);
                    templateMapping.put(bn[0].getProvider(), bn[1]);
                }
        );

        templateBindingNeurons.forEach(tbn -> {
                    BindingNeuron bn = (BindingNeuron) templateMapping.get(tbn.getProvider());
                    instantiatePatternSynapse(tpn, pn, tbn, bn);
                    instantiateInhibitorySynapse(tInhibN, inhibN, tbn, bn);
                    instantiateBindingNeuronSynapses(np -> templateMapping.get(np), tbn.getProvider(), bn);
                }
        );
        return pn;
    }

    private static PatternNeuron instantiatePatternNeuron(PatternNeuron tpn, String label) {
        PatternNeuron pn = tpn
                .instantiateTemplate()
                .init(tpn.getModel(), label);

        makeAbstract(pn);
        return pn;
    }

    private static OuterInhibitoryNeuron instantiateInhibitoryNeuron(OuterInhibitoryNeuron tInhibN, String label) {
        OuterInhibitoryNeuron inhibN;
        inhibN = tInhibN
                .instantiateTemplate()
                .init(tInhibN.getModel(), label);

        makeAbstract(inhibN);
        return inhibN;
    }

    private static void instantiatePatternSynapse(PatternNeuron tpn, PatternNeuron pn, BindingNeuron tbn, BindingNeuron bn) {
        PatternSynapse ps = (PatternSynapse) tpn.getInputSynapse(tbn.getProvider());
        ps.instantiateTemplate(bn, pn);
    }

    private static void instantiateInhibitorySynapse(OuterInhibitoryNeuron tInhibN, OuterInhibitoryNeuron inhibN, BindingNeuron tbn, BindingNeuron bn) {
        OuterInhibitorySynapse inhibS = (OuterInhibitorySynapse) tInhibN.getInputSynapse(tbn.getProvider());
        inhibS.instantiateTemplate(bn, inhibN);
    }

    private static void instantiateBindingNeuronSynapses(Function<NeuronProvider, Neuron> resolver, NeuronProvider tbn, BindingNeuron bn) {
        tbn.getInputSynapses()
                .filter(ts -> !(ts instanceof BindingCategoryInputSynapse))
                .forEach(ts -> {
                    Neuron<?> in = resolver.apply(ts.getPInput());
                    ts.instantiateTemplate(
                            in != null ?
                                    in :
                                    ts.getInput(),
                            bn
                    );
                });

        tbn.getOutputSynapses()
                .filter(ts -> !(ts instanceof BindingCategorySynapse))
                .forEach(ts -> {
                    Neuron<?> out = resolver.apply(ts.getPOutput());
                    ts.instantiateTemplate(
                            bn,
                            out != null ?
                                    out :
                                    ts.getOutput()
                    );
                });
    }

    private static BindingNeuron[] instantiateBindingNeuron(BindingNeuron tbn, String label) {
        BindingNeuron bn = tbn
                .instantiateTemplate()
                .init(tbn.getModel(), tbn.getLabel() + " " + label);

        makeAbstract(bn);

        return new BindingNeuron[] {tbn, bn};
    }
}
