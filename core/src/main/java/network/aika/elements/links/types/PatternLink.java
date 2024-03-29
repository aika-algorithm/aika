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
package network.aika.elements.links.types;

import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.text.Range;
import network.aika.fields.AbstractFunction;
import network.aika.elements.synapses.types.PatternSynapse;
import network.aika.fields.FieldOutput;
import network.aika.fields.SumField;
import network.aika.enums.sign.Sign;
import network.aika.queue.steps.LinkCounting;

import static network.aika.elements.activations.StateType.PRE_FEEDBACK;
import static network.aika.enums.Scope.SAME;
import static network.aika.fields.ConstantField.ZERO;
import static network.aika.fields.link.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.func;
import static network.aika.fields.Fields.scale;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class PatternLink extends ConjunctiveLink<PatternSynapse, BindingActivation, PatternActivation> {

    private AbstractFunction outputEntropy;
    private AbstractFunction informationGain;

    private Double[][] cachedSurprisal;

    public PatternLink(PatternSynapse s, BindingActivation input, PatternActivation output) {
        super(s, input, output);

        LinkCounting.add(this);
    }

    @Override
    public void init() {
        if(input != null)
            input.getBindingSignalSlot(SAME)
                    .updateBindingSignal(output, true);
    }

    @Override
    public void connectGradientFields() {
        gradient = new SumField(this, "Gradient", TOLERANCE);

        if(input != null)
            linkAndConnect(input.getGradient(), gradient);

        linkAndConnect(gradient, output.getGradient());

        informationGain = func(
                this,
                "Information-Gain",
                getInputPatternNet(),
                output.getNet(PRE_FEEDBACK),
                (x1, x2) ->
                        getSurprisal(
                                Sign.getSign(x1),
                                Sign.getSign(x2)
                        ),
                gradient
        );

        outputEntropy = scale(this, "-Entropy", -1,
                output.getEntropy(),
                gradient
        );
    }

    public double getSurprisal(Sign is, Sign os) {
        if(cachedSurprisal == null)
            cachedSurprisal = new Double[2][2];

        Double s = cachedSurprisal[is.index()][os.index()];
        if(s == null) {
            s = synapse.getSurprisal(is, os, getAbsoluteRange(), true);
            cachedSurprisal[is.index()][os.index()] = s;
        }
        return s;
    }

    private Range getAbsoluteRange() {
        Range r = (input != null ? input : output).getAbsoluteCharRange();
        return r;
    }

    public FieldOutput getInputPatternNet() {
        if(input == null)
            return ZERO;

        PatternActivation inputPatternAct = input.getInputPatternActivation();
        if(inputPatternAct == null)
            return ZERO;

        return inputPatternAct.getNet(PRE_FEEDBACK);
    }

    public FieldOutput getInputPatternValue() {
        if(input == null)
            return ZERO;

        PatternActivation inputPatternAct = input.getInputPatternActivation();
        if(inputPatternAct == null)
            return ZERO;

        return inputPatternAct.getValue();
    }

    public AbstractFunction getOutputEntropy() {
        return outputEntropy;
    }

    public AbstractFunction getInformationGain() {
        return informationGain;
    }
}
