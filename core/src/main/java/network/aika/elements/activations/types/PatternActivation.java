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
import network.aika.enums.Transition;
import network.aika.fields.*;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.enums.sign.Sign;
import network.aika.queue.activation.LatentLinking;
import network.aika.queue.activation.LinkingOut;
import network.aika.queue.activation.Propagate;

import static network.aika.enums.LinkingMode.FEEDBACK;
import static network.aika.enums.LinkingMode.REGULAR;
import static network.aika.enums.Transition.SAME;
import static network.aika.fields.Fields.*;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternActivation extends ConjunctiveActivation<PatternNeuron> {

    private FieldFunction entropy;

    private Double[] cachedSurprisal;

    public PatternActivation(int id, Document doc, PatternNeuron patternNeuron) {
        super(id, doc, patternNeuron);

        LinkingOut.add(this, this, FEEDBACK);
        LatentLinking.add(this, this, FEEDBACK);
        Propagate.add(this, FEEDBACK);
    }

    @Override
    protected void initBindingSignalSlots() {
        super.initBindingSignalSlots();

        getBindingSignalSlot(SAME)
                .connectBindingSignal(this, true);
    }

    @Override
    public PatternActivation getBindingSignal(Transition t) {
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