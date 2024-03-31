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
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.activations.State;
import network.aika.elements.links.Link;
import network.aika.elements.links.types.InnerPositiveFeedbackLink;
import network.aika.elements.links.types.InputObjectLink;
import network.aika.elements.synapses.slots.SynapseSlot;
import network.aika.elements.synapses.types.InnerPositiveFeedbackSynapse;
import network.aika.elements.synapses.types.RelationInputSynapse;
import network.aika.fields.*;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.queue.steps.Anneal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static network.aika.elements.activations.StateType.*;
import static network.aika.enums.Scope.SAME;
import static network.aika.fields.link.FieldLink.linkAndConnect;
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
    protected void initNet() {
        outerFeedbackAnnealingValue = new InputField(this, "annealing value", 0.0);

        super.initNet();

        Anneal.add(this);
    }

    @Override
    public Field getValue() {
        return getValue(INNER_FEEDBACK);
    }

    public PatternActivation getSamePatternActivation() {
        return getInputLinksByType(InnerPositiveFeedbackLink.class)
                .filter(Link::isActive)
                .map(Link::getInput)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public PatternActivation getInputPatternActivation() {
        return getInputLinksByType(InputObjectLink.class)
                .map(Link::getInput)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public void createLatentRelation(int relationSynId, Activation fromOriginAct, Activation toOriginAct) {
        RelationInputSynapse ris = (RelationInputSynapse) getNeuronProvider().getSynapseBySynId(relationSynId);
        if(ris.linkExists(this, true))
            return;

        LatentRelationActivation latentRelAct = ris.createOrLookupLatentActivation(
                fromOriginAct,
                toOriginAct
        );

        ris.createAndInitLink(latentRelAct, this);
    }

    @Override
    public boolean isActiveTemplateInstance() {
        return isNewInstance || (
                isFired(INNER_FEEDBACK) && getBindingSignalSlot(SAME).isSet()
        );
    }

    @Override
    protected void connectWeightUpdate() {
        updateValue = new SumField(this, "updateValue", TOLERANCE);

        super.connectWeightUpdate();
    }

    public void updateBias(double u) {
        getNet(PRE_FEEDBACK).receiveUpdate(null, u);
    }

    @Override
    public boolean checkPrimary() {
        return !getNeuron().isPrimary() || !isPosFeedbackSlotBlocked();
    }

    private boolean isPosFeedbackSlotBlocked() {
        SynapseSlot<InnerPositiveFeedbackSynapse, InnerPositiveFeedbackLink> slot = getInputSlotBySynapseType(InnerPositiveFeedbackSynapse.class);

        if(slot == null)
            return false;

        InnerPositiveFeedbackLink l = slot.getSelectedLink();
        return l == null &&
                slot.getLinks().allMatch(il ->
                        !il.isInputSideActive()
                );
    }
}
