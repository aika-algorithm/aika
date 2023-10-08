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
package network.aika.elements.activations;

import network.aika.Thought;
import network.aika.elements.Type;
import network.aika.elements.links.innerinhibitoryloop.InnerInhibitoryLink;
import network.aika.elements.links.innerinhibitoryloop.InnerNegativeFeedbackLink;
import network.aika.elements.neurons.InnerInhibitoryNeuron;
import network.aika.enums.Scope;
import network.aika.fields.FieldLink;
import network.aika.fields.IdentityFunction;
import network.aika.fields.MaxField;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.INNER_INHIBITORY;
import static network.aika.elements.activations.BindingActivation.isSelfRef;
import static network.aika.queue.Phase.INFERENCE;


/**
 *
 * @author Lukas Molzberger
 */
public class InnerInhibitoryActivation extends DisjunctiveActivation<InnerInhibitoryNeuron> {

    public InnerInhibitoryActivation(int id, Thought t, InnerInhibitoryNeuron neuron) {
        super(id, t, neuron);
    }

    @Override
    public Type getType() {
        return INNER_INHIBITORY;
    }

    @Override
    protected void initNet() {
        net = new MaxField(this, "net", this::onSelectionChanged)
                .setQueued(thought, INFERENCE);

        initNetPreAnneal();
    }

    protected void onSelectionChanged(FieldLink lastSelectedInput, FieldLink selectedInput) {
        BindingActivation lAct = getBindingActivation(lastSelectedInput);
        BindingActivation cAct = getBindingActivation(selectedInput);

        getOutputLinksByType(InnerNegativeFeedbackLink.class)
                .forEach(l -> {
                    BindingActivation oAct = l.getOutput();
                    FieldLink fl = ((IdentityFunction)l.getInputValue()).getInputLinkByArg(0);
                    updateConnected(fl, lAct, oAct, false);
                    updateConnected(fl, cAct, oAct, true);
                });
    }

    public static BindingActivation getBindingActivation(FieldLink fl) {
        if(fl == null)
            return null;

        InnerInhibitoryLink l = (InnerInhibitoryLink) fl.getInput().getReference();
        return l.getInput();
    }

    public static void updateConnected(FieldLink fl, BindingActivation aAct, BindingActivation bAct, boolean current) {
        if(isSelfRef(aAct, bAct, Scope.INPUT))
            fl.updateConnected(current, true);
    }

    @Override
    public boolean isActiveTemplateInstance() {
        return true;
    }
}
