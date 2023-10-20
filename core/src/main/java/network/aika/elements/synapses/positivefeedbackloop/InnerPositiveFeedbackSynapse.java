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
package network.aika.elements.synapses.positivefeedbackloop;

import network.aika.Model;
import network.aika.elements.Type;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.relations.Relation;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.Scope;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.positivefeedbackloop.InnerPositiveFeedbackLink;
import network.aika.elements.neurons.PatternNeuron;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.Type.*;
import static network.aika.enums.Scope.INPUT;
import static network.aika.enums.Scope.SAME;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        synapseTypeId = 4,
        inputType = PATTERN,
        outputType = BINDING,
        scope = SAME
)
public class InnerPositiveFeedbackSynapse extends PositiveFeedbackSynapse<
        InnerPositiveFeedbackSynapse,
        PatternNeuron,
        BindingNeuron,
        InnerPositiveFeedbackLink,
        PatternActivation,
        BindingActivation
        >
{
    private Relation relation;

    public InnerPositiveFeedbackSynapse() {
    }

    public InnerPositiveFeedbackSynapse(Relation rel) {
        this.relation = rel;
    }

    @Override
    public Relation getRelation() {
        return relation;
    }

    @Override
    public boolean checkSingularLinkDoesNotExist(BindingActivation oAct) {
        return !linkExists(oAct, false);
    }

    public InnerPositiveFeedbackLink createLink(PatternActivation input, BindingActivation output) {
        return new InnerPositiveFeedbackLink(this, input, output);
    }

    @Override
    public void setPropagable(boolean propagable) {
    }

    @Override
    public void linkAndPropagateOut(PatternActivation act) {
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
