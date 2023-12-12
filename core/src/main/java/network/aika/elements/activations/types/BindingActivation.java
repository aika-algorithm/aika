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
package network.aika.elements.activations.types;

import network.aika.Document;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.Link;
import network.aika.elements.links.types.InputObjectLink;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.elements.neurons.types.BindingNeuron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.enums.Scope.SAME;
import static network.aika.fields.Fields.isTrue;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class BindingActivation extends ConjunctiveActivation<BindingNeuron> {

    protected static final Logger log = LoggerFactory.getLogger(BindingActivation.class);

    public BindingActivation(int id, Document doc, BindingNeuron n) {
        super(id, doc, n);
    }

    @Override
    protected boolean isFeedback(Scope bsSlot) {
        return bsSlot == SAME;
    }

    @Override
    public boolean isActiveTemplateInstance() {
        return isNewInstance || (
                isTrue(value) && getBindingSignalSlot(SAME).isSet()
        );
    }

    public PatternActivation getInputPatternActivation() {
        return getInputLinksByType(InputObjectLink.class)
                .map(Link::getInput)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void connectWeightUpdate() {
        updateValue = new SumField(this, "updateValue", TOLERANCE);

        super.connectWeightUpdate();
    }

    public void updateBias(double u) {
        getNet(false).receiveUpdate(null, u);
    }
}
