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

import network.aika.FieldObject;
import network.aika.Model;
import network.aika.Thought;
import network.aika.callbacks.ActivationCheckCallback;
import network.aika.elements.activations.Activation;
import network.aika.elements.Element;
import network.aika.elements.links.Link;
import network.aika.elements.activations.Timestamp;
import network.aika.direction.Direction;
import network.aika.fields.QueueSumField;
import network.aika.fields.SumField;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.utils.Utils;
import network.aika.utils.Writable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

import static network.aika.direction.Direction.INPUT;
import static network.aika.fields.Fields.isTrue;
import static network.aika.elements.activations.Timestamp.MAX;
import static network.aika.elements.activations.Timestamp.MIN;
import static network.aika.steps.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Synapse<S extends Synapse, I extends Neuron, O extends Neuron<OA>, L extends Link<S, IA, OA>, IA extends Activation<?>, OA extends Activation> extends FieldObject implements Element, Writable {

    private static final Logger log = LoggerFactory.getLogger(Synapse.class);

    protected NeuronProvider input;
    protected NeuronProvider output;

    protected S template;

    protected SumField weight = (SumField) new QueueSumField(this, TRAINING, "weight", TOLERANCE, true)
            .addListener("onWeightModified", () ->
                    setModified()
            );

    protected boolean trainingAllowed = true;

    protected Scope scope;

    public Synapse(Scope scope) {
        this.scope = scope;
    }

    public abstract double getSumOfLowerWeights();

    public boolean checkLinkingEvent(Activation act) {
        return isTrue(act.getIsFired());
    }

    public Scope getScope() {
        return scope;
    }

    public void propagate(IA iAct) {
        if(propagateLinkExists(iAct))
            return;

        Thought t = iAct.getThought();
        OA oAct = getOutput().createActivation(t);

        createAndInitLink(iAct, oAct);
    }

    protected void warmUpRelatedInputNeurons(IA bs) {
    }

    public double getPropagatePreNet(IA iAct) {
        if (getOutput().isCallActivationCheckCallback() &&
                iAct != null
        ) {
            ActivationCheckCallback acc = iAct.getThought().getActivationCheckCallBack();
            if (acc == null || !acc.check(iAct))
                return -1000.0;
        }

        return getOutput().getCurrentCompleteBias() +
                getWeight().getUpdatedCurrentValue();
    }

    public static double getLatentLinkingPreNet(Synapse synA, Synapse synB) {
        double preUB = synA.getWeight().getUpdatedCurrentValue();

        if(synB != null) {
            preUB += synB.getWeight().getUpdatedCurrentValue() +
                    Math.min(
                            synA.getSumOfLowerWeights(),
                            synB.getSumOfLowerWeights()
                    );
        } else
            preUB += synA.getSumOfLowerWeights();

        return preUB;
    }

    public static boolean latentActivationExists(Synapse synA, Synapse synB, Activation iActA, Activation iActB) {
        Stream<Link> linksA = iActA.getOutputLinks(synA);
        return linksA.map(lA -> lA.getOutput())
                .map(oAct -> oAct.getInputLink(synB))
                .filter(Objects::nonNull)
                .map(lB -> lB.getInput())
                .anyMatch(iAct -> iAct == iActB);
    }

    public boolean linkExists(IA iAct, OA oAct) {
        assert iAct.getNeuron() == getInput();

        Link existingLink = oAct.getInputLink(iAct.getNeuron());
        if(existingLink == null)
            return false;

        assert existingLink.getSynapse() == this;

        if(existingLink.getInput() != iAct) {
            log.warn("Invalid relinking: (Previous iAct: " +
                    existingLink.getInput().getId() + ":" + existingLink.getInput().getLabel() +
                    " New iAct: " + iAct.getId() + ":" + iAct.getLabel() +
                    " oAct:" + oAct.getId() + ":" + oAct.getLabel()
            );
        }

        return true;
    }

    public boolean linkExists(OA oAct) {
        return oAct.getInputLink(getInput()) != null;
    }

    public static boolean synapseExists(Activation iAct, Activation oAct) {
        return oAct.getNeuron().getInputSynapse(iAct.getNeuronProvider()) != null;
    }

    public boolean propagateLinkExists(IA iAct) {
        return iAct.getOutputLinks(this)
                        .findAny()
                        .isPresent();
    }

    public void linkAndPropagateOut(IA act) {
        getOutput()
                .linkOutgoing(this, act);

        if (!isFeedbackSynapse())
            getOutput()
                    .latentLinkOutgoing(this, act);

        if (getPropagatePreNet(act) > 0.0) {
            propagate(act);
        } else if(getStoredAt() == INPUT) {
            warmUpRelatedInputNeurons(act);
        }
    }

    public boolean isFeedbackSynapse() {
        return false;
    }

    public void setModified() {
        Neuron n = getStoredAt().getNeuron(this);
        if(n != null)
            n.setModified();
    }

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
        setInput(input);
        setOutput(output);

        link();
        linkFields();

        weight.setInitialValue(
                templateSyn.weight.getUpdatedCurrentValue()
        );
    }

    public S init(Neuron input, Neuron output) {
        setInput((I) input);
        setOutput((O) output);

        link();
        linkFields();

        return (S) this;
    }

    public void linkFields() {
    }

    public void link() {
        input.linkInput(this);
        output.linkOutput(this);
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

    public SumField getWeight() {
        return weight;
    }

    public boolean isTrainingAllowed() {
        return trainingAllowed && getOutput().isTrainingAllowed();
    }

    public void setTrainingAllowed(boolean trainingAllowed) {
        this.trainingAllowed = trainingAllowed;
    }

    public S getTemplate() {
        return template;
    }

    public boolean isOfTemplate(Synapse templateSynapse) {
        if(template == templateSynapse)
            return true;

        if(template == null)
            return false;

        return template.isOfTemplate(templateSynapse);
    }

    public double getSortingWeight() {
        return getWeight().getUpdatedCurrentValue();
    }

    public Direction getStoredAt() {
        return INPUT;
    }

    public NeuronProvider getPInput() {
        return input;
    }

    public NeuronProvider getPOutput() {
        return output;
    }

    public I getInput() {
        if(input == null)
            return null;

        return (I) input.getNeuron();
    }

    public O getOutput() {
        if(output == null)
            return null;

        return (O) output.getNeuron();
    }

    public Model getModel() {
        return output != null ?
                output.getModel() :
                null;
    }

    public boolean isZero() {
        return Utils.belowTolerance(TOLERANCE, weight.getCurrentValue());
    }

    public boolean isNegative() {
        return weight.getCurrentValue() < 0.0;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(getClass().getName());

        out.writeLong(input.getId());
        out.writeLong(output.getId());

        weight.write(out);
    }

    public static Synapse read(DataInput in, Model m) throws IOException {
        String synClazz = in.readUTF();

        Synapse s = (Synapse) m.suspensionCallback.modelClass(synClazz, m);
        s.readFields(in, m);
        return s;
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        input = m.lookupNeuronProvider(in.readLong());
        output = m.lookupNeuronProvider(in.readLong());

        weight.readFields(in, m);
    }

    @Override
    public Timestamp getCreated() {
        return MIN;
    }

    @Override
    public Timestamp getFired() {
        return MAX;
    }

    @Override
    public Thought getThought() {
        Model m = getModel();
        return m != null ?
                m.getCurrentThought() :
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
