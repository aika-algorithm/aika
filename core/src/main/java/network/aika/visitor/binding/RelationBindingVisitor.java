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
import static network.aika.utils.Utils.depthToSpace;

/**
 * @author Lukas Molzberger
 */
public class RelationBindingVisitor extends BindingVisitor {

    protected Synapse startSyn;
    protected PatternActivation downBindingSource;
    protected PatternActivation upBindingSource;

    protected RelationInputSynapse relation;
    protected Direction relationDir;

    public RelationBindingVisitor(Thought t, LinkingOperator operator, Synapse startSyn) {
        super(t, operator);

        this.startSyn = startSyn;
    }

    protected RelationBindingVisitor(
            RelationBindingVisitor parent,
            PatternActivation downBindingSource,
            PatternActivation upBindingSource,
            RelationInputSynapse relation,
            Direction relationDir
    ) {
        super(parent, downBindingSource);
        this.downBindingSource = downBindingSource;
        this.upBindingSource = upBindingSource;
        this.relation = relation;
        this.relationDir = relationDir;
    }

    public PatternActivation getDownBindingSource() {
        return downBindingSource;
    }

    public PatternActivation getUpBindingSource() {
        return upBindingSource;
    }


    @Override
    public void expandRelations(PatternActivation downBindingSource, int depth) {
        Direction opDir = operator.getDirection();
        if(opDir == null)
            return;

        BindingNeuron bn = (BindingNeuron) startSyn.getOutput();
        Stream<RelationInputSynapse> relations = bn.findLatentRelationNeurons().stream();

        if(startSyn.getRelationSynId() != null) {
            relationDir = OUTPUT;
            relations = relations.filter(s -> s.getSynapseId() == startSyn.getRelationSynId());
        } else
            relationDir = INPUT;

        relations.forEach(rel ->
                rel.getInput()
                        .evaluateLatentRelation(downBindingSource, opDir.combine(relationDir).invert())
                        .forEach(relTokenAct ->
                                up(downBindingSource, relTokenAct, rel, relationDir, depth)
                        )
        );
    }

    private void up(
            PatternActivation downBindingSource,
            PatternActivation upBindingSource,
            RelationInputSynapse relation,
            Direction relationDir,
            int depth
    ) {
        if(log.isDebugEnabled()) {
            log.debug(
                    depthToSpace(depth) + "U-TURN (rel) " +
                            "downBS:" + downBindingSource.getClass().getSimpleName() + " " + downBindingSource.getId() + " " + downBindingSource.getLabel() + "  " +
                            "upBS:" + upBindingSource.getClass().getSimpleName() + " " + upBindingSource.getId() + " " + upBindingSource.getLabel());
        }

        new RelationBindingVisitor(this, downBindingSource, upBindingSource, relation, relationDir)
                .visit(upBindingSource, null, depth);
    }

    public void up(PatternActivation origin, int depth) {
        if(!direction.isUp() && startSyn.getRelationSynId() == null)
            super.up(origin, depth);
    }

    @Override
    public boolean compatible(Synapse from, Synapse to) {
        if(downBindingSource == null)
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
                relationDir.getInput(downBindingSource, upBindingSource),
                relationDir.getOutput(downBindingSource, upBindingSource)
        );

        relation.createAndInitLink(latentRelAct, (BindingActivation) l.getOutput());
    }
}
