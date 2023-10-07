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
import network.aika.elements.links.outerinhibitoryloop.OuterInhibitoryCategoryInputLink;
import network.aika.elements.links.outerinhibitoryloop.OuterInhibitoryCategoryLink;
import network.aika.elements.links.outerinhibitoryloop.OuterInhibitoryLink;
import network.aika.elements.links.outerinhibitoryloop.OuterNegativeFeedbackLink;
import network.aika.elements.neurons.OuterInhibitoryNeuron;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


/**
 *
 * @author Lukas Molzberger
 */
public class OuterInhibitoryActivation extends DisjunctiveActivation<OuterInhibitoryNeuron> {

    public OuterInhibitoryActivation(int id, Thought t, OuterInhibitoryNeuron neuron) {
        super(id, t, neuron);
    }

    @Override
    public boolean isActiveTemplateInstance() {
        return true;
    }

    public Stream<OuterInhibitoryLink> getAllInhibitoryLinks() {
        return getRelatedInhibitoryActivations()
                .flatMap(OuterInhibitoryActivation::getOwnInhibitoryLinks);
    }

    public Stream<OuterInhibitoryLink> getOwnInhibitoryLinks() {
        return getInputLinksByType(OuterInhibitoryLink.class);
    }

    public Stream<OuterNegativeFeedbackLink> getAllNegativeFeedbackLinks() {
        return getRelatedInhibitoryActivations()
                .flatMap(OuterInhibitoryActivation::getOwnNegativeFeedbackLinks);
    }

    public Stream<OuterNegativeFeedbackLink> getOwnNegativeFeedbackLinks() {
        return getOutputLinksByType(OuterNegativeFeedbackLink.class);
    }

    private Stream<OuterInhibitoryActivation> getRelatedInhibitoryActivations() {
        return Stream.concat(
                Stream.of(this),
                isAbstract() ?
                        getConcreteInhibitoryActivations() :
                        getAbstractInhibitoryActivations()
        );
    }

    private Stream<OuterInhibitoryActivation> getConcreteInhibitoryActivations() {
        return getInputLinksByType(OuterInhibitoryCategoryInputLink.class)
                .map(Link::getInput)
                .flatMap(CategoryActivation::getCategoryInputs)
                .map(act -> (OuterInhibitoryActivation) act);
    }

    private Stream<OuterInhibitoryActivation> getAbstractInhibitoryActivations() {
        return getOutputLinksByType(OuterInhibitoryCategoryLink.class)
                .map(Link::getOutput)
                .map(act -> (OuterInhibitoryActivation) act.getTemplate())
                .filter(Objects::nonNull);
    }

    public static void crossConnectFields(OuterInhibitoryActivation concrInhibAct, OuterInhibitoryActivation templateInhibAct) {
        if(concrInhibAct == null || templateInhibAct == null)
            return;

        OuterInhibitoryActivation.connectFields(
                concrInhibAct.getOwnInhibitoryLinks(),
                templateInhibAct.getOwnNegativeFeedbackLinks()
        );

        OuterInhibitoryActivation.connectFields(
                templateInhibAct.getOwnInhibitoryLinks(),
                concrInhibAct.getOwnNegativeFeedbackLinks()
        );
    }

    public static void connectFields(Stream<OuterInhibitoryLink> in, Stream<OuterNegativeFeedbackLink> out) {
        List<OuterNegativeFeedbackLink> nfls = out.toList();

        in.forEach(il ->
                nfls.forEach(nfl ->
                        il.connectFields(nfl)
                )
        );
    }
}
