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
package network.aika.activations;

import network.aika.Document;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Neuron;
import network.aika.neurons.Synapse;
import network.aika.typedefs.ActivationDefinition;
import network.aika.bindingsignal.BSType;
import network.aika.typedefs.SynapseDefinition;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public class ConjunctiveActivation extends Activation {

    protected NavigableMap<Integer, Link> inputLinks = new TreeMap<>();

    public ConjunctiveActivation(
            ActivationDefinition t,
            Activation parent,
            Integer id,
            Neuron n,
            Document doc,
            Map<BSType, BindingSignal> bindingSignals
    ) {
        super(t, parent, id, n, doc, bindingSignals);
    }

    @Override
    public void linkIncoming(Activation excludedInputAct) {
        neuron
                .getInputSynapsesAsStream()
                .filter(s ->
                        ((SynapseDefinition)s.getType()).isIncomingLinkingCandidate(getBindingSignals().keySet())
                )
                .forEach(s ->
                        linkIncoming(s, excludedInputAct)
                );
    }

    void linkIncoming(Synapse targetSyn, Activation excludedInputAct) {
        collectLinkingTargets(targetSyn.getInput(getModel())).stream()
                .filter(iAct -> iAct != excludedInputAct)
                .forEach(iAct ->
                        targetSyn.createLink(
                                iAct,
                                this
                        )
                );
    }

    @Override
    public void addInputLink(Link l) {
        Synapse syn = l.getSynapse();
        assert inputLinks.get(syn.getSynapseId()) == null;
        inputLinks.put(syn.getSynapseId(), l);
    }

    @Override
    public Stream<Link> getInputLinks() {
        return inputLinks.values()
                .stream();
    }
}
