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

import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.enums.Scope;
import network.aika.elements.neurons.*;
import network.aika.elements.synapses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.meta.AbstractTemplateModel.POS_MARGIN;

/**
 *
 * @author Lukas Molzberger
 */
public class NetworkMotivs {
    private static final Logger log = LoggerFactory.getLogger(NetworkMotivs.class);


    public static BindingNeuron addBindingNeuron(PatternNeuron input, String label, double weight, double inputNetTarget, double netTarget) {
        BindingNeuron bn = new BindingNeuron()
                .init(input.getModel(), label);

        double inputValueTarget = input.getActivationFunction()
                .f(inputNetTarget);

        new InputPatternSynapse()
                .setWeight(weight)
                .init(input, bn)
                .adjustBias(inputValueTarget);

        bn.setBias(netTarget);
        return bn;
    }

    public static void addOuterInhibitoryLoop(BindingNeuron bn, OuterInhibitoryNeuron in, double weight) {
        new OuterInhibitorySynapse(Scope.INPUT)
                .setWeight(1.0)
                .init(bn, in);

        new OuterNegativeFeedbackSynapse()
                .setWeight(weight)
                .init(in, bn)
                .adjustBias();
    }

    public static void addInnerInhibitoryLoop(BindingNeuron bn, InnerInhibitoryNeuron in, double weight) {
        new InnerInhibitorySynapse(Scope.INPUT)
                .setWeight(1.0)
                .init(bn, in);

        new InnerNegativeFeedbackSynapse()
                .setWeight(weight)
                .init(in, bn)
                .adjustBias();
    }

    public static void addPositiveFeedbackLoop(
            BindingNeuron bn,
            PatternNeuron pn,
            double weight,
            double patternNetTarget,
            double bindingNetTarget,
            double weakInputMargin,
            boolean isOptional
    ) {
        double valueTarget = bn.getActivationFunction()
                .f(bindingNetTarget);

        PatternSynapse pSyn = new PatternSynapse()
                .setWeight(weight)
                .setOptional(isOptional)
                .init(bn, pn)
                .adjustBias(valueTarget + weakInputMargin);

        log.info("  " + pSyn + " targetNetContr:" + -pSyn.getSynapseBias().getValue());

        double patternValueTarget = pn.getActivationFunction()
                .f(patternNetTarget);

        PositiveFeedbackSynapse posFeedSyn = new PositiveFeedbackSynapse()
                .setWeight(POS_MARGIN * (bindingNetTarget / patternValueTarget))
                .init(pn, bn)
                .adjustBias(patternValueTarget);

        log.info("  " + posFeedSyn + " targetNetContr:" + -posFeedSyn.getSynapseBias().getValue());
    }

    public static void addRelation(
            BindingNeuron lastBN,
            BindingNeuron bn,
            LatentRelationNeuron rel,
            double relWeight,
            double spsWeight
    ) {
        new RelationInputSynapse()
                .setWeight(relWeight)
                .init(rel, bn)
                .adjustBias();

        double prevNetTarget = lastBN.getBias().getValue();
        double prevValueTarget = lastBN.getActivationFunction()
                .f(prevNetTarget);

        SamePatternSynapse spSyn = new SamePatternSynapse()
                .setWeight(spsWeight)
                .init(lastBN, bn)
                .adjustBias(prevValueTarget);

        log.info("  " + spSyn + " targetNetContr:" + -spSyn.getSynapseBias().getValue());
    }
}
