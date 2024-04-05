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

import network.aika.Document;
import network.aika.elements.links.CategoryInputLink;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.synapses.slots.AnnealingSynapseOutputSlot;
import network.aika.elements.synapses.slots.AnnealingType;
import network.aika.fields.Field;
import network.aika.fields.InputField;
import network.aika.queue.steps.FeedbackTrigger;

import static network.aika.fields.link.FieldLink.linkAndConnect;


/**
 *
 * @author Lukas Molzberger
 */
public abstract class ConjunctiveActivation extends Activation<ConjunctiveNeuron> {

    protected InputField outerFeedbackAnnealingValue;
    protected InputField instantiationAnnealingValue;

    public ConjunctiveActivation(int id, Document doc, ConjunctiveNeuron n) {
        super(id, doc, n);

        if(getConfig().isMetaInstantiationEnabled()) {
            FeedbackTrigger.add(this, true);
        }
    }

    @Override
    protected void initNet() {
        instantiationAnnealingValue = new InputField(this, "instantiation annealing value", 1.0);
        super.initNet();
    }

    public Field getAnnealingValue(AnnealingType at) {
        return switch (at) {
            case CATEGORY_INPUT -> instantiationAnnealingValue;
            case OUTER_FEEDBACK -> outerFeedbackAnnealingValue;
        };
    }

    public InputField getInstantiationAnnealingValue() {
        return instantiationAnnealingValue;
    }

    public InputField getOuterFeedbackAnnealingValue() {
        return outerFeedbackAnnealingValue;
    }

    public AnnealingSynapseOutputSlot getActiveCategoryInputSlot() {
        return getInputSlotsByType(AnnealingSynapseOutputSlot.class)
                .filter(as -> as.getAnnealingType() == AnnealingType.CATEGORY_INPUT)
                .findFirst()
                .orElse(null);
    }

    @Override
    public CategoryInputLink getActiveCategoryInputLink() {
        AnnealingSynapseOutputSlot sl = getActiveCategoryInputSlot();
        return sl != null ? (CategoryInputLink) sl.getSelectedLink() : null;
    }

    @Override
    protected void connectWeightUpdate() {
        negUpdateValue = scale(
                this,
                "-updateValue",
                -1.0,
                updateValue
        );

        linkAndConnect(
                updateValue,
                getNeuron().getBias()
        );
    }

    @Override
    protected void initBiases() {
        ((ConjunctiveNeuron)neuron).getSynapseBiasSynapses()
                .forEach(s ->
                        s.initBiasInput(this)
                );

        super.initBiases();
    }
}
