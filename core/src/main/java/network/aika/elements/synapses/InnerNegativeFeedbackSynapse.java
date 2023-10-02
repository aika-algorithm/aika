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
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.InnerNegativeFeedbackLink;
import network.aika.elements.neurons.InnerInhibitoryNeuron;
import network.aika.elements.neurons.relations.Relation;
import network.aika.enums.Scope;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class InnerNegativeFeedbackSynapse extends FeedbackSynapse<
        InnerNegativeFeedbackSynapse,
        InnerInhibitoryNeuron,
        InnerNegativeFeedbackLink,
        InnerInhibitoryActivation
        >
{
    private Relation relation;

    public InnerNegativeFeedbackSynapse() {
    }

    public InnerNegativeFeedbackSynapse(Relation rel) {
        this.relation = rel;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public void setPropagable(boolean propagable) {
    }

    @Override
    public Scope getScope() {
        return Scope.SAME;
    }

    @Override
    protected void checkWeight() {
        if(isNegative())
            delete();
    }

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return false;
    }

    @Override
    public InnerNegativeFeedbackLink createLink(InnerInhibitoryActivation input, BindingActivation output) {
        return new InnerNegativeFeedbackLink(this, input, output);
    }

    @Override
    public void initDummyLink(BindingActivation oAct) {
    }

    @Override
    public void linkAndPropagateOut(InnerInhibitoryActivation act) {
        getOutput()
                .linkOutgoing(this, act);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeBoolean(relation != null);
        if(relation != null)
            relation.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        if(in.readBoolean())
            relation = Relation.read(in, m);
    }
}
