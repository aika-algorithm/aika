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
package network.aika.elements.typedef;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.fielddefs.Type;
import network.aika.fielddefs.ObjectPath;
import network.aika.fielddefs.TypeRegistry;

import java.util.List;
import java.util.Set;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkDefinition extends Type<LinkDefinition, Link> {

    private SynapseDefinition synapse;
    private ActivationDefinition input;
    private ActivationDefinition output;

    private SynapseSlotDefinition inputSlot;
    private SynapseSlotDefinition outputSlot;


    public LinkDefinition(TypeRegistry registry, String name, Class<? extends Link> clazz) {
        super(registry, name, clazz);
    }

    @Override
    public void dumpType(StringBuilder sb) {
        sb.append("  synapse:" + synapse.toKeyString());
        sb.append("  input:" + input.toKeyString());
        sb.append("  output:" + output.toKeyString());
        sb.append("  inputSlot:" + inputSlot.toKeyString());
        sb.append("  outputSlot:" + outputSlot.toKeyString());
    }

    public Link instantiate(Synapse synapse, Activation input, Activation output) {
        return instantiate(
                List.of(LinkDefinition.class, Synapse.class, Activation.class, Activation.class),
                List.of(this, synapse, input, output)
        );
    }

    public SynapseDefinition getSynapse() {
        return synapse;
    }

    public SynapseDefinition getSynapse(ObjectPath p) {
        addPathEntry(p, "link->synapse", synapse, l -> Set.of(l.getSynapse()));
        return synapse;
    }

    LinkDefinition setSynapse(SynapseDefinition synapse) {
        assert synapse != null;

        this.synapse = synapse;

        return this;
    }

    public ActivationDefinition getInput(ObjectPath p) {
        addPathEntry(p, "link->input", input, l ->
                Set.of(l.getInput())
        );
        return input;
    }

    public ActivationDefinition getInput() {
        return input;
    }

    public LinkDefinition setInput(ActivationDefinition input) {
        assert input != null;

        this.input = input;

        return this;
    }

    public ActivationDefinition getOutput(ObjectPath p) {
        addPathEntry(p, "link->output", output, l ->
                Set.of(l.getOutput())
        );
        return output;
    }

    public ActivationDefinition getOutput() {
        return output;
    }

    public LinkDefinition setOutput(ActivationDefinition output) {
        assert output != null;

        this.output = output;

        return this;
    }


    public SynapseSlotDefinition getInputSlot() {
        return inputSlot;
    }

    public SynapseSlotDefinition getInputSlot(ObjectPath p) {
        addPathEntry(p, "link->inputSlot", inputSlot, l ->
                Set.of(l.getInputSlot())
        );
        return inputSlot;
    }

    public LinkDefinition setInputSlot(SynapseSlotDefinition inputSlot) {
        assert inputSlot != null;

        this.inputSlot = inputSlot;

        return this;
    }

    public SynapseSlotDefinition getOutputSlot() {
        return outputSlot;
    }

    public SynapseSlotDefinition getOutputSlot(ObjectPath p) {
        addPathEntry(p, "link->outputSlot", outputSlot, l ->
                Set.of(l.getOutputSlot())
        );
        return outputSlot;
    }

    public LinkDefinition setOutputSlot(SynapseSlotDefinition outputSlot) {
        assert outputSlot != null;

        this.outputSlot = outputSlot;

        return this;
    }
}
