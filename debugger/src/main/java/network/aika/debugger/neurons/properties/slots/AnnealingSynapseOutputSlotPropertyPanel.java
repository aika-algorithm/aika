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
package network.aika.debugger.neurons.properties.slots;

import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.slots.AnnealingSynapseOutputSlot;


/**
 * @author Lukas Molzberger
 */
public class AnnealingSynapseOutputSlotPropertyPanel extends ConjunctiveSynapseSlotPropertyPanel<AnnealingSynapseOutputSlot> {

    public AnnealingSynapseOutputSlotPropertyPanel(AnnealingSynapseOutputSlot s) {
        super(s);
    }

    @Override
    public void initSlotProperties(AnnealingSynapseOutputSlot s) {
        super.initSlotProperties(s);

        addConstant("Annealing-Type: ", "" + s.getAnnealingType());
        addField(
                ((ConjunctiveActivation)s.getActivation()).getAnnealingValue(s.getAnnealingType())
        );
    }

    public static AnnealingSynapseOutputSlotPropertyPanel create(AnnealingSynapseOutputSlot s) {
        return new AnnealingSynapseOutputSlotPropertyPanel(s);
    }
}
