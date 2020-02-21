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
package network.aika.neuron.excitatory.patternpart;

import network.aika.Config;
import network.aika.Document;
import network.aika.Model;
import network.aika.neuron.Neuron;
import network.aika.neuron.Sign;
import network.aika.neuron.Synapse;
import network.aika.neuron.TNeuron;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;
import network.aika.neuron.excitatory.ExcitatoryNeuron;
import network.aika.neuron.excitatory.pattern.PatternNeuron;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lukas Molzberger
 */
public class PatternPartNeuron extends ExcitatoryNeuron<PatternPartSynapse> {
    private static final Logger log = LoggerFactory.getLogger(ExcitatoryNeuron.class);

    public static byte type;

    public PatternPartNeuron(Neuron p) {
        super(p);
    }

    public PatternPartNeuron(Model model, String label) {
        super(model, label);
    }

    @Override
    public byte getType() {
        return type;
    }

    public boolean isMature(Config c) {
        return binaryFrequency >= c.getMaturityThreshold();  // Sign.NEG, Sign.POS
    }

    /*
    public double getInformationGain(Sign si, Sign so) {
        TNeuron primaryInput = getPrimaryInput().getInput();
        TNeuron patternInput = getPatternInput().getInput();

        double fz = primaryInput.frequency;
        double Nz = primaryInput.N;
        double fy = patternInput.frequency;
        double Ny = patternInput.N;

        double pXi = fz / Nz;
        pXi = si == Sign.POS ? pXi : 1.0 - pXi;

        double pXo = fy / Ny;
        pXo = so == Sign.POS ? pXo : 1.0 - pXo;

        double f;
        if(si == Sign.POS) {
            if(so == Sign.POS) {
                f = frequency;
            } else {
                f = fz - frequency;
            }
        } else {
            if(so == Sign.POS) {
                f = fy - frequency;
            } else {
                f = N + frequency - (fz + fy);
            }
        }
        double pXio = f / N;

        return Math.log(pXio) - (Math.log(pXi) + Math.log(pXo));
    }
    */

    public double propagateRangeCoverage(Activation iAct) {
        return getPrimaryInput() == iAct.getINeuron() ? iAct.rangeCoverage : 0.0;
    }

    public double getCost(Sign s) {
        TNeuron primaryInput = getPrimaryInput();
        TNeuron patternInput = getPatternInput();

        double fz = primaryInput.frequency;
        double Nz = primaryInput.getN();
        double fy = patternInput.frequency;
        double Ny = patternInput.getN();

        double pXi = fz / Nz;
        double pXo = fy / Ny;

        double pXio = frequency / getN();
        double pXioIndep = pXi * pXo;

        if (s == Sign.POS) {
            return Math.log(pXio) - Math.log(pXioIndep);
        } else {
            return Math.log(1.0 - pXio) - Math.log(1.0 - pXioIndep);
        }
    }

    private Synapse<TNeuron, TNeuron> getPatternSynapse(boolean isRecurrent) {
        return inputSynapses
                .values()
                .stream()
                .filter(s -> isRecurrent == s.isRecurrent())
                .filter(s -> s.getInput() instanceof PatternNeuron)
                .findFirst()
                .orElse(null);
    }

    public TNeuron getPrimaryInput() {
        return getPatternSynapse(false).getInput();
    }

    public TNeuron getPatternInput() {
        return getPatternSynapse(true).getInput();
    }

    public Activation init(Activation iAct) {
        Document doc = iAct.getDocument();

        setBias(2.0);

        int actBegin = 0; // iAct.getSlot(BEGIN).getFinalPosition();

        PatternPartSynapse s = new PatternPartSynapse(iAct.getNeuron(), getProvider());

        s.updateDelta(doc, 2.0);

        s.link();

        if (log.isDebugEnabled()) {
            log.debug("    Created Synapse: " + s.getInput().getId() + ":" + s.getInput().getLabel() + " -> " + s.getOutput().getId() + ":" + s.getOutput().getLabel());
        }

        Activation targetAct = new Activation(doc, this, null, 0);

        Link l = new Link(s, iAct, targetAct);
        targetAct.addLink(l, false);

        return targetAct;
    }

    protected void createCandidateSynapse(Config c, Activation iAct, Activation targetAct) {
        Neuron targetNeuron = targetAct.getNeuron();
        Neuron inputNeuron = iAct.getNeuron();

        if (!((TNeuron) inputNeuron.get()).isMature(c)) {
            return;
        }

        PatternPartSynapse s = new PatternPartSynapse(inputNeuron, targetNeuron);

        s.link();

        if (log.isDebugEnabled()) {
            log.debug("    Created Synapse: " + s.getInput().getId() + ":" + s.getInput().getLabel() + " -> " + s.getOutput().getId() + ":" + s.getOutput().getLabel());
        }

        Link l = new Link(s, iAct, targetAct);

        targetAct.addLink(l, false);
    }
}