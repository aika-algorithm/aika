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
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.fields.FieldInput;
import network.aika.fields.SynapseOutputSlot;

import java.util.NavigableMap;
import java.util.TreeMap;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.scale;
import static network.aika.utils.Utils.TOLERANCE;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveActivation<N extends ConjunctiveNeuron<N, ?>> extends Activation<N> {

    protected NavigableMap<Long, SynapseOutputSlot> inputSlots;

    public ConjunctiveActivation(int id, Document doc, N n) {
        super(id, doc, n);
    }

    public SynapseOutputSlot registerInputSlot(ConjunctiveSynapse syn) {
        if(inputSlots == null)
            inputSlots = new TreeMap<>();

        return inputSlots.computeIfAbsent(syn.getInput().getId(), nId -> {
            SynapseOutputSlot slot = new SynapseOutputSlot(syn, "out-slot-" + nId, TOLERANCE);
            linkAndConnect(slot, getNet());
            return slot;
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
