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

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.PrimaryInputLink;
import network.aika.elements.neurons.PatternNeuron;
import network.aika.enums.Scope;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class PrimaryInputSynapse<S extends PrimaryInputSynapse, L extends PrimaryInputLink<S>> extends BindingNeuronSynapse<
        S,
        PatternNeuron,
        L,
        PatternActivation
        >
{
    public PrimaryInputSynapse(Scope scope) {
        super(scope);
    }

    @Override
    public boolean checkSingularLinkDoesNotExist(BindingActivation oAct) {
        return !linkExists(oAct, true);
    }
}