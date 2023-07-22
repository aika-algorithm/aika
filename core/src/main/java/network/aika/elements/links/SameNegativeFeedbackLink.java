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

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.SameInhibitoryActivation;
import network.aika.elements.synapses.SameNegativeFeedbackSynapse;
import network.aika.fields.Field;
import network.aika.fields.MaxField;
import network.aika.fields.Multiplication;
import network.aika.visitor.binding.BindingVisitor;

import java.util.stream.Stream;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.mul;

/**
 * @author Lukas Molzberger
 */
public class SameNegativeFeedbackLink extends FeedbackLink<SameNegativeFeedbackSynapse, SameInhibitoryActivation> {

    private Field weightUpdate;


    public SameNegativeFeedbackLink(SameNegativeFeedbackSynapse s, SameInhibitoryActivation input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    public Field getOutputNet() {
        return getOutput().getNet();
    }

    @Override
    protected void connectInputValue() {
    }


    @Override
    public void bindingVisit(BindingVisitor v, int depth) {
        // don't allow negative feedback links to create new links; i.d. do nothing
    }

    @Override
    public void connectWeightUpdate() {
        weightUpdate = mul(
                this,
                "weight update",
                getInputIsFired(),
                getOutput().getNegUpdateValue()
        );

        linkAndConnect(
                weightUpdate,
                synapse.getWeight()
        );
    }

    @Override
    public void disconnect() {
        super.disconnect();

        if(weightUpdate != null)
            weightUpdate.disconnectAndUnlinkOutputs(false);
    }
}
