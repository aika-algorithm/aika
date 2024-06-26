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
package network.aika.meta;

import network.aika.Model;
import network.aika.elements.Type;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.InhibitoryNeuron;
import network.aika.elements.neurons.types.LatentRelationNeuron;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.relations.LatentProxyRelation;
import network.aika.elements.relations.NearRelation;
import network.aika.elements.synapses.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.elements.neurons.RefType.TEMPLATE_MODEL;
import static network.aika.meta.sequences.SequenceModel.POS_MARGIN;

/**
 *
 * @author Lukas Molzberger
 */
public class NetworkMotifs {
    private static final Logger LOG = LoggerFactory.getLogger(NetworkMotifs.class);

    public static double DEFAULT_BINDING_INPUT_CATEGORY_SYNAPSE_WEIGHT = 5.0;
    public static double DEFAULT_PATTERN_INPUT_CATEGORY_SYNAPSE_WEIGHT = 5.0;
    public static double DEFAULT_INHIBITORY_INPUT_CATEGORY_SYNAPSE_WEIGHT = 1.0;

    public static double getDefaultInputCategorySynapseWeight(Type type) {
        return switch (type) {
            case BINDING -> DEFAULT_BINDING_INPUT_CATEGORY_SYNAPSE_WEIGHT;
            case PATTERN -> DEFAULT_PATTERN_INPUT_CATEGORY_SYNAPSE_WEIGHT;
            case INHIBITORY -> DEFAULT_INHIBITORY_INPUT_CATEGORY_SYNAPSE_WEIGHT;
        };
    }

    public static BindingNeuron addBindingNeuron(PatternNeuron input, String label, double weight, double netTarget, boolean isPrimary) {
        BindingNeuron bn = new BindingNeuron(input.getModel(), TEMPLATE_MODEL)
                .setLabel(label)
                .setPersistent(true);

        new InputObjectSynapse()
                .setWeight(weight)
                .link(input, bn)
                .setPropagable(isPrimary)
                .adjustBias();

        bn.setBias(netTarget);
        bn.setTargetNet(netTarget);

        return bn;
    }

    public static BindingNeuron addBindingNeuron(Model m, String label, double netTarget) {
        BindingNeuron bn = new BindingNeuron(m, TEMPLATE_MODEL)
                .setLabel(label)
                .setPersistent(true);

        bn.setBias(netTarget);
        bn.setTargetNet(netTarget);

        return bn;
    }

    public static InputObjectSynapse addInputObjectSynapse(PatternNeuron input, BindingNeuron bn, double weight, boolean propagable) {
        return new InputObjectSynapse()
                .setWeight(weight)
                .link(input, bn)
                .setPropagable(propagable)
                .adjustBias();
    }

    public static OuterPositiveFeedbackSynapse addOuterPositiveFeedbackLoop(
            PatternNeuron pn,
            BindingNeuron bn,
            PatternNeuron feedbackPN,
            double psWeight
    ) {
        addPositiveFeedbackLoop(
                bn,
                pn,
                psWeight,
                0.0,
                false,
                false
        ).setPropagable(true);

        return new OuterPositiveFeedbackSynapse()
                .setWeight(getPositiveFeedbackWeight(bn.getTargetNet(), bn.getTargetValue()))
                .link(feedbackPN, bn)
                .adjustBias();
    }

    public static void addInhibitoryLoop(BindingNeuron bn, InhibitoryNeuron in, double weight) {
        new InhibitorySynapse()
                .setWeight(1.0)
                .link(bn, in);

        new NegativeFeedbackSynapse()
                .setWeight(weight)
                .link(in, bn)
                .adjustBias();
    }

    public static InnerPositiveFeedbackSynapse addPositiveFeedbackLoop(
            BindingNeuron bn,
            PatternNeuron pn,
            double weight,
            double weakInputMargin,
            boolean allowRelaxedMatching,
            boolean isOptional
    ) {
        PatternSynapse pSyn = new PatternSynapse()
                .setWeight(weight)
                .setOptional(isOptional)
                .link(bn, pn)
                .setPropagable(true)
                .adjustBias(bn.getTargetValue() + weakInputMargin);

        LOG.info("  " + pSyn + " targetNetContr:" + -pSyn.getSynapseBias().getValue());

        InnerPositiveFeedbackSynapse posFeedSyn = new InnerPositiveFeedbackSynapse()
                .setWeight(getPositiveFeedbackWeight(bn.getTargetNet(), pn.getTargetValue()))
                .setRelation(
                        allowRelaxedMatching ?
                                new NearRelation(6) :
                                null
                )
                .link(pn, bn)
                .adjustBias();

        LOG.info("  " + posFeedSyn + " targetNetContr:" + -posFeedSyn.getSynapseBias().getValue());

        return posFeedSyn;
    }

    public static double getPosFeedbackMargin(double bindingNetTarget, double patternValueTarget) {
        return getPositiveFeedbackWeight(bindingNetTarget, patternValueTarget) - getPositiveFeedbackWeight(bindingNetTarget, 1.0);
    }

    public static double getPositiveFeedbackWeight(double bindingNetTarget, double patternValueTarget) {
        return POS_MARGIN * (bindingNetTarget / patternValueTarget);
    }

    public static void addRelation(
            BindingNeuron lastBN,
            BindingNeuron bn,
            LatentRelationNeuron relN,
            double relWeight,
            double spsWeight
    ) {
        RelationInputSynapse relSyn = new RelationInputSynapse()
                .setWeight(relWeight)
                .link(relN, bn)
                .adjustBias();

        double prevNetTarget = lastBN.getBias().getValue();
        double prevValueTarget = lastBN.getActivationFunction()
                .f(prevNetTarget);

        LatentProxyRelation rel = new LatentProxyRelation();
        SameObjectSynapse spSyn = new SameObjectSynapse()
                .setWeight(spsWeight)
                .setRelation(rel)
                .link(lastBN, bn)
                .setPropagable(true)
                .adjustBias(prevValueTarget);

        rel.linkRelation(spSyn, relSyn);

        LOG.info("  " + spSyn + " targetNetContr:" + -spSyn.getSynapseBias().getValue());
    }
}
