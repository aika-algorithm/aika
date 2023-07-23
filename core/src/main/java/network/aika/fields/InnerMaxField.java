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
import network.aika.elements.links.InnerNegativeFeedbackLink;
import network.aika.enums.Scope;

import static network.aika.elements.activations.BindingActivation.isSelfRef;

/**
 * @author Lukas Molzberger
 */
public class InnerMaxField extends MaxField {

    public InnerMaxField(FieldObject ref, String label) {
        super(ref, label);
    }

    protected void onSelectionChanged(FieldLink lastSelectedInput, FieldLink selectedInput) {
        BindingActivation lAct = lastSelectedInput != null ?
                (BindingActivation) lastSelectedInput.getInput().getReference() :
                null;
        BindingActivation cAct = (BindingActivation) selectedInput.getInput().getReference();

        getReceivers().stream()
                .filter(FieldLink.class::isInstance)
                .map(FieldLink.class::cast)
                .forEach(fl -> {
                    InnerNegativeFeedbackLink l = (InnerNegativeFeedbackLink) fl.getOutput().getReference();
                    BindingActivation act = l.getOutput();

                    updateConnected(fl, lAct, act, false);
                    updateConnected(fl, cAct, act, true);
        });
    }

    private void updateConnected(FieldLink fl, BindingActivation aAct, BindingActivation bAct, boolean current) {
        if(fl.isConnected() != current && isSelfRef(aAct, bAct, Scope.SAME)) {
            fl.updateConnected(current, true);
        }
    }
}
