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
import network.aika.elements.links.*;
import network.aika.elements.neurons.InhibitoryNeuron;
import network.aika.fields.AbstractFieldLink;
import network.aika.fields.MaxField;
import network.aika.fields.QueueSumField;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.add;
import static network.aika.steps.Phase.INFERENCE;


/**
 *
 * @author Lukas Molzberger
 */
public class InhibitoryActivation extends DisjunctiveActivation<InhibitoryNeuron> {


    private MaxField maxInputNet;

    public InhibitoryActivation(int id, Thought t, InhibitoryNeuron neuron) {
        super(id, t, neuron);
    }

    @Override
    protected void initNet() {
        maxInputNet = new MaxField(this, "max-input-net", (ofl, nfl) -> {
            if(ofl != null) {
                updateConnect(ofl, true);
            }
            updateConnect(nfl, false);
        });

        net = new QueueSumField(this, INFERENCE, "net", null);
        linkAndConnect(getNeuron().getBias(), maxInputNet);
        linkAndConnect(getNeuron().getBias(), net)
                .setPropagateUpdates(false);
    }

    private void updateConnect(AbstractFieldLink fl, boolean connected) {
        InhibitoryLink il = (InhibitoryLink) fl.getInput().getReference();
        BindingActivation bAct = il.getInput();
        NegativeFeedbackLink nfl = (NegativeFeedbackLink) bAct.getInputLink(neuron);
        nfl.getInputValue().getInputs().forEach(ifl -> {
            if(connected)
                ifl.connect(false);
            else
                ifl.disconnect(false);
        });
    }

    public MaxField getMaxInputNet() {
        return maxInputNet;
    }

    @Override
    public boolean isActiveTemplateInstance() {
        return true;
    }

    public Stream<InhibitoryLink> getAllInhibitoryLinks() {
        return getRelatedInhibitoryActivations()
                .flatMap(InhibitoryActivation::getOwnInhibitoryLinks);
    }

    public Stream<InhibitoryLink> getOwnInhibitoryLinks() {
        return getInputLinksByType(InhibitoryLink.class);
    }

    public Stream<NegativeFeedbackLink> getOwnNegativeFeedbackLinks() {
        return getOutputLinksByType(NegativeFeedbackLink.class);
    }

    public Stream<InhibitoryActivation> getRelatedInhibitoryActivations() {
        return Stream.concat(
                Stream.of(this),
                isAbstract() ?
                        getConcreteInhibitoryActivations() :
                        getAbstractInhibitoryActivations()
        );
    }

    private Stream<InhibitoryActivation> getConcreteInhibitoryActivations() {
        return getInputLinksByType(InhibitoryCategoryInputLink.class)
                .map(Link::getInput)
                .flatMap(CategoryActivation::getCategoryInputs)
                .map(act -> (InhibitoryActivation) act);
    }

    private Stream<InhibitoryActivation> getAbstractInhibitoryActivations() {
        return getOutputLinksByType(InhibitoryCategoryLink.class)
                .map(Link::getOutput)
                .map(act -> (InhibitoryActivation) act.getTemplate())
                .filter(Objects::nonNull);
    }

    public static void crossConnectFields(InhibitoryActivation concrInhibAct, InhibitoryActivation templateInhibAct) {
        if(concrInhibAct == null || templateInhibAct == null)
            return;
/*
        InhibitoryActivation.connectFields(
                concrInhibAct.getOwnInhibitoryLinks(),
                templateInhibAct.getOwnNegativeFeedbackLinks()
        );

        InhibitoryActivation.connectFields(
                templateInhibAct.getOwnInhibitoryLinks(),
                concrInhibAct.getOwnNegativeFeedbackLinks()
        );*/
    }
}
