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

import network.aika.enums.direction.Direction;
import network.aika.elements.activations.*;
import network.aika.elements.links.Link;
import network.aika.elements.links.positivefeedbackloop.InnerPositiveFeedbackLink;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.elements.synapses.PatternSynapse;
import network.aika.elements.synapses.RelationInputSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.Document;

import java.util.function.Predicate;

import static network.aika.enums.direction.Direction.INPUT;


/**
 * @author Lukas Molzberger
 */
public class LabelUtil {

    public static String getAbstractPatternLabel(String placeholder) {
        return "Abstract " + placeholder;
    }

    public static String getAbstractBindingNeuronLabel(String placeholder) {
        return "Abstract " + placeholder;
    }

    public static void generateTemplateInstanceLabels(Activation<?> act) {
        Document doc = (Document) act.getThought();
        String actTxt = doc.getTextSegment(act.getTextReference().getCharRange());
        if(act instanceof BindingActivation) {
            if(act.getNeuron().getLabel() == null) {
                act.getNeuron().setLabel(actTxt + " (" + extractContext(act) + ")");
            }
        } else if(act instanceof PatternActivation) {
            if(act.getNeuron().getLabel() == null) {
                act.getNeuron().setLabel(actTxt);
            }
        } else if(act instanceof OuterInhibitoryActivation) {
            if(act.getNeuron().getLabel() == null) {
                act.getNeuron().setLabel(actTxt);
            }
        } else if(act instanceof InnerInhibitoryActivation) {
            if(act.getNeuron().getLabel() == null) {
                act.getNeuron().setLabel(actTxt);
            }
        } else if(act instanceof CategoryActivation) {
            if(act.getNeuron().getLabel() == null) {
                act.getNeuron().setLabel(actTxt);
            }
        }
    }

    private static String extractContext(Activation<?> act) {
        Document doc = (Document) act.getThought();

        Activation<?> tAct = act.getTemplate();
        if(tAct == null)
            return "...";

        InnerPositiveFeedbackLink pfl = tAct.getInputLinkByType(InnerPositiveFeedbackLink.class)
                .orElse(null);

        if(pfl == null || pfl.getInput() == null)
            return "...";

        PatternActivation pAct = pfl.getInput();
        return doc.getTextSegment(pAct.getTextReference().getCharRange());
    }

    public static String generateLabel(PatternActivation pAct, boolean fired, boolean netPreAnneal) {
        PatternNeuron pn = pAct.getNeuron();
        return generateLabel(pn, bn -> {
            Link l = pAct.getInputLinks(bn)
                    .findAny()
                    .orElse(null);

            if(l == null)
                return false;

            BindingActivation act = (BindingActivation) l.getInput();
            return act != null &&
                    (!fired || act.isFired()) &&
                    (!netPreAnneal || act.getNetPreAnneal().getValue() > 0.0);
        });
    }

    public static String generateLabel(PatternNeuron pn) {
        return generateLabel(pn, bn -> {
            PatternSynapse s = (PatternSynapse) bn.getOutputSynapse(pn.getProvider());
            return (-s.getSynapseBias().getValue() > pn.getBias().getValue());
        });
    }

    public static String generateLabel(PatternNeuron pn, Predicate<BindingNeuron> test) {
        BindingNeuron[] bn = getOrderedBindingNeurons(pn);

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < bn.length; i++) {
            if (test.test(bn[i])) {
                sb.append((!first ? ", " : "") + bn[i].getLabel());
                first = false;
            }
        }
        return sb.toString();
    }

    private static BindingNeuron[] getOrderedBindingNeurons(PatternNeuron pn) {
        BindingNeuron[] bn = pn.getInputSynapsesByType(PatternSynapse.class)
                .map(Synapse::getInput)
                .toList()
                .toArray(new BindingNeuron[0]);

        for (int i = 0; i < bn.length - 1; i++) {
            for (int j = i + 1; j < bn.length; j++) {
                BindingNeuron a = bn[i];
                BindingNeuron b = bn[j];
                if ((a.getInputSynapse(b.getProvider()) != null && getDirection(b) == INPUT) ||
                        (b.getInputSynapse(a.getProvider()) != null && getDirection(a) == INPUT)) {
                    bn[i] = b;
                    bn[j] = a;
                }
            }
        }
        return bn;
    }

    private static Direction getDirection(BindingNeuron n) {
        RelationInputSynapse rs = n.getInputSynapseByType(RelationInputSynapse.class);

        if (rs == null)
            return null;

        return null;  //rs.getInput().getDirection();
    }
}
