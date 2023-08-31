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
package network.aika.visitor.binding;

import network.aika.Thought;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.direction.Direction;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.LatentRelationActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.RelationInputSynapse;
import network.aika.visitor.operator.LinkingOperator;

import java.util.stream.Stream;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * @author Lukas Molzberger
 */
public class RelationBindingVisitor extends BindingVisitor {

    protected Synapse startSyn;
    protected PatternActivation downOrigin;
    protected PatternActivation upOrigin;

    protected RelationInputSynapse relation;
    protected Direction relationDir;

    public RelationBindingVisitor(Thought t, LinkingOperator operator, Synapse startSyn) {
        super(t, operator);

        this.startSyn = startSyn;
    }

    protected RelationBindingVisitor(
            RelationBindingVisitor parent,
            PatternActivation downOrigin,
            PatternActivation upOrigin,
            RelationInputSynapse relation,
            Direction relationDir
    ) {
        super(parent, downOrigin);
        this.downOrigin = downOrigin;
        this.upOrigin = upOrigin;
        this.relation = relation;
        this.relationDir = relationDir;
    }

    public PatternActivation getDownOrigin() {
        return downOrigin;
    }

    public PatternActivation getUpOrigin() {
        return upOrigin;
    }


    @Override
    public void expandRelations(PatternActivation origin, int depth) {
        BindingNeuron bn = (BindingNeuron) startSyn.getOutput();
        Stream<RelationInputSynapse> relations = bn.findLatentRelationNeurons().stream();

        if(startSyn.getRelationSynId() != null) {
            relationDir = OUTPUT;
            relations = relations.filter(s -> s.getSynapseId() == startSyn.getRelationSynId());
        } else
            relationDir = INPUT;

        relations.forEach(rel ->
                rel.getInput()
                        .evaluateLatentRelation(origin, relationDir.invert())
                        .forEach(relTokenAct ->
                                up(origin, relTokenAct, rel, relationDir, depth)
                        )
        );
    }

    private void up(
            PatternActivation origin,
            PatternActivation relOrigin,
            RelationInputSynapse relation,
            Direction relationDir,
            int depth
    ) {
        new RelationBindingVisitor(this, origin, relOrigin, relation, relationDir)
                .visit(relOrigin, null, depth);
    }

    public void up(PatternActivation origin, int depth) {
        if(!direction.isUp() && startSyn.getRelationSynId() == null)
            super.up(origin, depth);
    }

    @Override
    public boolean compatible(Synapse from, Synapse to) {
        if(downOrigin == null)
            return false;

        if(relationDir == OUTPUT)
            return true;

        Integer relId = to.getRelationSynId();
        return relId != null && relation.getSynapseId() == relId;
    }

    @Override
    public void createRelation(Link l) {
        if(relation.linkExists((BindingActivation) l.getOutput(), true))
            return;

        LatentRelationActivation latentRelAct = relation.createOrLookupLatentActivation(
                relationDir.getInput(downOrigin, upOrigin),
                relationDir.getOutput(downOrigin, upOrigin)
        );

        relation.createAndInitLink(latentRelAct, (BindingActivation) l.getOutput());
    }
}
