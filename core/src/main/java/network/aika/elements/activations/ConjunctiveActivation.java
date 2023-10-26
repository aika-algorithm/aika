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
package network.aika.elements.activations;

import network.aika.Document;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.synapses.FeedbackSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.fields.SynapseInputSlot;
import network.aika.fields.SynapseOutputSlot;

import java.util.NavigableMap;
import java.util.TreeMap;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.scale;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveActivation<N extends ConjunctiveNeuron<N, ?>> extends Activation<N> {

    protected NavigableMap<Long, SynapseInputSlot> inputSlots;
    protected NavigableMap<Long, SynapseOutputSlot> outputSlots;

    public ConjunctiveActivation(int id, Document doc, N n) {
        super(id, doc, n);
    }

    @Override
    protected void initInactiveLinks() {
        neuron.getInputSynapsesByType(FeedbackSynapse.class)
                .forEach(s ->
                        s.initDummyLink(this)
                );
    }

    public SynapseInputSlot getInputSlot(Synapse s) {
        return inputSlots.get(s.getInput().getId());
    }

    public SynapseInputSlot lookupInputSlot(Synapse s) {
        if(inputSlots == null)
            inputSlots = new TreeMap<>();

        return inputSlots.computeIfAbsent(s.getInput().getId(), nId -> {
            SynapseInputSlot f = new SynapseInputSlot(s, "slot-" + nId);
            linkAndConnect(f, net);
            return f;
        });
    }

    public SynapseOutputSlot getOutputSlot(Synapse s) {
        return outputSlots.get(s.getOutput().getId());
    }

    @Override
    public SynapseOutputSlot lookupOutputSlot(Link l) {
        Synapse syn = l.getSynapse();
        if(outputSlots == null)
            outputSlots = new TreeMap<>();

        return outputSlots.computeIfAbsent(syn.getOutput().getId(), nId -> {
            SynapseOutputSlot f = new SynapseOutputSlot(syn, "slot-" + nId);
            linkAndConnect(f, l.getOutput().getNet());
            return f;
        });
    }


    @Override
    protected void connectWeightUpdate() {
        negUpdateValue = scale(
                this,
                "-updateValue",
                -1.0,
                updateValue
        );

        linkAndConnect(
                updateValue,
                getNeuron().getBias()
        );
    }

    @Override
    protected void initNet() {
        super.initNet();

        neuron.getSynapseBiasSynapses()
                .forEach(s ->
                        s.initBiasInput(this)
                );
    }
}
