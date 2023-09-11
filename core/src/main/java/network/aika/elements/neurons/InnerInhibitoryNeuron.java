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
import network.aika.Thought;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.synapses.*;
import network.aika.enums.Scope;
import network.aika.visitor.inhibitory.InhibitoryVisitor;
import network.aika.visitor.operator.LinkingOperator;

/**
 *
 * @author Lukas Molzberger
 */
public class InnerInhibitoryNeuron extends DisjunctiveNeuron<InnerInhibitoryNeuron, InnerInhibitoryActivation> {

    public InnerInhibitoryNeuron(Model m) {
        super(m);
    }

    @Override
    public InnerInhibitoryCategoryInputSynapse makeAbstract() {
        InhibitoryCategoryNeuron inhibCategory = new InhibitoryCategoryNeuron(getModel(), Scope.SAME)
                .setLabel(getLabel() + CATEGORY_LABEL);

        InnerInhibitoryCategoryInputSynapse s = new InnerInhibitoryCategoryInputSynapse()
                .link(inhibCategory, this);

        s.setInitialCategorySynapseWeight(1.0);

        return s;
    }

    @Override
    public void startVisitor(LinkingOperator c, Activation act, Synapse targetSyn) {
        new InhibitoryVisitor(act.getThought(), c, Scope.SAME)
                .start(act);
    }

    @Override
    public InnerInhibitoryNeuron instantiateTemplate() {
        InnerInhibitoryNeuron n = new InnerInhibitoryNeuron(getModel());
        n.initFromTemplate(this);
        return n;
    }

    @Override
    public InnerInhibitoryActivation createActivation(Thought t) {
        return new InnerInhibitoryActivation(t.createActivationId(), t, this);
    }

    public ActivationFunction getActivationFunction() {
        return ActivationFunction.LIMITED_RECTIFIED_LINEAR_UNIT;
    }

    @Override
    public CategorySynapse createCategorySynapse() {
        return new OuterInhibitoryCategorySynapse();
    }

    @Override
    public InnerInhibitoryCategoryInputSynapse getCategoryInputSynapse() {
        return getInputSynapseByType(InnerInhibitoryCategoryInputSynapse.class);
    }

    @Override
    public InnerInhibitoryCategorySynapse getCategoryOutputSynapse() {
        return getOutputSynapseByType(InnerInhibitoryCategorySynapse.class);
    }

    @Override
    public boolean isTrainingAllowed() {
        return false;
    }
}
