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
package network.aika.elements.synapses.innerinhibitoryloop;

import network.aika.Model;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.innerinhibitoryloop.InnerInhibitoryLink;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.InnerInhibitoryNeuron;
import network.aika.elements.neurons.relations.Relation;
import network.aika.elements.synapses.DisjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.fields.FieldOutput;
import network.aika.enums.LinkingMode;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.Type.*;
import static network.aika.enums.Scope.SAME;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        synapseTypeId = 10,
        inputType = BINDING,
        outputType = INNER_INHIBITORY,
        scope = SAME
)
public class InnerInhibitorySynapse extends DisjunctiveSynapse<
        InnerInhibitorySynapse,
        BindingNeuron,
        InnerInhibitoryNeuron,
        InnerInhibitoryLink,
        BindingActivation,
        InnerInhibitoryActivation
        > {
    private Relation relation;

    public InnerInhibitorySynapse() {
    }

    public InnerInhibitorySynapse(Relation rel) {
        this.relation = rel;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public boolean isLinkingAllowed(boolean latent) {
        return !latent;
    }

    @Override
    public FieldOutput getInputValue(BindingActivation input) {
        return input.getValueUnsuppressed();
    }

    @Override
    public InnerInhibitoryLink createLink(BindingActivation input, InnerInhibitoryActivation output) {
        return new InnerInhibitoryLink(this, input, output);
    }

    @Override
    public InnerInhibitorySynapse instantiateTemplate(BindingNeuron input, InnerInhibitoryNeuron output) {
        InnerInhibitorySynapse s = new InnerInhibitorySynapse();
        s.initFromTemplate(input, output, this);
        return s;
    }

    @Override
    public boolean checkLinkingMode(LinkingMode mode) {
        return mode == LinkingMode.UNSUPPRESSED;
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
