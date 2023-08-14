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
import network.aika.enums.direction.Direction;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.LatentRelationActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.activations.TokenActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.RelationInputSynapse;
import network.aika.enums.Scope;
import network.aika.visitor.operator.LinkingOperator;

/**
 * @author Lukas Molzberger
 */
public class RelationBindingVisitor extends BindingVisitor {

    protected RelationInputSynapse relation;
    protected Direction relationDir;
    protected TokenActivation downOrigin;
    protected TokenActivation upOrigin;

    public RelationBindingVisitor(Thought t, LinkingOperator operator, RelationInputSynapse rel, Direction relationDir) {
        super(t, operator);

        this.relation = rel;
        this.relationDir = relationDir;
    }

    protected RelationBindingVisitor(RelationBindingVisitor parent, TokenActivation downOrigin, TokenActivation upOrigin) {
        super(parent, downOrigin);
        this.downOrigin = downOrigin;
        this.upOrigin = upOrigin;
        this.relation = parent.relation;
        this.relationDir = parent.relationDir;
    }

    public RelationInputSynapse getRelation() {
        return relation;
    }

    public Direction getRelationDir() {
        return relationDir;
    }

    public PatternActivation getDownOrigin() {
        return downOrigin;
    }

    public TokenActivation getUpOrigin() {
        return upOrigin;
    }

    @Override
    public void expandRelations(TokenActivation origin, int depth) {
        RelationInputSynapse relSyn = getRelation();

        if(!operator.verifySamePatternSynapse(relSyn.getCorrespondingSPSInput()))
            return;

        relSyn.getInput()
                .evaluateLatentRelation(origin, relationDir)
                .forEach(relTokenAct ->
                        up(origin, relTokenAct, depth)
                );
    }

    private void up(TokenActivation origin, TokenActivation relOrigin, int depth) {
        new RelationBindingVisitor(this, origin, relOrigin)
                .visit(relOrigin, null, depth);
    }

    @Override
    public boolean compatible(Scope from, Scope to) {
        if(downOrigin == null)
            return false;

        if(from == to)
            return downOrigin == upOrigin;

        if(from != to)
            return downOrigin != upOrigin;

        return false;
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
