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
package network.aika.bindingsignal;

import network.aika.Document;
import network.aika.activations.Activation;
import network.aika.activations.ActivationKey;
import network.aika.neurons.Neuron;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingSignal {

    private final int tokenId;

    private final Document doc;

    private final NavigableMap<ActivationKey, Activation> activations = new TreeMap<>(
            Comparator.comparingLong(ActivationKey::neuronId)
                    .thenComparingInt(ActivationKey::actId)
    );

    public BindingSignal(int tokenId, Document doc) {
        this.tokenId = tokenId;
        this.doc = doc;
    }

    public int getTokenId() {
        return tokenId;
    }

    public Document getDocument() {
        return doc;
    }

    public void addActivation(Activation act) {
        activations.put(act.getKey(), act);
    }

    public Stream<Activation> getActivations(Neuron n) {
        return activations.subMap(
                        new ActivationKey(n.getId(), Integer.MIN_VALUE),
                        new ActivationKey(n.getId(), Integer.MAX_VALUE)
                )
                .values()
                .stream();
    }

    public Collection<Activation> getActivations() {
        return activations.values();
    }

    public String toString() {
        return "" + tokenId;
    }
}
