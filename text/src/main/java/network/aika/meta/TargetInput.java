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
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.text.Document;

import static network.aika.meta.NetworkMotifs.addBindingNeuron;
import static network.aika.meta.NetworkMotifs.addPositiveFeedbackLoop;
import static network.aika.utils.NetworkUtils.PASSIVE_SYNAPSE_WEIGHT;
import static network.aika.utils.NetworkUtils.makeAbstract;

/**
 *
 * @author Lukas Molzberger
 */
public class TargetInput {

    Model model;

    protected NeuronProvider targetInput;

    protected NeuronProvider targetInputCategory;

    protected NeuronProvider targetInputBN;

    protected double targetInputNetTarget = 5.0;

    public double bindingNetTarget = 2.5;

    private String label;

    public TargetInput(Model model, String label) {
        this.model = model;
        this.label = label;
    }

    public double getBindingValueTarget() {
        return targetInputBN.getNeuron().getActivationFunction()
                .f(bindingNetTarget);
    }

    public NeuronProvider getTargetInput() {
        return targetInput;
    }

    public void setTemplateOnly(boolean templateOnly) {
        targetInput.getNeuron().setTemplateOnly(templateOnly);
        targetInputCategory.getNeuron().setTemplateOnly(templateOnly);
        targetInputBN.getNeuron().setTemplateOnly(templateOnly);

        targetInput.getInputSynapses().forEach(s ->
                s.setTemplateOnly(templateOnly)
        );
        targetInput.getOutputSynapses().forEach(s ->
                s.setTemplateOnly(templateOnly)
        );
    }

    public PatternNeuron addTarget(String target) {
        return model.lookupInputNeuron(target, targetInput.getNeuron());
    }

    public void addTarget(Document doc, String target) {
        PatternNeuron n = addTarget(target);
        doc.addToken(n, null, null, null, targetInputNetTarget);
    }

    public void initTargetInput() {
        targetInput = new PatternNeuron(model)
                        .setLabel(label + " Target Input")
                .getProvider();

        targetInput.getNeuron()
                .setBias(targetInputNetTarget);
        targetInput.getNeuron()
                .setTargetNet(targetInputNetTarget);

        targetInputCategory = makeAbstract((PatternNeuron) targetInput.getNeuron())
                .setWeight(1.0)
                .adjustBias()
                .getPInput();
    }

    public BindingNeuron createTargetInputBindingNeuron(PatternNeuron pn, double patternNetTarget) {
        BindingNeuron bn = addBindingNeuron(
                targetInput.getNeuron(),
                "Abstr. " + label + " Target Input",
                10.0,
                bindingNetTarget
        );
        makeAbstract(bn)
                .setWeight(PASSIVE_SYNAPSE_WEIGHT);

        addPositiveFeedbackLoop(
                bn,
                pn,
                2.5,
                0.0,
                false
        );

        targetInputBN = bn.getProvider();

        return bn;
    }

    public PatternNeuron instantiateTargetInput(String label) {
        PatternNeuron ttiPN = targetInput.getNeuron();
        PatternNeuron n = ttiPN.instantiateTemplate()
                .setLabel(label + " Target Input");

        n.setAllowTraining(false);

        return n;
    }
}
