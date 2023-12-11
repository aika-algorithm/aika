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
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.enums.sign.Sign;
import network.aika.queue.steps.Anneal;

import static network.aika.enums.Scope.SAME;
import static network.aika.fields.Fields.*;
import static network.aika.queue.Phase.INFERENCE;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternActivation extends ConjunctiveActivation<PatternNeuron> {

    protected Field feedbackValue;

    private InputField feedbackTrigger;

    protected long visited;

    private FieldFunction entropy;

    private Double[] cachedSurprisal;

    public PatternActivation(int id, Document doc, PatternNeuron patternNeuron) {
        super(id, doc, patternNeuron);
    }

    @Override
    protected void initValue() {
        super.initValue();

        NextRoundFunction nextRoundValue = new NextRoundFunction(this, "nr value");
        FieldLink.linkAndConnect(value, 0, nextRoundValue);
        nextRoundValue.setQueued(doc, INFERENCE);

        feedbackTrigger = new InputField(this, "feedback trigger", 0.0);
        feedbackValue = max(this, "feedback value", nextRoundValue, feedbackTrigger);

        Anneal.add(this, getModel().getConfig().getAnnealStart());
    }

    @Override
    public Field getFeedbackValue() {
        return feedbackValue;
    }

    public InputField getFeedbackTrigger() {
        return feedbackTrigger;
    }

    @Override
    protected void initBindingSignalSlots() {
        super.initBindingSignalSlots();

        getBindingSignalSlot(SAME)
                .connectBindingSignal(this, true);
    }

    @Override
    public boolean checkVisited(long v) {
        long lv = visited;
        visited = v;
        return visited == lv;
    }

    @Override
    public PatternActivation getBindingSignal(Scope t) {
        return this;
    }

    @Override
    public PatternActivation instantiateTemplateNode() {
        PatternActivation ti = (PatternActivation) super.instantiateTemplateNode();
        if(ti != null)
            getModel().registerLabel(ti.getNeuron(), neuron);
        return ti;
    }

    @Override
    public void connectGradientFields() {
        entropy = func(
                this,
                "entropy",
                TOLERANCE,
                net,
                x -> getSurprisal(Sign.getSign(x)),
                gradient
        );

        super.connectGradientFields();
    }

    @Override
    protected void connectWeightUpdate() {
        updateValue = scale(
                this,
                "updateValue = lr * grad * f'(net)",
                getConfig().getLearnRate(neuron.isAbstract()),
                mul(
                        this,
                        "gradient * f'(net)",
                        gradient,
                        netOuterGradient
                )
        );

        super.connectWeightUpdate();
    }

    public FieldOutput getEntropy() {
        return entropy;
    }

    public double getSurprisal(Sign sign) {
        if(cachedSurprisal == null)
            cachedSurprisal = new Double[2];

        Double s = cachedSurprisal[sign.index()];
        if(s == null) {
            s = neuron.getSurprisal(sign, getAbsoluteCharRange(), true);
            cachedSurprisal[sign.index()] = s;
        }
        return s;
    }
}
