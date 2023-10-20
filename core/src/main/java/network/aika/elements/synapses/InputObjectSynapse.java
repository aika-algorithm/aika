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
package network.aika.elements.synapses;

import network.aika.elements.Type;
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.InputObjectLink;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.enums.Scope;

import static network.aika.elements.Type.BINDING;
import static network.aika.elements.Type.PATTERN;

/**
 *
 * @author Lukas Molzberger
 */
public class InputObjectSynapse extends ConjunctiveSynapse<
        InputObjectSynapse,
        PatternNeuron,
        BindingNeuron,
        InputObjectLink,
        PatternActivation,
        BindingActivation
        >
{
    public static int TYPE_ID = 2;

    public int getTypeId() {
        return TYPE_ID;
    }

    @Override
    public Type getInputType() {
        return PATTERN;
    }

    @Override
    public Type getOutputType() {
        return BINDING;
    }

    @Override
    public Scope getScope() {
        return Scope.INPUT;
    }

    @Override
    public InputObjectLink createLink(PatternActivation input, BindingActivation output) {
        return new InputObjectLink(this, input, output);
    }

    @Override
    public boolean checkSingularLinkDoesNotExist(BindingActivation oAct) {
        return !linkExists(oAct, true);
    }
}
