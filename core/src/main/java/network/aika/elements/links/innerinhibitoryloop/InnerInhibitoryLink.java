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
package network.aika.elements.links.innerinhibitoryloop;

import network.aika.elements.Type;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.links.DisjunctiveLink;
import network.aika.elements.synapses.innerinhibitoryloop.InnerInhibitorySynapse;
import network.aika.visitor.Visitor;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.INNER_INHIBITORY;
import static network.aika.fields.FieldLink.linkAndConnect;

/**
 * @author Lukas Molzberger
 */
public class InnerInhibitoryLink extends DisjunctiveLink<InnerInhibitorySynapse, BindingActivation, InnerInhibitoryActivation> {

    public InnerInhibitoryLink(InnerInhibitorySynapse inhibitorySynapse, BindingActivation input, InnerInhibitoryActivation output) {
        super(inhibitorySynapse, input, output);
    }

    @Override
    public Type getInputType() {
        return BINDING;
    }

    @Override
    public Type getOutputType() {
        return INNER_INHIBITORY;
    }

    @Override
    public void patternVisit(Visitor v, int depth) {
    }

    @Override
    public void patternCatVisit(Visitor v, int depth) {
    }

    @Override
    public void outerInhibVisit(Visitor v, int depth) {
    }

    @Override
    public void innerSelfRefVisit(Visitor v, int depth) {
    }

    @Override
    public void outerSelfRefVisit(Visitor v, int depth) {
    }
}