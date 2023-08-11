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
package network.aika.elements.neurons;

import network.aika.ActivationFunction;
import network.aika.Thought;
import network.aika.elements.activations.OuterInhibitoryActivation;
import network.aika.elements.synapses.*;

/**
 *
 * @author Lukas Molzberger
 */
public class OuterInhibitoryNeuron extends DisjunctiveNeuron<OuterInhibitoryActivation> {

    @Override
    public OuterInhibitoryNeuron instantiateTemplate() {
        OuterInhibitoryNeuron n = new OuterInhibitoryNeuron();
        n.initFromTemplate(this);
        return n;
    }

    @Override
    public OuterInhibitoryActivation createActivation(Thought t) {
        return new OuterInhibitoryActivation(t.createActivationId(), t, this);
    }

    public ActivationFunction getActivationFunction() {
        return ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new InhibitoryCategorySynapse();
    }

    @Override
    public OuterInhibitoryCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(OuterInhibitoryCategoryInputSynapse.class);
    }

    @Override
    public InhibitoryCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(InhibitoryCategorySynapse.class);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }
}
