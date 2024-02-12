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
import network.aika.elements.Type;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.StateType;
import network.aika.elements.relations.Relation;
import network.aika.Document;
import network.aika.elements.Element;
import network.aika.elements.links.Link;
import network.aika.elements.Timestamp;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.fields.Field;
import network.aika.fields.FieldOutput;
import network.aika.fields.SumField;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.enums.Trigger;
import network.aika.text.TextReference;
import network.aika.utils.Utils;
import network.aika.utils.Writable;
import network.aika.visitor.operator.LinkingOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

import static network.aika.elements.Timestamp.MAX;
import static network.aika.elements.Timestamp.MIN;
import static network.aika.elements.synapses.SynapseTypeHolder.getHolder;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Synapse<S extends Synapse, I extends Neuron, O extends Neuron<O, OA>, L extends Link<S, IA, OA, ?, ?>, IA extends Activation<?>, OA extends Activation<?>> implements Element, Writable {

    protected static final Logger log = LoggerFactory.getLogger(Synapse.class);

    protected final SynapseTypeHolder synapseType = getHolder(getClass());

    protected int synapseId;
    protected NeuronProvider input;
    protected NeuronProvider output;

    private Relation relation;

    private boolean instantiable = true;

    protected SumField weight = (SumField) new SumField(this, "weight", TOLERANCE)
            .setQueued(getDocument(), TRAINING)
            .addListener("onWeightModified", (fl, u) -> {
                checkWeight();
                setModified();
            }, true);

    protected boolean trainingAllowed = true;

    public Synapse() {
    }

    public int getSynapseId() {
        return synapseId;
    }

    public void setSynapseId(int synapseId) {
        this.synapseId = synapseId;
    }

    public Type getInputType() {
        return synapseType.getInputType();
    }

    public Type getOutputType() {
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

    public abstract SynapseSlot createInputSlot(IA iAct);

    public SynapseSlot createAndInitInputSlot(IA iAct) {
        SynapseSlot slot = createInputSlot(iAct);
        slot.init();
        return slot;
    }


    public abstract SynapseSlot createOutputSlot(OA oAct);

    public SynapseSlot createAndInitOutputSlot(OA oAct) {
        SynapseSlot slot = createOutputSlot(oAct);
        slot.init();
        return slot;
    }

    public SumField getOutputNet(Activation act) {
        return act.getNet(synapseType.stateType());
    }

    public FieldOutput getInputValue(IA input) {
        return input.getValue();
    }

    public FieldOutput getInputValue(IA input, StateType t) {
        return input.getValue(t);
    }

    protected void checkWeight() {
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

    public void propagate(IA iAct) {
        if(!isPropagable())
            return;

        if(getRelation() != null)
            return;

        if(propagateLinkExists(iAct))
            return;

        Document doc = iAct.getDocument();
        OA oAct = getOutput().createActivation(doc);

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

    public L getLink(IA iAct, OA oAct) {
        L l = (L) oAct.getInputLink(iAct, synapseId);
        assert l == null || l.getSynapse() == this;
        return l;
    }

    public boolean linkExists(OA oAct, boolean includeInactive) {
        Stream<Link> links = oAct.getInputLinks(this);

        if(!includeInactive)
            links = links
                    .filter(l -> l.getInput() != null);

        return links.findAny()
                .isPresent();
    }

    public boolean propagateLinkExists(IA iAct) {
        return iAct.getOutputLinks(this)
                        .findAny()
                        .isPresent();
    }

    public L link(IA iAct, OA oAct) {
        L l = getLink(iAct, oAct);
        if (l != null) {
            if(log.isDebugEnabled())
                log.debug("existing link: " + l);

            return l;
        }
        return createAndInitLink(iAct, oAct);
    }

    public abstract void setModified();

    public void count(L l) {
    }

    public void setInput(I input) {
        this.input = input.getProvider();
    }

    public void setOutput(O output) {
        this.output = output.getProvider();
    }

    public S instantiateTemplate(I input, O output) {
        S s = (S) output.getInputSynapse(input.getProvider());
        if(s != null)
            return s;

        try {
            s = (S) getClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        s.initFromTemplate(input, output, this);
        return s;
    }

    public void initFromTemplate(I input, O output, Synapse templateSyn) {
        synapseId = output.getNewSynapseId();
        setInput(input);
        setOutput(output);

        link();

        if(templateSyn.relation != null)
            relation = templateSyn.relation.instantiate();

        weight.setValue(
                templateSyn.getInitialInstanceWeight()
        );
    }

    public double getInitialInstanceWeight() {
        return weight.getUpdatedValue();
    }

    public S setInstantiable(boolean instantiable) {
        this.instantiable = instantiable;

        return (S) this;
    }

    public boolean isInstantiable() {
        return instantiable;
    }

    public S link(Neuron input, Neuron output) {
        input.verifyNeuronExistsOnlyOnce();
        output.verifyNeuronExistsOnlyOnce();

        synapseId = output.getNewSynapseId();

        setInput((I) input);
        setOutput((O) output);

        link();

        return (S) this;
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

    public abstract L createLink(IA input, OA output);

    public L createAndInitLink(IA input, OA output) {
        L l = createLink(input, output);
        l.init();
        return l;
    }

    public L createLinkFromTemplate(IA input, OA output, Link template) {
        L l = createLink(input, output);
        l.initFromTemplate(template);
        return l;
    }

    public S setWeight(double w) {
        weight.setValue(w);

        return (S) this;
    }

    public S adjustBias() {
        return (S) this;
    }

    public SumField getWeight() {
        return weight;
    }

    public Field getWeightForAnnealing() {
        return weight;
    }

    public boolean isTrainingAllowed() {
        return trainingAllowed && getOutput().isTrainingAllowed();
    }

    public void setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;
    }

    public abstract Direction getStoredAt();

    public NeuronProvider getPInput() {
        return input;
    }

    public NeuronProvider getPOutput() {
        return output;
    }

    public I getInput() {
        if(input == null)
            return null;

        return input.getNeuron();
    }

    public O getOutput() {
        if(output == null)
            return null;

        return output.getNeuron();
    }

    public S setRelation(Relation rel) {
        this.relation = rel;

        return (S) this;
    }

    public Relation getRelation() {
        return relation;
    }

    public void expandRelation(LinkingOperator op, Neuron to, Direction relDir) {
        Relation rel = getRelation();
        if(rel == null)
            return;

        Activation from = op.getSourceAct();
        PreActivation<?> toPreAct = to.getPreActivation(from.getDocument());
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
        return Utils.belowTolerance(TOLERANCE, weight.getValue());
    }

    public boolean isNegative() {
        return weight.getUpdatedValue() < 0.0;
    }

    public SynapseTypeHolder getSynapseType() {
        return synapseType;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getName());
        out.writeInt(synapseId);

        out.writeLong(input.getId());
        out.writeLong(output.getId());

        weight.write(out);
        out.writeBoolean(instantiable);

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
        input = m.lookupNeuronProvider(in.readLong());
        output = m.lookupNeuronProvider(in.readLong());

        weight.readFields(in, m);
        instantiable = in.readBoolean();

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
    public Document getDocument() {
        Model m = getModel();
        return m != null ?
                m.getCurrentDocument() :
                null;
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
}
