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
package network.aika.elements.synapses.types;

import network.aika.Model;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.SynapseType;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.types.InnerPositiveFeedbackLink;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.PositiveFeedbackSynapse;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.elements.Type.*;
import static network.aika.enums.LinkingMode.FEEDBACK;
import static network.aika.enums.Transition.SAME_SAME;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = PATTERN,
        outputType = BINDING,
        transition = SAME_SAME,
        required = SAME_SAME,
        linkingMode = FEEDBACK
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

    public InnerPositiveFeedbackLink createLink(PatternActivation input, BindingActivation output) {
        return new InnerPositiveFeedbackLink(this, input, output);
    }

    @Override
    public void setPropagable(boolean propagable) {
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
