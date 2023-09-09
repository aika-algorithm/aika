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
import network.aika.Model;
import network.aika.enums.Scope;
import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.OuterInhibitoryActivation;
import network.aika.elements.synapses.*;
import network.aika.visitor.operator.LinkingOperator;
import network.aika.visitor.inhibitory.InhibitoryVisitor;

/**
 *
 * @author Lukas Molzberger
 */
public class OuterInhibitoryNeuron extends DisjunctiveNeuron<OuterInhibitoryNeuron, OuterInhibitoryActivation> {

    public OuterInhibitoryNeuron(Model m) {
        super(m);
    }

    @Override
    public void startVisitor(LinkingOperator c, Activation act, Synapse targetSyn) {
        new InhibitoryVisitor(act.getThought(), c, Scope.INPUT)
                .start(act);
    }

    @Override
    public OuterInhibitoryNeuron instantiateTemplate() {
        OuterInhibitoryNeuron n = new OuterInhibitoryNeuron(getModel());
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
        return new OuterInhibitoryCategorySynapse();
    }

    @Override
    public OuterInhibitoryCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(OuterInhibitoryCategoryInputSynapse.class);
    }

    @Override
    public OuterInhibitoryCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(OuterInhibitoryCategorySynapse.class);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }
}
