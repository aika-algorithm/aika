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
package network.aika.elements.links;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.synapses.FeedbackSynapse;
import network.aika.fields.Field;
import network.aika.visitor.Visitor;
import network.aika.visitor.binding.BindingVisitor;
import network.aika.visitor.pattern.PatternCategoryVisitor;
import network.aika.visitor.pattern.PatternVisitor;

import static network.aika.fields.FieldLink.linkAndConnect;

/**
 * @author Lukas Molzberger
 *
 */
public abstract class FeedbackLink<S extends FeedbackSynapse, IA extends Activation<?>> extends BindingNeuronLink<S, IA> {

    protected long visited;

    public FeedbackLink(S s, IA input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    protected void initWeightInput() {
        super.initWeightInput();

        linkAndConnect(getSynapse().getSynapseBias(), getOutputNetForBias());
    }

    @Override
    public Field getOutputNetForBias() {
        return getOutput().getNet();
    }

    @Override
    public Field getOutputNetForWeight() {
        return getOutput().getNet();
    }

    @Override
    public void propagateRanges() {
    }

    @Override
    public void bindingVisit(BindingVisitor v, int depth) {
        if(checkVisited(v))
            return;

        super.bindingVisit(v, depth);
    }

    @Override
    public void patternVisit(PatternVisitor v, int depth) {
        if(checkVisited(v))
            return;

        super.patternVisit(v, depth);
    }

    @Override
    public void patternCatVisit(PatternCategoryVisitor v, int depth) {
    }

    private boolean checkVisited(Visitor v) {
        if(visited == v.getV())
            return true;
        visited = v.getV();
        return false;
    }
}
