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
package network.aika.elements.activations;

import network.aika.Thought;
import network.aika.elements.Type;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.LatentRelationNeuron;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.text.Document;
import network.aika.text.TextReference;
import network.aika.visitor.Visitor;
import network.aika.enums.sign.Sign;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.PATTERN;
import static network.aika.fields.Fields.*;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class PatternActivation extends ConjunctiveActivation<PatternNeuron> {

    private Map<LatentRelationNeuron, LatentRelationActivation> toRelations = new TreeMap<>(
            Comparator.comparingLong(Neuron::getId)
    );

    private FieldFunction entropy;

    private Double[] cachedSurprisal;

    public PatternActivation(int id, Thought t, PatternNeuron patternNeuron) {
        super(id, t, patternNeuron);
    }

    @Override
    public Type getType() {
        return PATTERN;
    }

    public Map<LatentRelationNeuron, LatentRelationActivation> getToRelations() {
        return toRelations;
    }

    @Override
    protected void registerPosRange(TextReference oldTextReference, TextReference newTextReference) {
        Document doc = (Document) thought;

        doc.updateGroundRef(
                this,
                oldTextReference,
                newTextReference
        );
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

    @Override
    public void bindingVisit(Visitor v, Link lastLink, int state, int depth) {
        super.bindingVisit(v, lastLink, state, depth);

        v.up(this, state, depth);
    }


    @Override
    public void innerInhibVisit(Visitor v, Link lastLink, int state, int depth) {
        super.innerInhibVisit(v, lastLink, state, depth);

        v.up(this, state, depth);
    }

    @Override
    public void outerInhibVisit(Visitor v, Link lastLink, int state, int depth) {
        super.outerInhibVisit(v, lastLink, state, depth);

        v.up(this, state, depth);
    }

    @Override
    public void innerSelfRefVisit(Visitor v, Link lastLink, int state, int depth) {
        super.innerSelfRefVisit(v, lastLink, state, depth);

        v.up(this, state, depth);
    }

    @Override
    public void outerSelfRefVisit(Visitor v, Link lastLink, int state, int depth) {
        super.outerSelfRefVisit(v, lastLink, state, depth);

        v.up(this, state, depth);
    }

    @Override
    public void patternCatVisit(Visitor v, Link lastLink, int state, int depth) {
        if(v.isDown() && lastLink != null && lastLink.getSynapse().getScope() == Scope.INPUT)
            v.up(this, state, depth);
        else
            super.patternCatVisit(v, lastLink, state, depth);
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
