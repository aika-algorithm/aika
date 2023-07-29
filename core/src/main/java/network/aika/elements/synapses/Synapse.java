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
import network.aika.enums.Scope;
import network.aika.Thought;
import network.aika.callbacks.ActivationCheckCallback;
import network.aika.elements.activations.Activation;
import network.aika.elements.Element;
import network.aika.elements.links.Link;
import network.aika.elements.Timestamp;
import network.aika.enums.direction.Direction;
import network.aika.fields.QueueSumField;
import network.aika.fields.MultiInputField;
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

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.elements.Timestamp.MAX;
import static network.aika.elements.Timestamp.MIN;
import static network.aika.steps.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Synapse<S extends Synapse, I extends Neuron, O extends Neuron<OA>, L extends Link<S, IA, OA>, IA extends Activation<?>, OA extends Activation> implements Element, Writable {

    protected static final Logger log = LoggerFactory.getLogger(Synapse.class);

    protected NeuronProvider input;
    protected NeuronProvider output;

    protected S template;

    protected MultiInputField weight = (MultiInputField) new QueueSumField(this, TRAINING, "weight", TOLERANCE)
            .addListener("onWeightModified", (fl, nr, u) -> {
                checkWeight();
                setModified();
            }, true);

    protected boolean trainingAllowed = true;

    protected Scope scope;

    public Synapse(Scope scope) {
        this.scope = scope;
    }

    public abstract double getSumOfLowerWeights();

    public boolean checkLinkingEvent(Activation act) {
        return act.isFired();
    }

    protected void checkWeight() {
        if(isNegative())
            delete();
    }

    public Scope getScope() {
        return scope;
    }

    public int outgoingLinkingOrder() {
        return 0;
    }

    public boolean isLatentLinkingAllowed() {
        return true;
    }

    @Override
    public void disconnect() {
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
            if (acc == null || !acc.check(this, iAct))
                return -1000.0;
        }

        return getOutput().getCurrentCompleteBias() +
                getWeight().getUpdatedValue();
    }

    public static double getLatentLinkingPreNet(Synapse synA, Synapse synB) {
        double preUB = synA.getWeight().getUpdatedValue();

        if(synB != null) {
            preUB += synB.getWeight().getUpdatedValue() +
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
        return linksA.map(Link::getOutput)
                .anyMatch(oAct ->
                        oAct.getInputLink(iActB) != null
                );
    }

    public L getDummyLink(OA oAct) {
        return (L) oAct.getInputDummyLink(input.getNeuron());
    }

    public L checkExistingLink(IA iAct, OA oAct) {
        return (L) oAct.getInputLink(iAct);
    }

    public boolean linkExists(OA oAct) {
        return oAct.getInputLinks(this)
                .findAny()
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

        if (isLatentLinkingAllowed())
            getOutput()
                    .latentLinkOutgoing(this, act);

        if (getPropagatePreNet(act) > 0.0) {
            propagate(act);
        } else if(getStoredAt() == INPUT) {
            warmUpRelatedInputNeurons(act);
        }
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

        setInitialWeight(templateSyn);
    }

    protected void setInitialWeight(Synapse templateSyn) {
        weight.setInitialValue(
                templateSyn.weight.getUpdatedValue()
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

    public MultiInputField getWeight() {
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
        return getWeight().getUpdatedValue();
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

        return input.getNeuron();
    }

    public O getOutput() {
        if(output == null)
            return null;

        return output.getNeuron();
    }

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

        out.writeLong(input.getId());
        out.writeLong(output.getId());

        weight.write(out);
    }

    public static Synapse read(DataInput in, Model m) throws IOException {
        String synClazz = in.readUTF();

        Synapse s = (Synapse) m.modelClass(synClazz);
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

    public void delete() {
        if(log.isInfoEnabled())
            log.info("Delete synapse: " + this);

        input.removeOutputSynapse(this);
        output.removeInputSynapse(this);
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
