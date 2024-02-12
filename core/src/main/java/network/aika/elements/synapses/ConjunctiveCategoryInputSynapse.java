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

import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.ConjunctiveCategoryInputLink;
import network.aika.elements.neurons.CategoryNeuron;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.synapses.slots.AnnealingSynapseOutputSlot;
import network.aika.elements.synapses.slots.AnnealingType;
import network.aika.elements.synapses.slots.SynapseSlot;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveCategoryInputSynapse<
        S extends ConjunctiveCategoryInputSynapse,
        I extends CategoryNeuron,
        O extends ConjunctiveNeuron<O, OA>,
        L extends ConjunctiveCategoryInputLink<S, IA, OA>,
        IA extends CategoryActivation,
        OA extends ConjunctiveActivation<O>
        > extends PositiveFeedbackSynapse<S, I, O, L, IA, OA> implements CategoryInputSynapse<S> {

    @Override
    public SynapseSlot createOutputSlot(OA oAct) {
        return new AnnealingSynapseOutputSlot(oAct, this, AnnealingType.CATEGORY_INPUT);
    }

    @Override
    public void initBiasInput(OA act) {
        super.initBiasInput(act);

        act.registerInputSlot(this);
    }
}
