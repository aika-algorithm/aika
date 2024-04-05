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
package network.aika.elements.synapses.types;

import network.aika.Model;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.links.types.PatternLink;
import network.aika.elements.neurons.types.PatternNeuron;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.fields.FieldOutput;
import network.aika.Range;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.enums.sign.Sign;
import network.aika.statistic.SynapseStatistic;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Collectors;

import static network.aika.elements.activations.StateType.OUTER_FEEDBACK;
import static network.aika.fields.Fields.isTrue;
import static network.aika.utils.ToleranceUtils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternSynapse extends ConjunctiveSynapse {

    SynapseStatistic statistic = new SynapseStatistic(
            this,
            "statistic",
            getConfig().getAlpha(),
            TOLERANCE
    );

    public PatternSynapse() {
    }

    public SynapseStatistic getStatistic() {
        return statistic;
    }

    @Override
    public FieldOutput getInputValue(BindingActivation input) {
        return input.getValue(OUTER_FEEDBACK);
    }

    @Override
    public void link() {
        checkAlreadyLinkedToPattern(input.getNeuron());

        super.link();
        // Pattern Synapses always need to be propagable because their inputs may depend on each other.
        getInput().updatePropagable(output, true);
    }

    @Override
    public PatternLink createLink(BindingActivation input, PatternActivation output) {
        checkAlreadyLinkedToPattern(input);

        return super.createLink(input, output);
    }

    private static void checkAlreadyLinkedToPattern(BindingActivation input) {
        if(input == null)
            return;

        if(input.getOutputLinksByType(PatternLink.class).count() > 0) {
            log.warn("Already linked to Pattern: " +
                    input.getOutputLinksByType(PatternLink.class)
                            .map(Link::getOutput)
                            .map(Activation::toString)
                            .collect(Collectors.joining(", "))
            );
        }
    }

    private static void checkAlreadyLinkedToPattern(BindingNeuron input) {
        if(input == null)
            return;

        PatternSynapse ps = input.getOutputSynapseByType(PatternSynapse.class);
        if(ps != null)
            log.warn("Already linked to Pattern: " + ps);
    }

    @Override
    public void delete() {
        super.delete();
        getInput().delete();
    }

    public void setFrequency(Sign inputSign, Sign outputSign, double f) {
        statistic.setFrequency(inputSign, outputSign, f);
        setModified();
    }

    @Override
    public void count(PatternLink l) {
        super.count(l);

        if(l.getInput() == null)
            return; // TODO: fix

        boolean inputActive = isTrue(l.getInputPatternValue());
        boolean outputActive = isTrue(l.getOutput().getValue());

        Range absoluteRange = l.getInput().getAbsoluteCharRange();

        statistic.count(absoluteRange, inputActive, outputActive);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        statistic.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
        super.readFields(in, m);

        statistic.readFields(in);
    }
}
