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

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.InnerInhibitoryActivation;
import network.aika.elements.synapses.InnerInhibitorySynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.fields.Field;
import network.aika.visitor.pattern.PatternCategoryVisitor;
import network.aika.visitor.pattern.PatternVisitor;

import static network.aika.fields.FieldLink.linkAndConnect;

/**
 * @author Lukas Molzberger
 */
public class InnerInhibitoryLink extends DisjunctiveLink<InnerInhibitorySynapse, BindingActivation, InnerInhibitoryActivation> {

    public InnerInhibitoryLink(InnerInhibitorySynapse inhibitorySynapse, BindingActivation input, InnerInhibitoryActivation output) {
        super(inhibitorySynapse, input, output);
    }

    @Override
    protected void connectInputValue() {
        linkAndConnect(input.getValueUnsuppressed(), 0, inputValue);
    }

    @Override
    public void patternVisit(PatternVisitor v, int depth) {
    }

    @Override
    public void patternCatVisit(PatternCategoryVisitor v, int depth) {
    }
}