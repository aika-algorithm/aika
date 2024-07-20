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
import network.aika.elements.neurons.Neuron;
import network.aika.elements.links.Link;
import network.aika.queue.Timestamp;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class ConjunctiveSynapse extends Synapse
{

    private boolean optional;

    protected boolean propagable;

    protected Integer relActTimeSum;
    protected Integer relActTimeN;


    public ConjunctiveSynapse() {
    }

    public ConjunctiveSynapse(Neuron input, Neuron output) {
        super(input, output);
    }

    @Override
    public void count(Link l) {
        super.count(l);

        if(l.getInput() == null)
            return;

        Timestamp inFired = l.getInput().getFired(StateType.NON_FEEDBACK);
        Timestamp outFired = l.getOutput().getFired(StateType.NON_FEEDBACK);

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

    public Synapse setRelativeActivationTime(Integer relActTimeSum, Integer relActTimeN) {
        this.relActTimeSum = relActTimeSum;
        this.relActTimeN = relActTimeN;

        return this;
    }

    public Float getAvgRelActTime() {
        if(relActTimeSum == null)
            return null;

        return ((float) relActTimeSum) / ((float) relActTimeN);
    }

    public boolean isOptional() {
        return optional;
    }

    public Synapse setOptional(boolean optional) {
        this.optional = optional;

        return this;
    }

    @Override
    public void initFromTemplate(Neuron input, Neuron output, Synapse templateSyn) {
        super.initFromTemplate(input, output, templateSyn);
        setPropagable(templateSyn.isPropagable());
    }

    public Synapse setPropagable(boolean propagable) {
        if(this.propagable != propagable)
            input.getNeuron().setModified();

        getInput().updatePropagable(output, propagable);
        this.propagable = propagable;

        return this;
    }

    @Override
    public boolean isPropagable() {
        return propagable;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

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

        propagable = in.readBoolean();
        optional = in.readBoolean();

        if(in.readBoolean()) {
            relActTimeSum = in.readInt();
            relActTimeN = in.readInt();
        }
    }
}
