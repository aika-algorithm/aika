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

import network.aika.elements.activations.PatternActivation;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.enums.Scope;
import network.aika.Thought;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.LatentRelationActivation;
import network.aika.elements.links.RelationInputLink;
import network.aika.enums.direction.Direction;
import network.aika.elements.neurons.relations.LatentRelationNeuron;

import static network.aika.debugger.EventType.UPDATE;

/**
 *
 * @author Lukas Molzberger
 */
public class RelationInputSynapse extends BindingNeuronSynapse<
        RelationInputSynapse,
        LatentRelationNeuron,
        RelationInputLink,
        LatentRelationActivation
        >
{
    private NeuronProvider correspondingSPSInput;


    public RelationInputSynapse() {
        super(Scope.INPUT);
        currentStoredAt = Direction.OUTPUT;
    }

    public NeuronProvider getCorrespondingSPSInput() {
        return correspondingSPSInput;
    }

    public void setCorrespondingSPSInput(NeuronProvider spsInput) {
        this.correspondingSPSInput = spsInput;
    }

    public void setCorrespondingSPS(SameObjectSynapse correspondingSPS) {
        setCorrespondingSPSInput(correspondingSPS.getPInput());
    }

    @Override
    public void setStoredAt(Direction storedAt) {
    }

    @Override
    protected void warmUpInputNeuron(Thought t) {
    }

    @Override
    public void linkAndPropagateOut(LatentRelationActivation act) {
    }

    @Override
    public RelationInputLink createLink(LatentRelationActivation input, BindingActivation output) {
        return new RelationInputLink(this, input, output);
    }

    public LatentRelationActivation createOrLookupLatentActivation(PatternActivation fromOriginAct, PatternActivation toOriginAct) {
        return fromOriginAct.getToRelations().computeIfAbsent(getInput(), n -> {
            LatentRelationActivation relAct = getInput().createActivation(fromOriginAct.getThought());
            relAct.setFromAct(fromOriginAct);
            relAct.setToAct(toOriginAct);
            fromOriginAct.getThought().onElementEvent(UPDATE, relAct);
            return relAct;
        });
    }

    @Override
    public double getSortingWeight() {
        return 0.0;
    }
}
