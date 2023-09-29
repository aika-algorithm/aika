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
import network.aika.elements.activations.PatternActivation;
import network.aika.visitor.operator.LinkingOperator;

import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.enums.direction.Direction.OUTPUT;
import static network.aika.utils.Utils.depthToSpace;

/**
 * @author Lukas Molzberger
 */
public class RelationBindingVisitor extends BindingVisitor {

    public RelationBindingVisitor(Thought t, LinkingOperator operator) {
        super(t, operator);
    }

    public RelationBindingVisitor(BindingVisitor downVisitor, PatternActivation origin) {
        super(downVisitor, origin);
    }

    @Override
    public void up(PatternActivation origin, int depth) {
        if(direction.isUp())
            return;

        Synapse startSyn = operator.getStartSynapse();

        if (startSyn.getRelation() != null) {
            checkRelation(
                    origin,
                    operator.getStartSynapse(),
                    OUTPUT,
                    depth
            );
        } else {
            super.up(origin, depth);

            ((BindingNeuron) startSyn.getOutput())
                    .getSynapsesWithRelations()
                    .forEach(relSyn ->
                            checkRelation(origin, relSyn, INPUT, depth)
                    );
        }
    }

    private void checkRelation(PatternActivation downBindingSource, Synapse relSyn, Direction relDir, int depth) {
        relSyn.getRelation()
                .evaluateLatentRelation(downBindingSource, relDir.invert())
                .forEach(relTokenAct -> {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                        depthToSpace(depth) + "U-TURN (rel) " +
                                                "downBS:" + downBindingSource.getClass().getSimpleName() + " " + downBindingSource.getId() + " " + downBindingSource.getLabel() + "  " +
                                                "upBS:" + relTokenAct.getClass().getSimpleName() + " " + relTokenAct.getId() + " " + relTokenAct.getLabel());
                            }

                            new RelationUpBindingVisitor(
                                    this,
                                    downBindingSource,
                                    relTokenAct,
                                    relSyn,
                                    relDir
                            )
                                    .visit(relTokenAct, null, depth);
                        }
                );
    }
}
