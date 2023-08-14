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
package network.aika.fields;

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.InnerInhibitoryLink;
import network.aika.elements.links.InnerNegativeFeedbackLink;
import network.aika.enums.Scope;

import java.util.Comparator;

import static network.aika.elements.activations.BindingActivation.isSelfRef;

/**
 * @author Lukas Molzberger
 */
public class InnerMaxField extends MaxField {

    private FieldLink selectedInput;

    public InnerMaxField(FieldObject ref, String label) {
        super(ref, label);
    }

    public FieldLink getSelectedInput() {
        return selectedInput;
    }

    @Override
    public void receiveUpdate(FieldLink fl, boolean nextRound, double u) {
        if(interceptor != null) {
            interceptor.receiveUpdate(fl, nextRound, u);
            return;
        }

        super.receiveUpdate(fl, nextRound, u);
    }

    public void triggerUpdate(boolean nextRound, double u) {
        FieldLink lastSelectedInput = selectedInput;

        updatedSelectedInput(lastSelectedInput);

        super.triggerUpdate(nextRound, u);
    }

    private void updatedSelectedInput(FieldLink lastSelectedInput) {
        selectedInput = getInputs().stream()
                .max(Comparator.comparingDouble(AbstractFieldLink::getInputValue))
                .orElse(null);

        if(lastSelectedInput != selectedInput)
            onSelectionChanged(lastSelectedInput, selectedInput);
    }

    protected void onSelectionChanged(FieldLink lastSelectedInput, FieldLink selectedInput) {
        BindingActivation lAct = getBindingActivation(lastSelectedInput);
        BindingActivation cAct = getBindingActivation(selectedInput);

        InnerInhibitoryActivation inhibAct = (InnerInhibitoryActivation) getReference();
        inhibAct.getOutputLinksByType(InnerNegativeFeedbackLink.class)
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
        if(!isSelfRef(aAct, bAct, Scope.INPUT))
            fl.updateConnected(current, true);
    }
}
