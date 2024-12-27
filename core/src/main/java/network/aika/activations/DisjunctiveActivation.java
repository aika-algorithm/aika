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
package network.aika.activations;

import network.aika.Document;
import network.aika.bindingsignal.BindingSignal;
import network.aika.neurons.Neuron;
import network.aika.typedefs.ActivationDefinition;
import network.aika.bindingsignal.BSType;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * @author Lukas Molzberger
 */
public class DisjunctiveActivation extends Activation {

    protected NavigableMap<Integer, Link> inputLinks = new TreeMap<>();

    public DisjunctiveActivation(
            ActivationDefinition t,
            Activation parent,
            Integer id,
            Neuron n,
            Document doc,
            Map<BSType, BindingSignal> bindingSignals
    ) {
        super(t, parent, id, n, doc, bindingSignals);
    }

    @Override
    public void addInputLink(Link l) {
        Activation iAct = l.getInput();
        assert inputLinks.get(iAct.getId()) == null;
        inputLinks.put(iAct.getId(), l);
    }

    @Override
    public void linkIncoming(Activation excludedInputAct) {

    }

    @Override
    public Stream<Link> getInputLinks() {
        return inputLinks.values()
                .stream();
    }
}
