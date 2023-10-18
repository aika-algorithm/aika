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

import network.aika.elements.neurons.LatentRelationNeuron;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.relations.NearRelation;
import network.aika.elements.synapses.*;
import network.aika.elements.synapses.innerinhibitoryloop.InnerInhibitorySynapse;
import network.aika.elements.synapses.innerinhibitoryloop.InnerNegativeFeedbackSynapse;
import network.aika.elements.synapses.outerinhibitoryloop.OuterInhibitorySynapse;
import network.aika.elements.synapses.outerinhibitoryloop.OuterNegativeFeedbackSynapse;
import network.aika.elements.synapses.positivefeedbackloop.InnerPositiveFeedbackSynapse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.meta.sequences.SequenceModel.POS_MARGIN;

/**
 *
 * @author Lukas Molzberger
 */
public class NetworkMotifs {
    private static final Logger log = LoggerFactory.getLogger(NetworkMotifs.class);

    public static double PASSIVE_SYNAPSE_WEIGHT = 0.0;

    public static double DEFAULT_INPUT_CATEGORY_SYNAPSE_WEIGHT = 5.0;


    public static double SAME_OBJECT_MARGIN = 0.15;

    public static BindingNeuron addBindingNeuron(PatternNeuron input, String label, double weight, double netTarget) {
        BindingNeuron bn = new BindingNeuron(input.getModel())
                .setLabel(label)
                .setPersistent(true);

        new InputObjectSynapse()
                .setWeight(weight)
                .link(input, bn)
                .adjustBias();

        bn.setBias(netTarget);
        bn.setTargetNet(netTarget);

        return bn;
    }

    public static void addOuterInhibitoryLoop(BindingNeuron bn, OuterInhibitoryNeuron in, double weight) {
        new OuterInhibitorySynapse()
                .setWeight(1.0)
                .link(bn, in);

        new OuterNegativeFeedbackSynapse()
                .setWeight(weight)
                .link(in, bn)
                .adjustBias();
    }

    public static double getMaxBindingNetTarget(double bindingNetTarget, double patternValueTarget) {
        return bindingNetTarget +
                getPosFeedbackMargin(bindingNetTarget, patternValueTarget) +
                SAME_OBJECT_MARGIN;
    }

    public static void addInnerInhibitoryLoop(BindingNeuron bn, InnerInhibitoryNeuron in, double weight) {
        new InnerInhibitorySynapse(
                new NearRelation(5)
        )
                .setWeight(1.0)
                .link(bn, in);

        new InnerNegativeFeedbackSynapse()
                .setWeight(-weight)
                .link(in, bn)
                .setSynapseBias(weight);
    }

    public static void addPositiveFeedbackLoop(
            BindingNeuron bn,
            PatternNeuron pn,
            double weight,
            double weakInputMargin,
            boolean allowRelaxedMatching,
            boolean isOptional
    ) {
        addPositiveFeedbackLoop(bn, pn, weight, weakInputMargin, allowRelaxedMatching, isOptional, false);
    }

    public static void addPositiveFeedbackLoop(
            BindingNeuron bn,
            PatternNeuron pn,
            double weight,
            double weakInputMargin,
            boolean allowRelaxedMatching,
            boolean isOptional,
            boolean isTemplateOnly
    ) {
        PatternSynapse pSyn = new PatternSynapse()
                .setWeight(weight)
                .setOptional(isOptional)
                .link(bn, pn)
                .setTemplateOnly(isTemplateOnly)
                .adjustBias(bn.getTargetValue() + weakInputMargin);

        log.info("  " + pSyn + " targetNetContr:" + -pSyn.getSynapseBias().getValue());

        InnerPositiveFeedbackSynapse posFeedSyn = new InnerPositiveFeedbackSynapse(
                        allowRelaxedMatching ?
                                new NearRelation(5) :
                                null
                )
                .setWeight(getPositiveFeedbackWeight(bn.getTargetNet(), pn.getTargetValue()))
                .link(pn, bn)
                .setTemplateOnly(isTemplateOnly)
                .adjustBias();

        log.info("  " + posFeedSyn + " targetNetContr:" + -posFeedSyn.getSynapseBias().getValue());
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
            LatentRelationNeuron rel,
            double relWeight,
            double spsWeight,
            boolean templateOnly
    ) {
        RelationInputSynapse relSyn = new RelationInputSynapse()
                .setWeight(relWeight)
                .setTemplateOnly(templateOnly)
                .link(rel, bn)
                .adjustBias();

        double prevNetTarget = lastBN.getBias().getValue();
        double prevValueTarget = lastBN.getActivationFunction()
                .f(prevNetTarget);

        SameObjectSynapse spSyn = new SameObjectSynapse()
                .setWeight(spsWeight)
                .setTemplateOnly(templateOnly)
                .link(lastBN, bn)
                .adjustBias(prevValueTarget);

        spSyn.setRelationSynId(relSyn.getSynapseId());

        log.info("  " + spSyn + " targetNetContr:" + -spSyn.getSynapseBias().getValue());
    }
}
