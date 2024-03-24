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
package network.aika.debugger.neurons.properties.synapses;

import network.aika.elements.links.Link;
import network.aika.Range;
import network.aika.elements.synapses.types.PatternSynapse;
import network.aika.enums.sign.Sign;

import static network.aika.debugger.AbstractConsole.NOT_SET_STR;
import static network.aika.enums.sign.Sign.NEG;
import static network.aika.enums.sign.Sign.POS;
import static network.aika.utils.StringUtils.doubleToString;

/**
 * @author Lukas Molzberger
 */
public class PatternSynapsePropertyPanel extends ConjunctiveSynapsePropertyPanel<PatternSynapse> {

    public PatternSynapsePropertyPanel(PatternSynapse s, Link ref) {
        super(s, ref);

        Range range = ref != null && ref.getInput() != null ? ref.getInput().getAbsoluteCharRange() : null;
        addConstant("Range: ", range != null ? "" + range : NOT_SET_STR);

        addConstant("Frequency(POS, POS): ", frequencyToString(POS, POS, s, range));
        addConstant("Frequency(POS, NEG): ", frequencyToString(POS, NEG, s, range));
        addConstant("Frequency(NEG, POS): ", frequencyToString(NEG, POS, s, range));
        addConstant("Frequency(NEG, NEG): ", frequencyToString(NEG, NEG, s, range));
        addConstant("SampleSpace: ", "" + s.getStatistic().getSampleSpace().toString(range));
        addConstant("P(POS, POS) :", probabilityToString(POS, POS, s, range));
        addConstant("P(POS, NEG) :", probabilityToString(POS, NEG, s, range));
        addConstant("P(NEG, POS) :", probabilityToString(NEG, POS, s, range));
        addConstant("P(NEG, NEG) :", probabilityToString(NEG, NEG, s, range));
        addConstant("Surprisal(POS, POS): ", surprisalToString(POS, POS, s, range));
        addConstant("Surprisal(POS, NEG): ", surprisalToString(POS, NEG, s, range));
        addConstant("Surprisal(NEG, POS): ", surprisalToString(NEG, POS, s, range));
        addConstant("Surprisal(NEG, NEG): ", surprisalToString(NEG, NEG, s, range));
    }

    private String frequencyToString(Sign is, Sign os, PatternSynapse s, Range range) {
        double N = s.getStatistic().getSampleSpace().getN(range, s.getOutput().getTemplate().getAverageCoveredSpace().getValue());
        if(N == 0.0)
            return NOT_SET_STR;

        try {
            return doubleToString(s.getStatistic().getFrequency(is, os, s.getStatistic().getSampleSpace().getN(range, s.getOutput().getTemplate().getAverageCoveredSpace().getValue())));
        } catch(IllegalStateException e) {
            return NOT_SET_STR;
        }
    }

    private String probabilityToString(Sign is, Sign os, PatternSynapse s, Range range) {
        double N = s.getStatistic().getSampleSpace().getN(range, s.getOutput().getTemplate().getAverageCoveredSpace().getValue());
        if(N == 0.0)
            return NOT_SET_STR;

        try {
            return doubleToString(s.getStatistic().getProbability(is, os, N, false), "#.########");
        } catch(IllegalStateException e) {
            return NOT_SET_STR;
        }
    }

    private String surprisalToString(Sign is, Sign os, PatternSynapse s, Range range) {
        double N = s.getStatistic().getSampleSpace().getN(range, s.getOutput().getTemplate().getAverageCoveredSpace().getValue());
        if(N == 0.0)
            return NOT_SET_STR;

        try {
            return "" + doubleToString(s.getStatistic().getSurprisal(is, os, range, false));
        } catch(IllegalStateException e) {
            return NOT_SET_STR;
        }
    }
}
