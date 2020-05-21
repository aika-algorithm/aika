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
package network.aika.templates;

import network.aika.neuron.Neuron;
import network.aika.neuron.NeuronProvider;
import network.aika.neuron.Synapse;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Link;

import static network.aika.Phase.INDUCTION;
import static network.aika.neuron.activation.Direction.OUTPUT;


public class LTargetLink<S extends Synapse> extends LLink<S> {

    private Boolean isRecurrent;
    private Boolean isNegative;
    private Boolean isPropagate;

    private double initialWeight;

    public LTargetLink() {
        super();
    }

    public LTargetLink setRecurrent(Boolean recurrent) {
        isRecurrent = recurrent;
        return this;
    }

    public LTargetLink setNegative(Boolean negative) {
        isNegative = negative;
        return this;
    }

    public LTargetLink setPropagate(Boolean propagate) {
        isPropagate = propagate;
        return this;
    }

    public LTargetLink setInitialWeight(double initialWeight) {
        this.initialWeight = initialWeight;
        return this;
    }

    @Override
    public void followBackwards(Link l) {

    }

    protected void follow(Activation act, LNode from, Activation startAct) {
        Activation iAct = selectActivation(input, act, startAct);
        Activation oAct = selectActivation(output, act, startAct);
        LNode to = getTo(from);

        if(oAct != null) {
            followClosedLoop(iAct, oAct);
        } else if(to.isOpenEnd()) {
            followOpenEnd(iAct, to, startAct);
        }
    }

    private void followClosedLoop(Activation iAct, Activation oAct) {
        Neuron<?> in = iAct.getNeuron();
        if(iAct.outputLinkExists(oAct)) {
            return;
        }

        NeuronProvider on = oAct.getNeuronProvider();
        Synapse s = in.getOutputSynapse(on, patternScope);

        if(s == null) {
            if(iAct.getThought().getPhase() != INDUCTION) return;
            s = createSynapse(in.getProvider(), on);
        }

        Link.link(s, iAct, oAct);
    }

    private void followOpenEnd(Activation iAct, LNode to, Activation startAct) {
        Activation oAct;
        Neuron<?> in = iAct.getNeuron();
        if(iAct.getThought().getPhase() != INDUCTION) {
            in.getOutputSynapses()
                    .filter(s -> checkSynapse(s))
                    .forEach(s -> {
                        Activation oa = to.follow(s.getOutput(), null, this, startAct);
                        Link.link(s, iAct, oa);
                    });
        } else {
            if(!outputLinkExists(iAct, to)) {
                oAct = to.follow(null, null, this, startAct);
                Synapse s = createSynapse(in.getProvider(), oAct.getNeuronProvider());
                Link.link(s, iAct, oAct);
            }
        }
    }

    private boolean outputLinkExists(Activation iAct, LNode to) {
        return !iAct.getLinks(OUTPUT)
                .filter(l -> checkSynapse(l.getSynapse()))
                .filter(l -> to.checkNeuron(l.getOutput().getNeuron()))
                .findAny()
                .isEmpty();
    }

    private Activation selectActivation(LNode n, Activation... acts) {
        for(Activation act : acts) {
            if(act.getLNode() == n) {
                return act;
            }
        }
        return null;
    }

    private Synapse createSynapse(NeuronProvider in, NeuronProvider on) {
        try {
            Synapse s = synapseClass.getConstructor().newInstance();

            s.setInput(in);
            s.setOutput(on);
            s.setWeight(initialWeight);
            s.setPatternScope(patternScope);
            s.setRecurrent(isRecurrent);
            s.setNegative(isNegative);
            s.setPropagate(isPropagate);

            s.link();

            System.out.println(s.toString());
            return s;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean checkSynapse(Synapse s) {
        super.checkSynapse(s);

        if(isRecurrent != null && isRecurrent.booleanValue() != s.isRecurrent()) {
            return false;
        }

        if(isNegative != null && isNegative.booleanValue() != s.isNegative()) {
            return false;
        }

        if(isPropagate != null && isPropagate.booleanValue() != s.isPropagate()) {
            return false;
        }

        return true;
    }

    @Override
    public String getTypeStr() {
        return "T";
    }
}