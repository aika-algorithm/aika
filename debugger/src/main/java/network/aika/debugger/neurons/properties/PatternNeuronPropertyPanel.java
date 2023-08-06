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
package network.aika.debugger.neurons.properties;

import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.text.Range;
import network.aika.elements.neurons.TokenNeuron;
import network.aika.enums.sign.Sign;

import static network.aika.debugger.AbstractConsole.NOT_SET_STR;
import static network.aika.enums.sign.Sign.NEG;
import static network.aika.enums.sign.Sign.POS;
import static network.aika.utils.Utils.doubleToString;


/**
 * @author Lukas Molzberger
 */
public class PatternNeuronPropertyPanel<E extends PatternNeuron> extends ConjunctiveNeuronPropertyPanel<E> {


    public PatternNeuronPropertyPanel(E n, Activation ref) {
        super(n, ref);

        Range range = ref != null ? ref.getAbsoluteCharRange() : null;
        addConstant("Range: ", range != null ? "" + range : NOT_SET_STR);
        addConstant("Frequency: ", "" + doubleToString(n.getFrequency()));
        addConstant("SampleSpace: ", "" + n.getSampleSpace().toString(range));
        addConstant("P(POS): ", probabilityToString(POS, n, range));
        addConstant("P(NEG): ", probabilityToString(NEG, n, range));
        addConstant("Surprisal(POS): ", surprisalToString(POS, n, range));
        addConstant("Surprisal(NEG): ", surprisalToString(NEG, n, range));
    }

    public static PatternNeuronPropertyPanel create(PatternNeuron n, Activation ref) {
        if(n instanceof TokenNeuron) {
            return TokenNeuronPropertyPanel.create((TokenNeuron) n, ref);
        }

        return new PatternNeuronPropertyPanel(n, ref);
    }

    private String probabilityToString(Sign s, PatternNeuron n, Range range) {
        double N = n.getSampleSpace().getN(range);
        if(N == 0.0)
            return NOT_SET_STR;

        try {
            return doubleToString(n.getProbability(s, N, false), "#.########");
        } catch(IllegalStateException e) {
            return NOT_SET_STR;
        }
    }

    private String surprisalToString(Sign s, PatternNeuron n, Range range) {
        double N = n.getSampleSpace().getN(range);
        if(N == 0.0)
            return NOT_SET_STR;

        try {
            return "" + doubleToString(n.getSurprisal(s, range, false));
        } catch(IllegalStateException e) {
            return NOT_SET_STR;
        }
    }
}
