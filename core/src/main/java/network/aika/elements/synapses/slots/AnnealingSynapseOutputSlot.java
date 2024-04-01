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
package network.aika.elements.synapses.slots;

import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.fields.*;

import static network.aika.elements.synapses.slots.AnnealingType.CATEGORY_INPUT;
import static network.aika.fields.Fields.*;
import static network.aika.fields.link.FieldLink.linkAndConnect;

/**
 *
 * @author Lukas Molzberger
 */
public class AnnealingSynapseOutputSlot extends SynapseOutputSlot<ConjunctiveSynapse, ConjunctiveLink> {

    private AnnealingType annealingType;

    protected Field mixField;

    public AnnealingSynapseOutputSlot(ConjunctiveActivation act, ConjunctiveSynapse synapse, AnnealingType at) {
        super(act, synapse);

        this.annealingType = at;
    }

    @Override
    public void init() {
        super.init();

        mixField = mix(
                this,
                "annealed input value",
                ((ConjunctiveActivation)act).getAnnealingValue(annealingType),
                synapse.getWeightForAnnealing(),
                this
        );
    }

    public AnnealingType getAnnealingType() {
        return annealingType;
    }

    @Override
    protected boolean isNegativeInputAllowed() {
        return annealingType == CATEGORY_INPUT ||
                super.isNegativeInputAllowed();
    }

    @Override
    public void connectToActivation() {
        linkAndConnect(mixField, synapse.getOutputNet(act));
    }

    @Override
    public void disconnect() {
        super.disconnect();
        mixField.unlinkInputs();
    }
}
