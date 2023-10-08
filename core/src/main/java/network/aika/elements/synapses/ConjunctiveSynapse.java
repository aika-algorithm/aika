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
import network.aika.enums.direction.Direction;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.Link;
import network.aika.fields.SumField;
import network.aika.utils.Utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveSynapse<S extends ConjunctiveSynapse<S, I, O, L, IA, OA>, I extends Neuron, O extends ConjunctiveNeuron<O, OA>, L extends Link<S, IA, OA>, IA extends Activation<I>, OA extends ConjunctiveActivation<O>> extends
        Synapse<S, I, O, L, IA, OA>
{

    protected SumField synapseBias = (SumField) new SumField(this, "synapseBias", TOLERANCE)
            .setQueued(getThought(), TRAINING)
            .addListener("onSynapseBiasModified", (fl, nr, u) -> {
                        setModified();
                        getOutput().updateSumOfLowerWeights();
                    },
                    true
            );

    private boolean optional;

    private double[] sumOfLowerWeights = SULW_ZERO;
    protected boolean propagable;

    public ConjunctiveSynapse() {
        synapseBias.setValue(0.0);
    }

    public void initBiasInput(OA act) {
        linkAndConnect(synapseBias, getOutputNetForBias(act))
                .setPropagateUpdates(false);
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
        synapseBias.setInitialValue(
                ((ConjunctiveSynapse)templateSyn).synapseBias.getUpdatedValue()
        );
        super.initFromTemplate(input, output, templateSyn);
    }

    public void setPropagable(boolean propagable) {
        if(this.propagable != propagable)
            input.getNeuron().setModified();

        getInput().updatePropagable(output, propagable);
        this.propagable = propagable;
    }

    public boolean isPropagable() {
        return propagable;
    }

    public RelationInputSynapse getRelationInputSynapse() {
        return null;
    }

    @Override
    public void setModified() {
        O no = getOutput();
        if(no != null)
            no.setModified();
    }

    @Override
    public Direction getStoredAt() {
        return OUTPUT;
    }

    @Override
    public double[] getSumOfLowerWeights() {
        return sumOfLowerWeights;
    }

    public void setSumOfLowerWeights(double[] sumOfLowerWeights) {
        if(!Utils.belowTolerance(TOLERANCE, this.sumOfLowerWeights[0] - sumOfLowerWeights[0]))
            setModified();

        this.sumOfLowerWeights = sumOfLowerWeights;
    }

    public S adjustBias() {
        return adjustBias(
                getInput().getTargetValue()
        );
    }

    public S adjustBias(double inputValueTarget) {
        if(weight.getValue() > 0.0)
            synapseBias.receiveUpdate(null, false, -weight.getValue() * inputValueTarget);

        return (S) this;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        synapseBias.write(out);
        out.writeDouble(sumOfLowerWeights[0]);
        out.writeDouble(sumOfLowerWeights[1]);
        out.writeBoolean(propagable);
        out.writeBoolean(optional);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        synapseBias.readFields(in, m);
        sumOfLowerWeights = new double[] {
                in.readDouble(),
                in.readDouble()
        };
        propagable = in.readBoolean();
        optional = in.readBoolean();
    }
}
