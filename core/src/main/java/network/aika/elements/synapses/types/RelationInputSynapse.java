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
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.LatentRelationActivation;
import network.aika.elements.links.types.RelationInputLink;
import network.aika.elements.neurons.types.LatentRelationNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.SynapseType;
import network.aika.enums.direction.Direction;
import network.aika.enums.direction.DirectionEnum;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.debugger.EventType.UPDATE;
import static network.aika.elements.NeuronType.BINDING;
import static network.aika.enums.Transition.INPUT_INPUT;

/**
 *
 * @author Lukas Molzberger
 */
@SynapseType(
        inputType = BINDING,
        outputType = BINDING,
        transition = INPUT_INPUT,
        required = INPUT_INPUT,
        propagateRange = false,
        storedAt = DirectionEnum.OUTPUT
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

    private int latentProxySynapseId;

    public RelationInputSynapse() {
    }

    public int getLatentProxySynapseId() {
        return latentProxySynapseId;
    }

    public void setLatentProxySynapseId(int latentProxySynapseId) {
        this.latentProxySynapseId = latentProxySynapseId;
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
    public void write(DataOutput out) throws IOException {
        super.write(out);

        out.writeInt(latentProxySynapseId);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        latentProxySynapseId = in.readInt();
    }
}
