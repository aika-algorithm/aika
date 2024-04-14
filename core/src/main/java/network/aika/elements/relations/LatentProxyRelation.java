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
package network.aika.elements.relations;

import network.aika.Model;
import network.aika.elements.PreActivation;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.types.RelationInputSynapse;
import network.aika.enums.direction.Direction;
import network.aika.text.TextReference;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class LatentProxyRelation extends Relation {

    private Integer relationSynId;

    public LatentProxyRelation() {
    }

    @Override
    public Relation instantiate() {
        return new LatentProxyRelation();
    }

    @Override
    public void linkRelationFromTemplate(Activation instanceOAct, Synapse instanceSyn, Link templateLink) {
        Synapse templateSyn = templateLink.getSynapse();
        LatentProxyRelation templateRel = (LatentProxyRelation) templateSyn.getRelation();
        Integer relSynId = instanceOAct.getInstanceSynapseId(templateRel.relationSynId);
        if(relSynId != null) {
            Synapse ris = instanceSyn.getPOutput().getSynapseBySynId(relSynId);
            assert ris.getSynapseId() == relSynId;

            linkRelation(instanceSyn, ris);
        }
    }

    public void linkRelation(Synapse syn, Synapse relSyn) {
        relationSynId = relSyn.getSynapseId();
        relSyn.setLatentProxySynapseId(syn.getSynapseId());
    }

    public void setRelationSynId(int relationSynId) {
        this.relationSynId = relationSynId;
    }

    public int getRelationSynId() {
        return relationSynId;
    }

    @Override
    public int getRelationType() {
        return 5;
    }

    @Override
    public Stream<Activation> evaluateLatentRelation(Synapse s, TextReference ref, Activation fromAct, PreActivation toPreAct, Direction dir) {
        assert relationSynId != null;

        Synapse ris = s.getPOutput().getSynapseBySynId(relationSynId);
        return ris.getInput().getRelation()
                .evaluateLatentRelation(s, ref, fromAct, toPreAct, dir);
    }

    @Override
    public void createLatentRelation(Activation oAct, Activation fromOriginAct, Activation toOriginAct) {
        BindingActivation bAct = (BindingActivation) oAct;
        bAct.createLatentRelation(relationSynId, fromOriginAct, toOriginAct);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        out.writeInt(relationSynId);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);
        relationSynId = in.readInt();
    }

    @Override
    public String toString() {
        return "LatentProxyRelation: " + " relationSynId:" + relationSynId;
    }
}
