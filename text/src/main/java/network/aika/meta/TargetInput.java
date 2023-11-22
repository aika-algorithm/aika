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

import network.aika.Model;
import network.aika.elements.neurons.*;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.elements.neurons.types.LatentRelationNeuron;
import network.aika.Document;
import network.aika.elements.neurons.types.PatternNeuron;

import static network.aika.meta.NetworkMotifs.*;

/**
 *
 * @author Lukas Molzberger
 */
public class TargetInput {

    public static String TARGET_INPUT_LABEL = "Target Input";

    Model model;

    protected PatternNeuron targetInput;

    protected BindingNeuron targetInputBN;

    protected double targetInputNetTarget = 5.0;

    public double bindingNetTarget = 2.5;

    private String label;

    public TargetInput(Model model, String label) {
        this.model = model;
        this.label = label;
    }

    public PatternNeuron getTargetInput() {
        return targetInput;
    }

    public BindingNeuron getTargetInputBN() {
        return targetInputBN;
    }

    public void setTemplateOnly(boolean templateOnly) {
        setTemplateOnly(targetInput, templateOnly);
    }

    public static void setTemplateOnly(PatternNeuron ip, BindingNeuron bn, boolean templateOnly) {
        setTemplateOnly(ip, templateOnly);
        setTemplateOnly(bn, templateOnly);
    }

    private static void setTemplateOnly(Neuron<?, ?> n, boolean templateOnly) {
        n.setTemplateOnly(templateOnly);

        n.getInputSynapses().forEach(s ->
                s.setTemplateOnly(templateOnly)
        );
        n.getOutputSynapses().forEach(s ->
                s.setTemplateOnly(templateOnly)
        );
    }

    public PatternNeuron addTarget(String target) {
        return model.lookupInputNeuron(target, targetInput);
    }

    public void addTarget(Document doc, String target) {
        PatternNeuron n = addTarget(target);
        doc.addToken(n, null);
    }

    public void initTargetInput() {
        targetInput = new PatternNeuron(model)
                .setLabel(label + " " + TARGET_INPUT_LABEL)
                .setTargetNet(targetInputNetTarget);

        targetInput.makeAbstract()
                .setWeight(1.0)
                .adjustBias()
                .getInput();
    }

    public BindingNeuron createTargetInputBindingNeuron(BindingNeuron bn, PatternNeuron pn, LatentRelationNeuron rel) {
        BindingNeuron tibn = createTargetInputBindingNeuron(pn);

        addRelation(
                tibn,
                bn,
                rel,
                5.0,
                10.0,
                true
        );

        return tibn;
    }

    public BindingNeuron createTargetInputBindingNeuron(PatternNeuron pn) {
        BindingNeuron bn = addBindingNeuron(
                targetInput,
                "Abstr. " + label + " " + TARGET_INPUT_LABEL,
                10.0,
                bindingNetTarget
        );
        bn.makeAbstract()
                .setWeight(getDefaultInputCategorySynapseWeight(bn.getType()))
                .adjustBias();

        addPositiveFeedbackLoop(
                bn,
                pn,
                2.5,
                0.0,
                false,
                false
        );

        targetInputBN = bn;

        return bn;
    }

    public PatternNeuron instantiateTargetInput(String label) {
        PatternNeuron n = targetInput.instantiateTemplate()
                .setLabel(label + " " + TARGET_INPUT_LABEL);

        n.setAllowTraining(false);

        model.registerLabel(n, targetInput);

        return n;
    }
}
