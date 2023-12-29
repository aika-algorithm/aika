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
package network.aika.debugger.activations.properties;

import network.aika.debugger.neurons.properties.neurons.NeuronPropertyPanel;
import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.NeuronProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Lukas Molzberger
 */
public class PropagablePropertyPanel extends AbstractPropertyPanel {

    public PropagablePropertyPanel(Collection<NeuronProvider> propagable) {
        List<NeuronProvider> sortedPropagatable = propagable.stream().collect(Collectors.toList());

        Collections.sort(sortedPropagatable, Comparator.comparingDouble(np -> np.getId()));
        sortedPropagatable.stream()
                .limit(10)
                .forEach(np -> {
                    addEntry(NeuronPropertyPanel.create(np.getNeuron(), null));
                    addSeparator();
                }
        );

        addFinal();
    }

    public static PropagablePropertyPanel create(Neuron n) {
        if(n == null)
            return null;

        return new PropagablePropertyPanel(n.getPropagable());
    }
}