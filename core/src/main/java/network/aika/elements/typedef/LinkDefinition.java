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
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.fielddefs.ObjectRelationDefinition;
import network.aika.fielddefs.Type;
import network.aika.fielddefs.ObjectPath;
import network.aika.fielddefs.TypeRegistry;

import java.util.List;
import java.util.Set;

import static network.aika.fielddefs.ObjectRelationDefinition.single;
import static network.aika.fielddefs.ObjectRelationType.ONE_TO_MANY;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkDefinition extends Type<LinkDefinition, Link> {

    private SynapseDefinition synapse;
    ObjectRelationDefinition<LinkDefinition, Link, SynapseDefinition, Synapse> synapseRelation;

    private ActivationDefinition input;
    ObjectRelationDefinition<LinkDefinition, Link, ActivationDefinition, Activation> inputRelation;

    private ActivationDefinition output;
    ObjectRelationDefinition<LinkDefinition, Link, ActivationDefinition, Activation> outputRelation;


    private SynapseSlotDefinition inputSlot;
    ObjectRelationDefinition<LinkDefinition, Link, SynapseSlotDefinition, SynapseSlot> inputSlotRelation;

    private SynapseSlotDefinition outputSlot;
    ObjectRelationDefinition<LinkDefinition, Link, SynapseSlotDefinition, SynapseSlot> outputSlotRelation;



    public LinkDefinition(TypeRegistry registry, String name, Class<? extends Link> clazz) {
        super(registry, name, clazz);
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
        p.add(synapseRelation);
        return synapse;
    }

    LinkDefinition setSynapse(SynapseDefinition synapse) {
        this.synapse = synapse;

        synapseRelation = new ObjectRelationDefinition<>(
                this,
                synapse,
                ONE_TO_MANY,
                l -> single(l.getSynapse()),
                null
        );

        return this;
    }

    public ActivationDefinition getInput(ObjectPath p) {
        p.add(inputRelation);
        return input;
    }

    public ActivationDefinition getInput() {
        return input;
    }

    public LinkDefinition setInput(ActivationDefinition input) {
        assert input != null;

        this.input = input;
        inputRelation = new ObjectRelationDefinition<>(
                this,
                input,
                ONE_TO_MANY,
                l -> single(l.getInput()),
                null
        );

        return this;
    }

    public ActivationDefinition getOutput(ObjectPath p) {
        p.add(outputRelation);
        return output;
    }

    public ActivationDefinition getOutput() {
        return output;
    }

    public LinkDefinition setOutput(ActivationDefinition output) {
        assert output != null;

        this.output = output;
        outputRelation = new ObjectRelationDefinition<>(
                this,
                output,
                ONE_TO_MANY,
                l -> single(l.getOutput()),
                null
        );

        return this;
    }


    public SynapseSlotDefinition getInputSlot() {
        return inputSlot;
    }

    public SynapseSlotDefinition getInputSlot(ObjectPath p) {
        p.add(inputSlotRelation);
        return inputSlot;
    }

    public LinkDefinition setInputSlot(SynapseSlotDefinition inputSlot) {
        assert inputSlot != null;

        this.inputSlot = inputSlot;
        inputSlotRelation = new ObjectRelationDefinition<>(
                this,
                inputSlot,
                ONE_TO_MANY,
                l -> single(l.getInputSlot()),
                null
        );

        return this;
    }

    public SynapseSlotDefinition getOutputSlot() {
        return outputSlot;
    }

    public SynapseSlotDefinition getOutputSlot(ObjectPath p) {
        p.add(outputSlotRelation);
        return outputSlot;
    }

    public LinkDefinition setOutputSlot(SynapseSlotDefinition outputSlot) {
        assert outputSlot != null;

        this.outputSlot = outputSlot;
        outputSlotRelation = new ObjectRelationDefinition<>(
                this,
                outputSlot,
                ONE_TO_MANY,
                l -> single(l.getOutputSlot()),
                null
        );

        return this;
    }

    @Override
    public void dumpTypeDetails(StringBuilder sb) {
        sb.append("  synapse: " + synapse.getName() + "\n");

        if(input != null)
            sb.append("  input: " + input.getName() + "\n");

        if(output != null)
            sb.append("  output: " + output.getName() + "\n");

        if(inputSlot != null)
            sb.append("  inputSlot: " + inputSlot.getName() + "\n");

        if(outputSlot != null)
            sb.append("  outputSlot: " + outputSlot.getName() + "\n");
    }
}
