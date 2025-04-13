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
import network.aika.bindingsignal.BSType;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Neuron;
import network.aika.typedefs.ActivationDefinition;
import network.aika.typedefs.SynapseDefinition;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class InhibitoryActivation extends Activation {

    protected NavigableMap<Integer, Link> inputLinks = new TreeMap<>();
    protected NavigableMap<Integer, Link> outputLinks = new TreeMap<>();

    public InhibitoryActivation(
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
    public void addInputLink(Link l) {
        int bsId = getInputKey(l);
        assert inputLinks.get(bsId) == null;
        inputLinks.put(bsId, l);
    }

    public Link getInputLink(int bsId) {
        return inputLinks.get(bsId);
    }

    public int getInputKey(Link l) {
        BSType wildcard = ((ActivationDefinition) type).getWildcard();
        BSType inputBSType = ((SynapseDefinition)l.getSynapse().getType()).mapTransitionBackward(wildcard);
        BindingSignal inputBS = l.getInput().getBindingSignal(inputBSType);
        return inputBS.getTokenId();
    }

    @Override
    public void addOutputLink(Link l) {
        int bsId = getOutputKey(l);
        assert outputLinks.get(bsId) == null;
        outputLinks.put(bsId, l);
    }

    public Link getOutputLink(int bsId) {
        return outputLinks.get(bsId);
    }

    public int getOutputKey(Link l) {
        BSType wildcard = ((ActivationDefinition) type).getWildcard();
        BSType outputBSType = ((SynapseDefinition)l.getSynapse().getType()).mapTransitionForward(wildcard);
        BindingSignal outputBS = l.getOutput().getBindingSignal(outputBSType);
        return outputBS.getTokenId();
    }

    @Override
    public void linkIncoming(Activation excludedInputAct) {

    }

    @Override
    public Stream<Link> getInputLinks() {
        return inputLinks.values()
                .stream();
    }

    @Override
    public Stream<Link> getOutputLinks() {
        return outputLinks.values()
                .stream();
    }

    @Override
    public Link getCorrespondingInputLink(Link l) {
        int bsId = getOutputKey(l);
        return getInputLink(bsId);
    }

    @Override
    public Link getCorrespondingOutputLink(Link l) {
        int bsId = getInputKey(l);
        return getOutputLink(bsId);
    }
}
