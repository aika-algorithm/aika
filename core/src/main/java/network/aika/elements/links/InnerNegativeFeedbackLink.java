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
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.synapses.InnerNegativeFeedbackSynapse;
import network.aika.fields.Field;
import network.aika.fields.FieldLink;
import network.aika.fields.InnerMaxField;
import network.aika.visitor.binding.BindingVisitor;
import network.aika.visitor.inhibitory.InhibitoryVisitor;

import static network.aika.fields.InnerMaxField.getBindingActivation;
import static network.aika.fields.InnerMaxField.updateConnected;

/**
 * @author Lukas Molzberger
 */
public class InnerNegativeFeedbackLink extends FeedbackLink<InnerNegativeFeedbackSynapse, InnerInhibitoryActivation> {


    public InnerNegativeFeedbackLink(InnerNegativeFeedbackSynapse s, InnerInhibitoryActivation input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    protected void connectInputValue() {
        FieldLink fl = FieldLink.link(input.getValue(), 0, inputValue);
        InnerMaxField inhibNet = (InnerMaxField) input.getNet();
        BindingActivation selectBindingAct = getBindingActivation(inhibNet.getSelectedInput());

        updateConnected(fl, selectBindingAct, output, true);
    }

    @Override
    public void bindingVisit(BindingVisitor v, int depth) {
        // don't allow negative feedback links to create new links; i.d. do nothing
    }

    @Override
    public void inhibVisit(InhibitoryVisitor v, int depth) {
    }
}
