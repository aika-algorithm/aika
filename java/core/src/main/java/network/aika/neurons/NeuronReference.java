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
package network.aika.neurons;

import network.aika.Model;

/**
 *
 * @author Lukas Molzberger
 */
public class NeuronReference {

    private final long id;
    private final RefType refType;

    private Neuron neuron;


    public NeuronReference(long neuronId, RefType refType) {
        this.id = neuronId;
        this.refType = refType;
    }

    public NeuronReference(Neuron n, RefType refType) {
        this.id = n.getId();
        this.refType = refType;
        this.neuron = n;
    }

    public long getId() {
        return id;
    }

    public Neuron getRawNeuron() {
        return neuron;
    }

    public synchronized <N extends Neuron> N getNeuron(Model m) {
        if (neuron == null) {
            neuron = m.getNeuron(id);
            neuron.increaseRefCount(refType);
        }

        return (N) neuron;
    }

    public void suspendNeuron() {
        assert neuron != null;

        neuron.decreaseRefCount(refType);
        neuron = null;
    }

    public String toString() {
        return "p(" + (neuron != null ? neuron : id + ":" + "SUSPENDED") + ")";
    }

    public String toKeyString() {
        return "p(" + (neuron != null ? neuron.toKeyString() : id + ":" + "SUSPENDED") + ")";
    }
}
