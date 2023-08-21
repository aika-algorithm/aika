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
import network.aika.enums.direction.Direction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.Link;
import network.aika.fields.FieldLink;
import network.aika.fields.QueueInterceptor;
import network.aika.fields.SumField;
import network.aika.utils.Utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveSynapse<S extends ConjunctiveSynapse, I extends Neuron, O extends ConjunctiveNeuron<OA>, L extends Link<S, IA, OA>, IA extends Activation<?>, OA extends ConjunctiveActivation> extends
        Synapse<S, I, O, L, IA, OA>
{

    protected SumField synapseBias = (SumField) new SumField(this, "synapseBias", TOLERANCE)
            .setQueued(getThought(), TRAINING)
            .addListener("onSynapseBiasModified", (fl, nr, u) ->
                    setModified(),
                    true
            );

    private boolean optional;

    private double sumOfLowerWeights;
    protected Direction currentStoredAt = INPUT;


    public ConjunctiveSynapse(Scope scope) {
        super(scope);
    }

    @Override
    public S init(Neuron input, Neuron output) {
        synapseBias.setValue(0.0);
        return super.init(input, output);
    }

    @Override
    public void linkFields() {
        if(!optional && !output.isSuspended()) {
            if(biasLinkExists(synapseBias, getOutput().getSynapseBiasSum()))
                return;

            linkAndConnect(synapseBias, getOutput().getSynapseBiasSum());
        }
    }

    private boolean biasLinkExists(SumField synapseBias, SumField synapseBiasSum) {
        return synapseBias.getReceivers()
                .stream()
                .filter(fl -> fl instanceof FieldLink)
                .map(fl -> (FieldLink) fl)
                .anyMatch(fl -> fl.getOutput() == synapseBiasSum);
    }

    public S setSynapseBias(double b) {
        synapseBias.setValue(b);

        return (S) this;
    }

    public SumField getSynapseBias() {
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
        super.initFromTemplate(input, output, templateSyn);

        synapseBias.setInitialValue(
                ((ConjunctiveSynapse)templateSyn).synapseBias.getUpdatedValue()
        );
    }

    @Override
    protected void warmUpRelatedInputNeurons(IA bs) {
        Stream<ConjunctiveSynapse> iSyns = output.getNeuron().getInputSynapsesByType(ConjunctiveSynapse.class);
        iSyns.filter(s -> s.getStoredAt() == OUTPUT)
                .forEach(s ->
                        s.warmUpInputNeuron(bs.getThought())
                );
    }

    protected void warmUpInputNeuron(Thought t) {
        input.getNeuron()
                .getOrCreatePreActivation(t)
                .addOutputSynapse(this);
    }

    @Override
    public Direction getStoredAt() {
        return currentStoredAt;
    }

    public void setStoredAt(Direction newStoredAt) {
        if(currentStoredAt != newStoredAt) {
            input.getNeuron().setModified();
            output.getNeuron().setModified();
        }

        currentStoredAt = newStoredAt;
    }

    @Override
    public double getSumOfLowerWeights() {
        return sumOfLowerWeights;
    }

    public void setSumOfLowerWeights(double sumOfLowerWeights) {
        if(!Utils.belowTolerance(TOLERANCE, this.sumOfLowerWeights - sumOfLowerWeights))
            setModified();

        this.sumOfLowerWeights = sumOfLowerWeights;
    }


    public S adjustBias() {
        return adjustBias(1.0);
    }

    public S adjustBias(double x) {
        if(weight.getValue() > 0.0)
            synapseBias.receiveUpdate(null, false, -weight.getValue() * x);

        return (S) this;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeDouble(sumOfLowerWeights);
        out.writeBoolean(currentStoredAt == OUTPUT);
        out.writeBoolean(optional);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        sumOfLowerWeights = in.readDouble();
        currentStoredAt = in.readBoolean() ? OUTPUT : INPUT;
        optional = in.readBoolean();

        linkFields();
    }
}
