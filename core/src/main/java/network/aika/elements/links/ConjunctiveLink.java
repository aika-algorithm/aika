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
package network.aika.elements.links;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.fields.link.ArgumentFieldLink;
import network.aika.visitor.Visitor;

import static network.aika.enums.Scope.SAME;
import static network.aika.enums.direction.Direction.INPUT;
import static network.aika.fields.link.AbstractFieldLink.updateConnected;
import static network.aika.fields.link.ArgumentFieldLink.linkAndConnect;
import static network.aika.fields.Fields.*;
import static network.aika.visitor.operator.SubsumesOperator.subsumes;


/**
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveLink extends Link {

    protected ArgumentFieldLink<ConjunctiveLink> inputSlotFL;


    public ConjunctiveLink(ConjunctiveSynapse s, Activation input, ConjunctiveActivation output) {
        super(s, input, output);
    }

    @Override
    public void onOutputBindingSignalUpdate(Scope bsType, Activation nBS, boolean state) {
        if(input == null)
            return;

        Scope inputBSType = INPUT.transition(bsType, synapse.getTransition());
        if(inputBSType == null)
            return;

        Activation bs = input.getBindingSignal(inputBSType);
        if(bs == null)
            return;

        updateConnected(
                inputSlotFL,
                state && (
                        subsumes(SAME, nBS, bs) || subsumes(SAME, bs, nBS)
                )
        );
    }

    public boolean isInputSideActive() {
        if(input == null)
            return true;

        return synInputSlot != null && synInputSlot.getSelectedLink() == this;
    }

    public boolean isOutputSideActive() {
        return synOutputSlot != null && synOutputSlot.getSelectedLink() == this;
    }

    @Override
    public void visit(Visitor v, Scope s, int depth) {
       if(input == null)
            return;

        if(isActive() || output.getBindingSignalSlot(s).isFeedback())
            v.next(this, s, depth);
    }

    public void initFromTemplate(Link template) {
        super.initFromTemplate(template);

        synapse.initSlots(output);
    }
}
