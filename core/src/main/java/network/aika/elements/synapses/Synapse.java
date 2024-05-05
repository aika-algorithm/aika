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
package network.aika.elements.synapses;

import network.aika.Model;
import network.aika.elements.PreActivation;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.bsslots.BindingSignalSlot;
import network.aika.elements.relations.Relation;
import network.aika.Document;
import network.aika.elements.Element;
import network.aika.elements.links.Link;
import network.aika.elements.typedef.SynapseTypeDefinition;
import network.aika.elements.typedef.Type;
import network.aika.queue.Timestamp;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.enums.Scope;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.fields.Field;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.enums.Trigger;
import network.aika.queue.Queue;
import network.aika.text.TextReference;
import network.aika.utils.Utils;
import network.aika.utils.Writable;
import network.aika.visitor.operator.LinkingOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static network.aika.queue.Timestamp.MAX;
import static network.aika.queue.Timestamp.MIN;
import static network.aika.elements.neurons.RefType.SYNAPSE_IN;
import static network.aika.elements.neurons.RefType.SYNAPSE_OUT;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Synapse implements Type<SynapseTypeDefinition, Synapse>, Element, Writable {

    protected static final Logger log = LoggerFactory.getLogger(Synapse.class);

    protected SynapseTypeDefinition synapseType;

    protected int synapseId;
    protected NeuronProvider input;
    protected NeuronProvider output;

    private Relation relation;

    private boolean inputSideInstantiable = true;
    private boolean outputSideInstantiable = true;


    protected boolean trainingAllowed = true;

    private int latentProxySynapseId;


    public Synapse() {
    }

    @Override
    public void setTypeDefinition(SynapseTypeDefinition typeDef) {
        synapseType = typeDef;
    }

    public int getSynapseId() {
        return synapseId;
    }

    public void setSynapseId(int synapseId) {
        this.synapseId = synapseId;
    }

    public NeuronType getInputType() {
        return synapseType.getInputType();
    }

    public NeuronType getOutputType() {
        return synapseType.getOutputType();
    }

    public Transition[] getTransition() {
        return synapseType.getTransition();
    }

    public Transition getRequired() {
        return synapseType.getRequired();
    }

    public boolean isPropagateRange() {
        return synapseType.isPropagateRange();
    }

    public final SynapseSlot createInputSlot(Activation iAct) {
        return synapseType
                .getInputSlotType()
                .instantiate(iAct, this);
    }

    public SynapseSlot createAndInitInputSlot(Activation iAct) {
        SynapseSlot slot = createInputSlot(iAct);
        slot.init();
        return slot;
    }

    public Stream<BindingSignalSlot> transitionBindingSignal(Activation oAct, Scope is) {
        return Arrays.stream(getSynapseType().getTransition())
                .filter(t -> is == t.getFrom())
                .map(Transition::getTo)
                .map(oAct::getBindingSignalSlot)
                .filter(Objects::nonNull);
    }

    public final SynapseSlot createOutputSlot(Activation iAct) {
        return synapseType
                .getOutputSlotType()
                .instantiate(iAct, this);
    }

    public SynapseSlot createAndInitOutputSlot(Activation oAct) {
        SynapseSlot slot = createOutputSlot(oAct);
        slot.init();
        return slot;
    }

    public void checkWeight() {
        if(isNegative())
            delete();
    }

    public Trigger getTrigger() {
        return synapseType.getTrigger();
    }

    @Override
    public void disconnect() {
    }

    public boolean isPropagable() {
        return true;
    }

    public void propagate(Activation iAct) {
        if(!isPropagable())
            return;

        if(getRelation() != null)
            return;

        if(propagateLinkExists(iAct))
            return;

        Document doc = iAct.getDocument();
        Activation oAct = getOutput().createActivation(doc);

        createAndInitLink(iAct, oAct);
    }

    public boolean isWeak() {
        return false;
    }

    public static Link getLatentLink(Synapse synA, Synapse synB, Activation iActA, Activation iActB) {
        Stream<Link> linksA = iActA.getOutputLinks(synA);
        return linksA.filter(l -> synB.getLink(iActB, l.getOutput()) != null)
                .findAny()
                .orElse(null);
    }

    public Link getLink(Activation iAct, Activation oAct) {
        Link l = oAct.getInputLink(iAct, synapseId);
        assert l == null || l.getSynapse() == this;
        return l;
    }

    public boolean linkExists(Activation oAct, boolean includeInactive) {
        Stream<Link> links = oAct.getInputLinks(this);

        if(!includeInactive)
            links = links
                    .filter(l -> l.getInput() != null);

        return links.findAny()
                .isPresent();
    }

    public boolean propagateLinkExists(Activation iAct) {
        return iAct.getOutputLinks(this)
                        .findAny()
                        .isPresent();
    }

    public Link link(Activation iAct, Activation oAct) {
        Link l = getLink(iAct, oAct);
        if (l != null) {
            if(log.isDebugEnabled())
                log.debug("existing link: " + l);

            return l;
        }
        return createAndInitLink(iAct, oAct);
    }

    public final void setModified() {
        Neuron n = getStoredAt().getNeuron(this);
        if(n != null)
            n.setModified();
    }

    public void count(Link l) {
    }

    public void setInput(Neuron input) {
        this.input = input.getProvider();
        this.input.increaseRefCount(SYNAPSE_IN);
    }

    public void setOutput(Neuron output) {
        this.output = output.getProvider();
        this.output.increaseRefCount(SYNAPSE_OUT);
    }

    public Synapse instantiateTemplate(Neuron input, Neuron output) {
        Synapse s = output.getInputSynapse(input.getProvider());
        if(s != null)
            return s;

        SynapseTypeDefinition std = synapseType.getInstanceSynapseType() != null ?
                synapseType.getInstanceSynapseType() :
                synapseType;

        s = std.instantiate();

        input.setModified();
        output.setModified();
        s.initFromTemplate(input, output, this);
        return s;
    }

    public void initFromTemplate(Neuron input, Neuron output, Synapse templateSyn) {
        synapseId = output.getNewSynapseId();
        setInput(input);
        setOutput(output);

        link();

        if(templateSyn.relation != null)
            relation = templateSyn.relation.instantiate();

        getWeight()
                .setValue(
                        templateSyn.getInitialInstanceWeight()
                );
    }

    public double getInitialInstanceWeight() {
        return getWeight().getUpdatedValue();
    }

    public Synapse setInstantiable(boolean inputSideInstantiable, boolean outputSideInstantiable) {
        this.inputSideInstantiable = inputSideInstantiable;
        this.outputSideInstantiable = outputSideInstantiable;

        return this;
    }

    public boolean isInputSideInstantiable() {
        return inputSideInstantiable;
    }

    public boolean isOutputSideInstantiable() {
        return outputSideInstantiable;
    }

    public Synapse link(Neuron input, Neuron output) {
        input.verifyNeuronExistsOnlyOnce();
        output.verifyNeuronExistsOnlyOnce();

        synapseId = output.getNewSynapseId();

        setInput(input);
        setOutput(output);

        link();

        return this;
    }

    public void link() {
        input.addOutputSynapse(this);
        output.addInputSynapse(this);
    }

    public void unlinkInput() {
        input.removeOutputSynapse(this);
    }

    public void unlinkOutput() {
        input.removeInputSynapse(this);
    }

    public final Link createLink(Activation input, Activation output) {
        return synapseType
                .getLinkType()
                .instantiate(this, input, output);
    }

    public Link createAndInitLink(Activation input, Activation output) {
        Link l = createLink(input, output);
        l.init();
        return l;
    }

    public Link createLinkFromTemplate(Activation input, Activation output, Link template) {
        Link l = createLink(input, output);
        l.initFromTemplate(template);
        return l;
    }

    public Synapse setWeight(double w) {
        getWeight().setValue(w);

        return this;
    }

    public Synapse setInitialCategorySynapseWeight(double initialCategorySynapseWeight) {
        throw new UnsupportedOperationException();
    }

    public Synapse setOptional(boolean optional) {
        throw new UnsupportedOperationException();
    }

    public Synapse adjustBias() {
        return this;
    }

    public Field getWeight() {
        return getField(synapseType.weight);
    }

    public Field getWeightForAnnealing() {
        return getWeight();
    }

    public final boolean isTrainingAllowed() {
        return trainingAllowed && synapseType.isTrainingAllowed() && getOutput().isTrainingAllowed();
    }

    public void setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;
    }

    public final Direction getStoredAt() {
        return synapseType.getStoredAt();
    }

    public NeuronProvider getPInput() {
        return input;
    }

    public NeuronProvider getPOutput() {
        return output;
    }

    public Neuron getInput() {
        if(input == null)
            return null;

        return input.getNeuron();
    }

    public Neuron getOutput() {
        if(output == null)
            return null;

        return output.getNeuron();
    }

    public Synapse setRelation(Relation rel) {
        this.relation = rel;

        return this;
    }

    public Relation getRelation() {
        return relation;
    }

    public void expandRelation(LinkingOperator op, Neuron to, Direction relDir) {
        Relation rel = getRelation();
        if(rel == null)
            return;

        Activation from = op.getSourceAct();
        PreActivation toPreAct = to.getPreActivation(from.getDocument());
        if(toPreAct == null)
            return;

        TextReference ref = from.getTextReference();
        if(ref == null)
            return;

        rel.evaluateLatentRelation(this, ref, from, toPreAct, relDir.invert())
                .forEach(relAct ->
                        op.relationCheck(rel, this, relAct, relDir)
                );
    }


    @Override
    public Model getModel() {
        return output != null ?
                output.getModel() :
                null;
    }

    public boolean isZero() {
        return Utils.belowTolerance(TOLERANCE, getWeight().getValue());
    }

    public boolean isNegative() {
        return getWeight().getUpdatedValue() < 0.0;
    }

    public SynapseTypeDefinition getSynapseType() {
        return synapseType;
    }

    public boolean isOptional() {
        return false;
    }

    public void initSlots(Activation output) {
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getName());
        out.writeInt(synapseId);

        out.writeLong(input.getId());
        out.writeLong(output.getId());

        getWeight().write(out);
        out.writeBoolean(inputSideInstantiable);
        out.writeBoolean(outputSideInstantiable);

        out.writeBoolean(relation != null);
        if(relation != null)
            relation.write(out);
    }

    public static Synapse read(DataInput in, Model m) throws IOException {
        String synClazz = in.readUTF();

        Synapse s = m.createSynapseByClass(synClazz);
        s.readFields(in, m);
        return s;
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        synapseId = in.readInt();
        input = m.lookupNeuronProvider(in.readLong(), SYNAPSE_IN);
        output = m.lookupNeuronProvider(in.readLong(), SYNAPSE_OUT);

        getWeight().readFields(in);
        inputSideInstantiable = in.readBoolean();
        outputSideInstantiable = in.readBoolean();

        if(in.readBoolean())
            relation = Relation.read(in, m);
    }

    @Override
    public Timestamp getCreated() {
        return MIN;
    }

    @Override
    public Timestamp getFired() {
        return MAX;
    }

    public void delete() {
        if(log.isInfoEnabled())
            log.info("Delete synapse: " + this);

        if(input != null)
            input.removeOutputSynapse(this);
        if(output != null)
            output.removeInputSynapse(this);
    }

    @Override
    public Queue getQueue() {
        return getModel();
    }

    public String toString() {
        return getClass().getSimpleName() +
                " in:[" + (input != null ? input.toKeyString() : "--")  + "] " +
                getArrow() +
                " out:[" + (output != null ? output.toKeyString() : "--") + "])";
    }

    private String getArrow() {
        return "-->";
    }

    public void setLatentProxySynapseId(int synapseId) {

    }
}
