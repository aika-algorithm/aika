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
import network.aika.elements.neurons.SameInhibitoryNeuron;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


/**
 *
 * @author Lukas Molzberger
 */
public class SameInhibitoryActivation extends DisjunctiveActivation<SameInhibitoryNeuron> {

    public SameInhibitoryActivation(int id, Thought t, SameInhibitoryNeuron neuron) {
        super(id, t, neuron);
    }

    @Override
    public boolean isActiveTemplateInstance() {
        return true;
    }

    public Stream<SameInhibitoryLink> getAllInhibitoryLinks() {
        return getRelatedInhibitoryActivations()
                .flatMap(SameInhibitoryActivation::getOwnInhibitoryLinks);
    }

    public Stream<SameInhibitoryLink> getOwnInhibitoryLinks() {
        return getInputLinksByType(SameInhibitoryLink.class);
    }

    public Stream<NegativeFeedbackLink> getAllNegativeFeedbackLinks() {
        return getRelatedInhibitoryActivations()
                .flatMap(SameInhibitoryActivation::getOwnNegativeFeedbackLinks);
    }

    public Stream<NegativeFeedbackLink> getOwnNegativeFeedbackLinks() {
        return getOutputLinksByType(NegativeFeedbackLink.class);
    }

    private Stream<SameInhibitoryActivation> getRelatedInhibitoryActivations() {
        return Stream.concat(
                Stream.of(this),
                isAbstract() ?
                        getConcreteInhibitoryActivations() :
                        getAbstractInhibitoryActivations()
        );
    }

    private Stream<SameInhibitoryActivation> getConcreteInhibitoryActivations() {
        return getInputLinksByType(InhibitoryCategoryInputLink.class)
                .map(Link::getInput)
                .flatMap(CategoryActivation::getCategoryInputs)
                .map(act -> (SameInhibitoryActivation) act);
    }

    private Stream<SameInhibitoryActivation> getAbstractInhibitoryActivations() {
        return getOutputLinksByType(InhibitoryCategoryLink.class)
                .map(Link::getOutput)
                .map(act -> (SameInhibitoryActivation) act.getTemplate())
                .filter(Objects::nonNull);
    }

    public static void connectFields(Stream<SameInhibitoryLink> in, Stream<NegativeFeedbackLink> out) {
        List<NegativeFeedbackLink> nfls = out.toList();

        in.forEach(il ->
                nfls.forEach(nfl ->
                        il.connectFields(nfl)
                )
        );
    }
}
