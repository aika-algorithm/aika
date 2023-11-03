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
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.types.*;
import network.aika.enums.Transition;
import network.aika.Document;
import network.aika.elements.Element;
import network.aika.elements.links.Link;
import network.aika.elements.Timestamp;
import network.aika.enums.direction.Direction;
import network.aika.fields.FieldOutput;
import network.aika.fields.SumField;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.enums.LinkingMode;
import network.aika.utils.BitUtils;
import network.aika.utils.Utils;
import network.aika.utils.Writable;
import network.aika.visitor.operator.LinkingOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static network.aika.elements.Timestamp.MAX;
import static network.aika.elements.Timestamp.MIN;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Synapse<S extends Synapse, I extends Neuron, O extends Neuron<O, OA>, L extends Link<S, IA, OA>, IA extends Activation<?>, OA extends Activation> implements Element, Writable {

    protected static final Logger log = LoggerFactory.getLogger(Synapse.class);

    private final SynapseType synapseType = getClass().getAnnotation(SynapseType.class);

    public static Set<Class<? extends Synapse>> SYNAPSE_TYPES = Set.of(
            PatternSynapse.class,
            PatternCategoryInputSynapse.class,
            InputObjectSynapse.class,
            SameObjectSynapse.class,
            InnerPositiveFeedbackSynapse.class,
            OuterPositiveFeedbackSynapse.class,
            RelationInputSynapse.class,
            BindingCategoryInputSynapse.class,
            NegativeFeedbackSynapse.class,
            InhibitorySynapse.class,
            InhibitoryCategoryInputSynapse.class,
            PatternCategorySynapse.class,
            BindingCategorySynapse.class,
            InhibitoryCategorySynapse.class
    );


    private static int getRequirements(SynapseType st) {
        return 0;
    }

    private static int getForbidden(SynapseType st) {
        return 0;
    }

    protected static final double[] SULW_ZERO = new double[] {0.0, 0.0};

    protected int synapseId;
    protected NeuronProvider input;
    protected NeuronProvider output;

    private boolean templateOnly;

    protected SumField weight = (SumField) new SumField(this, "weight", TOLERANCE)
            .setQueued(getDocument(), TRAINING)
            .addListener("onWeightModified", (fl, nr, u) -> {
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
        return synapseType.inputType();
    }

    public Type getOutputType() {
        return synapseType.outputType();
    }

    public Transition[] getTransitions() {
        return synapseType.transition();
    }

    public boolean checkForbiddenTransitions(Link l, Direction dir) {
        Transition[] curTrans = l.getSynapse().getTransitions();
        for(Transition ft: synapseType.forbidden())
            for(Transition t: curTrans)
                if(ft == t)
                    return false;

        return true;
    }

    public boolean checkRequiredTransitions(int state) {
        for(Transition rt: synapseType.required())
            if(!BitUtils.isSet(state, rt))
                return false;

        return true;
    }

    public abstract double[] getSumOfLowerWeights();

    public FieldOutput getInputValue(IA input) {
        return input.getValue();
    }

    protected void checkWeight() {
        if(isNegative())
            delete();
    }

    public LinkingMode getLinkingMode() {
        return synapseType.linkingMode();
    }

    public boolean isLinkingAllowed(boolean latent) {
        return true;
    }

    @Override
    public void disconnect() {
    }

    public void propagate(IA iAct) {
        if(propagateLinkExists(iAct))
            return;

        Document doc = iAct.getDocument();
        OA oAct = getOutput().createActivation(doc);

        createAndInitLink(iAct, oAct);
    }
    public static double getNetUB(Synapse synA, Synapse synB) {
        if(synB != null)
            return synA.getWeight().getUpdatedValue() + synB.getWeight().getUpdatedValue() +
                    Math.min(
                            synA.getSumOfLowerWeights()[0],
                            synB.getSumOfLowerWeights()[0]
                    );
         else
            return synA.getNetUB();
    }

    public double getNetUB() {
        return getWeight().getUpdatedValue() + getSumOfLowerWeights()[isLinkingAllowed(true) ? 1 : 0];
    }

    public double getNetUB(IA iAct) {
        return (getInputValue(iAct).getUpdatedValue() * getWeight().getUpdatedValue()) +
                getSumOfLowerWeights()[isLinkingAllowed(true) ? 1 : 0];
    }

    public static Link getLatentLink(Synapse synA, Synapse synB, Activation iActA, Activation iActB) {
        Stream<Link> linksA = iActA.getOutputLinks(synA);
        return linksA.filter(l -> synB.getLink(iActB, l.getOutput()) != null)
                .findAny()
                .orElse(null);
    }
/*
    public boolean checkSecondaryVisitorRun(Activation iAct, Activation oAct) {
        return true;
    }
*/
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

    public void linkAndPropagateOut(IA act) {
        getOutput()
                .linkOutgoing(this, act);

        if (isLinkingAllowed(true))
            getOutput()
                    .latentLinkOutgoing(this, act);

        if (isPropagable(act)) {
            propagate(act);
        }
    }

    protected boolean isPropagable(IA act) {
        return getRelation() == null &&
                getNetUB(act) > 0.0;
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
        S s;
        try {
            s = (S) getClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        s.initFromTemplate(input, output, this);
        return s;
    }

    public void initFromTemplate(I input, O output, Synapse templateSyn) {
        synapseId = templateSyn.synapseId;
        setInput(input);
        setOutput(output);

        link();

        weight.setValue(
                templateSyn.getInitialInstanceWeight()
        );
    }

    public double getInitialInstanceWeight() {
        return weight.getUpdatedValue();
    }

    public S setTemplateOnly(boolean templateOnly) {
        this.templateOnly = templateOnly;

        return (S) this;
    }

    public boolean isTemplateOnly() {
        return templateOnly;
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
        if(log.isDebugEnabled())
            log.debug("createAndInitLink: synapse:" + this + " input:" + input + " output:" + output);

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

    public Relation getRelation() {
        return null;
    }

    public void expandRelation(LinkingOperator op, Relation rel, Neuron to, Direction relDir) {
        Activation from = op.getSourceAct();
        PreActivation<?> toPreAct = to.getOrCreatePreActivation(from.getDocument());

        rel.evaluateLatentRelation(from, toPreAct, relDir.invert())
                .forEach(relAct -> {
                            if (log.isDebugEnabled())
                                log.debug(
                                        "REL " +
                                                "downBS:" + from.getClass().getSimpleName() + " " + from.getId() + " " + from.getLabel() + "  " +
                                                "upBS:" + relAct.getClass().getSimpleName() + " " + relAct.getId() + " " + relAct.getLabel()
                                );

                            op.relationCheck(this, relAct, relDir);
                        }
                );
    }

    public void createLatentRelation(OA oAct, Activation fromOriginAct, Activation toOriginAct) {
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

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getName());
        out.writeInt(synapseId);

        out.writeLong(input.getId());
        out.writeLong(output.getId());

        weight.write(out);
        out.writeBoolean(templateOnly);
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
        templateOnly = in.readBoolean();
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
