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

import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.enums.Scope;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.LatentRelationActivation;
import network.aika.elements.links.RelationInputLink;
import network.aika.elements.neurons.LatentRelationNeuron;
import network.aika.enums.direction.Direction;

import static network.aika.debugger.EventType.UPDATE;
import static network.aika.elements.Type.BINDING;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        synapseTypeId = 6,
        inputType = BINDING,
        outputType = BINDING,
        scope = Scope.INPUT
)
public class RelationInputSynapse extends ConjunctiveSynapse<
        RelationInputSynapse,
        LatentRelationNeuron,
        BindingNeuron,
        RelationInputLink,
        LatentRelationActivation,
        BindingActivation
        >
{
    public RelationInputSynapse() {
    }

    @Override
    public void setPropagable(boolean propagable) {
    }

    @Override
    public void linkAndPropagateOut(LatentRelationActivation act) {
    }

    @Override
    public RelationInputLink createLink(LatentRelationActivation input, BindingActivation output) {
        return new RelationInputLink(this, input, output);
    }

    public LatentRelationActivation createOrLookupLatentActivation(Activation fromOriginAct, Activation toOriginAct) {
        return getInput().getOrCreatePreActivation(fromOriginAct.getDocument())
                .getRelatedTokensByTokenPosition(Direction.INPUT, fromOriginAct.getTextReference().getTokenPosRange())
                .map(LatentRelationActivation.class::cast)
                .findFirst()
                .orElseGet(() -> {
                            LatentRelationActivation rAct = getInput().createActivation(fromOriginAct.getDocument());
                            rAct.setFromAct(fromOriginAct);
                            rAct.setToAct(toOriginAct);
                            fromOriginAct.getDocument().onElementEvent(UPDATE, rAct);
                            return rAct;
                        }
                );
    }

    @Override
    public double getSortingWeight() {
        return 0.0;
    }
}
