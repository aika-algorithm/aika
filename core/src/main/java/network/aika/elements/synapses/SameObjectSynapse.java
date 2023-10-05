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
import network.aika.elements.activations.*;
import network.aika.elements.neurons.relations.Relation;
import network.aika.enums.Scope;
import network.aika.elements.links.SameObjectLink;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.enums.direction.Direction;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * The Same Object Binding Neuron Synapse is an inner synapse between two binding neurons of the same object.
 *
 * @author Lukas Molzberger
 */
public class SameObjectSynapse extends BindingNeuronSynapse<
        SameObjectSynapse,
        BindingNeuron,
        SameObjectLink,
        BindingActivation
        >
{
    private Integer relationSynId;

    public Integer getRelationSynId() {
        return relationSynId;
    }

    public void setRelationSynId(Integer relationSynId) {
        this.relationSynId = relationSynId;
    }

    @Override
    public Relation getRelation() {
        RelationInputSynapse ris = getRelationInputSynapse();
        if(ris == null)
            return null;

        return ris.getInput().getRelation();
    }

    @Override
    public void createLatentRelation(BindingActivation oAct, PatternActivation fromOriginAct, PatternActivation toOriginAct) {
        RelationInputSynapse ris = getRelationInputSynapse();
        if(ris.linkExists(oAct, true))
            return;

        LatentRelationActivation latentRelAct = ris.createOrLookupLatentActivation(
                fromOriginAct,
                toOriginAct
        );

        ris.createAndInitLink(latentRelAct, oAct);
    }

    @Override
    public RelationInputSynapse getRelationInputSynapse() {
        return (RelationInputSynapse) output.getSynapseBySynId(relationSynId);
    }

    @Override
    public SameObjectSynapse setTemplateOnly(boolean templateOnly) {
        super.setTemplateOnly(templateOnly);

        if(relationSynId != null)
            output.getSynapseBySynId(relationSynId)
                    .setTemplateOnly(templateOnly);

        return this;
    }

    @Override
    public Scope getScope() {
        return Scope.SAME;
    }

    @Override
    public SameObjectLink createLink(BindingActivation input, BindingActivation output) {
        return new SameObjectLink(this, input, output);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeBoolean(relationSynId != null);
        if(relationSynId != null)
            out.writeInt(relationSynId);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        if(in.readBoolean())
            relationSynId = in.readInt();
    }
}
