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
package network.aika.neurons;

import network.aika.Model;
import network.aika.activations.Activation;
import network.aika.Element;
import network.aika.bindingsignal.BindingSignal;
import network.aika.activations.Link;
import network.aika.type.Obj;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.typedefs.LinkDefinition;
import network.aika.typedefs.SynapseDefinition;
import network.aika.bindingsignal.BSType;
import network.aika.bindingsignal.Transition;
import network.aika.type.ObjImpl;
import network.aika.queue.QueueProvider;
import network.aika.queue.Timestamp;
import network.aika.misc.direction.Direction;
import network.aika.queue.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static network.aika.queue.Timestamp.MAX;
import static network.aika.queue.Timestamp.MIN;
import static network.aika.neurons.RefType.SYNAPSE_IN;
import static network.aika.neurons.RefType.SYNAPSE_OUT;
import static network.aika.typedefs.LinkDefinition.SELF;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Synapse extends ObjImpl implements Element, QueueProvider {

    protected static final Logger log = LoggerFactory.getLogger(Synapse.class);

    protected int synapseId;
    protected NeuronReference input;
    protected NeuronReference output;

    protected boolean propagable;

    public Synapse(SynapseDefinition type) {
        super(type);
    }

    public Synapse(SynapseDefinition type, Neuron input, Neuron output) {
        this(type);
        link(input.getModel(), input, output);
    }

    @Override
    public Stream<Obj> followManyRelation(Relation rel) {
        if(rel == SynapseDefinition.LINK)
            return null;
        else
            throw new RuntimeException("Invalid Relation");
    }

    @Override
    public Obj followSingleRelation(Relation rel) {
        if(rel == SynapseDefinition.SELF)
            return this;
        else if(rel == SynapseDefinition.INPUT)
            return getInput();
        else if(rel == SynapseDefinition.OUTPUT)
            return getOutput();
        else
            throw new RuntimeException("Invalid Relation");
    }

    public int getSynapseId() {
        return synapseId;
    }

    public void setSynapseId(int synapseId) {
        this.synapseId = synapseId;
    }

    public Map<BSType, BindingSignal> transitionForward(Map<BSType, BindingSignal> inputBindingSignals) {
        Map<BSType, BindingSignal> outputTransitions = new HashMap<>();
        Transition[] transitions = ((SynapseDefinition)type).getTransition();
        for (Transition t : transitions) {
            BindingSignal bs = inputBindingSignals.get(t.from());
            if (bs != null) {
                outputTransitions.put(t.to(), bs);
            }
        }
        return outputTransitions;
    }

    public Synapse setPropagable(Model m, boolean propagable) {
        if(this.propagable != propagable)
            input.getNeuron(m).setModified();

        getInput(m).updatePropagable(output.getNeuron(m), propagable);
        this.propagable = propagable;

        return this;
    }

    public boolean isPropagable() {
        return propagable;
    }

    public final void setModified(Model m) {
        Neuron n = getStoredAt().getNeuron(m, this);
        if(n != null)
            n.setModified();
    }

    public void setInput(Neuron n) {
        this.input = new NeuronReference(n, SYNAPSE_IN);
    }

    public void setOutput(Neuron n) {
        this.output = new NeuronReference(n, SYNAPSE_OUT);
    }

    public Synapse link(Model m, Neuron input, Neuron output) {
        synapseId = output.getNewSynapseId();

        setInput(input);
        setOutput(output);

        link(m);

        return this;
    }

    public void link(Model m) {
        input.getNeuron(m).addOutputSynapse(this);
        output.getNeuron(m).addInputSynapse(this);
    }

    public void unlinkInput(Model m) {
        getInput(m).removeOutputSynapse(this);
    }

    public void unlinkOutput(Model m) {
        getOutput(m).removeInputSynapse(this);
    }

    public final Link createLink(Activation input, Activation output) {
        return createLink(input, transitionForward(input.getBindingSignals()), output);
    }

    public final Link createLink(Activation input, Map<BSType, BindingSignal> bindingSignals, Activation output) {
        if(output.hasConflictingBindingSignals(bindingSignals))
            return null;
        else if(output.hasNewBindingSignals(bindingSignals)) {
            output = output.branch(bindingSignals);

            output.linkIncoming(input);
        }

        return ((SynapseDefinition)getType())
                .getLink()
                .instantiate(this, input, output);
    }

    public final Direction getStoredAt() {
        return ((SynapseDefinition)getType()).getStoredAt();
    }

    public NeuronReference getInputRef() {
        return input;
    }

    public NeuronReference getOutputRef() {
        return output;
    }

    public Neuron getInput() {
        return getInput(
                output.getRawNeuron().getModel()
        );
    }

    public Neuron getInput(Model m) {
        if(input == null)
            return null;

        return input.getNeuron(m);
    }

    public Neuron getOutput() {
        return getOutput(
                input.getRawNeuron().getModel()
        );
    }

    public Neuron getOutput(Model m) {
        if (output == null)
            return null;

        return output.getNeuron(m);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getName());

        super.write(out);
        out.writeInt(synapseId);

        out.writeLong(input.getId());
        out.writeLong(output.getId());
    }

    public static Synapse read(DataInput in, TypeRegistry tr) throws IOException {
        short synTypeId = in.readShort();
        SynapseDefinition synapseDefinition = (SynapseDefinition) tr
                .getType(synTypeId);
        Synapse syn = synapseDefinition.instantiate();
        syn.readFields(in, tr);
        return syn;
    }

    @Override
    public void readFields(DataInput in, TypeRegistry tr) throws IOException {
        super.readFields(in, tr);
        synapseId = in.readInt();
        input = new NeuronReference(in.readLong(), SYNAPSE_IN);
        output = new NeuronReference(in.readLong(), SYNAPSE_OUT);
    }

    @Override
    public Timestamp getCreated() {
        return MIN;
    }

    @Override
    public Timestamp getFired() {
        return MAX;
    }

    public void delete(Model m) {
        if(log.isInfoEnabled())
            log.info("Delete synapse: " + this);

        if(input != null)
            getInput(m).removeOutputSynapse(this);
        if(output != null)
            getOutput(m).removeInputSynapse(this);
    }

    @Override
    public Queue getQueue() {
        return null; // TODO
    }

    @Override
    public String toString() {
        return type.getName() +
                " in:[" + (input != null ? input.toKeyString() : "X")  + "] " +
                " --> " +
                " out:[" + (output != null ? output.toKeyString() : "X") + "])";
    }

    @Override
    public String toKeyString() {
        return (input != null ? input.toKeyString() : "X") +
                " --> " +
               (output != null ? output.toKeyString() : "X");
    }
}
