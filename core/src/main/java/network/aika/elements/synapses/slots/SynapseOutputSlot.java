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

import network.aika.elements.activations.Activation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.enums.direction.Direction;
import network.aika.fields.Field;

import static network.aika.fields.Fields.add;
import static network.aika.fields.link.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.excludeInput;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseOutputSlot<S extends ConjunctiveSynapse, L extends ConjunctiveLink> extends ConjunctiveSynapseSlot<S, L> {

    protected Field outputNet;

    public SynapseOutputSlot(Activation act, S synapse) {
        super(act, synapse, Direction.OUTPUT);
    }

    @Override
    public void init() {
        super.init();

        outputNet = add(
                this,
                "outputNet",
                excludeInput(
                        this,
                        "outputNet-exclude",
                        synapse.getOutputNet(act),
                        maxField
                ),
                synapse.getWeight()
        );
    }

    public Field getOutputNet() {
        return outputNet;
    }

    public void connectToActivation() {
        linkAndConnect(maxField, synapse.getOutputNet(act));
    }

    @Override
    public void disconnect() {
        super.disconnect();

        outputNet.disconnectAndUnlinkInputs(false);
    }

    @Override
    protected String getLabel() {
        return "out-slot-" + synapse.getPInput().getId();
    }
}
