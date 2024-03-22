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
import network.aika.elements.activations.StateType;
import network.aika.elements.synapses.slots.SynapseInputSlot;
import network.aika.elements.synapses.slots.SynapseOutputSlot;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.enums.direction.Direction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.Link;
import network.aika.queue.Timestamp;
import network.aika.fields.Field;
import network.aika.fields.SumField;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.fields.link.FieldLink.linkAndConnect;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveSynapse<
            S extends ConjunctiveSynapse,
            I extends Neuron,
            O extends ConjunctiveNeuron<O, OA>,
            L extends Link<S, IA, OA, SynapseInputSlot, SynapseOutputSlot>,
            IA extends Activation<?>,
            OA extends ConjunctiveActivation<?>
        >
        extends Synapse<S, I, O, L, IA, OA>
{

    protected Field synapseBias = new SumField(this, "synapseBias", TOLERANCE)
            .setQueued(getQueue(), TRAINING, false);

    private boolean optional;

    protected boolean propagable;

    protected Integer relActTimeSum;
    protected Integer relActTimeN;


    public ConjunctiveSynapse() {
        synapseBias.setValue(0.0);
    }

    @Override
    public SynapseSlot createInputSlot(IA iAct) {
        return new SynapseInputSlot(iAct, this);
    }

    @Override
    public SynapseSlot createOutputSlot(OA oAct) {
        return new SynapseOutputSlot(oAct, this);
    }

    @Override
    public SynapseSlot createAndInitOutputSlot(OA oAct) {
        SynapseOutputSlot slot = (SynapseOutputSlot) super.createAndInitOutputSlot(oAct);
        slot.connectToActivation();
        return slot;
    }

    public void initBiasInput(OA act) {
        linkAndConnect(synapseBias, act.getNet(synapseType.outputState()))
                .setPropagateUpdates(false);
    }

    public S setSynapseBias(double b) {
        synapseBias.setValue(b);

        return (S) this;
    }

    @Override
    public void count(L l) {
        super.count(l);

        if(l.getInput() == null)
            return;

        Timestamp inFired = l.getInput().getFired(StateType.PRE_FEEDBACK);
        Timestamp outFired = l.getOutput().getFired(StateType.PRE_FEEDBACK);

        if(inFired != null && outFired != null) {
            if(relActTimeSum == null) {
                relActTimeSum = 0;
                relActTimeN = 0;
            }
            relActTimeSum += (int) (inFired.getTimestamp() - outFired.getTimestamp());
            relActTimeN++;
            setModified();
        }
    }

    public S setRelativeActivationTime(Integer relActTimeSum, Integer relActTimeN) {
        this.relActTimeSum = relActTimeSum;
        this.relActTimeN = relActTimeN;

        return (S) this;
    }

    public Float getAvgRelActTime() {
        if(relActTimeSum == null)
            return null;

        return ((float) relActTimeSum) / ((float) relActTimeN);
    }

    public Field getSynapseBias() {
        return synapseBias;
    }

    public boolean isOptional() {
        return optional;
    }

    public S setOptional(boolean optional) {
        this.optional = optional;

        return (S) this;
    }

    @Override
    public void initFromTemplate(I input, O output, Synapse templateSyn) {
        synapseBias.setInitialValue(
                ((ConjunctiveSynapse)templateSyn).synapseBias.getUpdatedValue()
        );

        super.initFromTemplate(input, output, templateSyn);
        setPropagable(templateSyn.isPropagable());
    }

    public S setPropagable(boolean propagable) {
        if(this.propagable != propagable)
            input.getNeuron().setModified();

        getInput().updatePropagable(output, propagable);
        this.propagable = propagable;

        return (S) this;
    }

    @Override
    public boolean isPropagable() {
        return propagable;
    }

    @Override
    public void setModified() {
        O no = getOutput();
        if(no != null)
            no.setModified();
    }

    @Override
    public boolean isWeak() {
        return getOutput().getBias().getUpdatedValue() > -synapseBias.getUpdatedValue();
    }

    public S adjustBias() {
        return adjustBias(
                getInput().getTargetValue()
        );
    }

    public S adjustBias(double inputValueTarget) {
        if(weight.getValue() > 0.0)
            synapseBias.receiveUpdate(null, -weight.getValue() * inputValueTarget);

        return (S) this;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        synapseBias.write(out);
        out.writeBoolean(propagable);
        out.writeBoolean(optional);

        out.writeBoolean(relActTimeSum != null);
        if(relActTimeSum != null) {
            out.writeInt(relActTimeSum);
            out.writeInt(relActTimeN);
        }
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        synapseBias.readFields(in);
        propagable = in.readBoolean();
        optional = in.readBoolean();

        if(in.readBoolean()) {
            relActTimeSum = in.readInt();
            relActTimeN = in.readInt();
        }
    }
}
