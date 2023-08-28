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
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.text.Range;
import network.aika.elements.neurons.relations.LatentRelationNeuron;
import network.aika.elements.neurons.TokenNeuron;
import network.aika.visitor.binding.BindingVisitor;
import network.aika.text.Document;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 *
 * @author Lukas Molzberger
 */
public class TokenActivation extends PatternActivation {

    public TokenActivation(int id, Thought t, TokenNeuron tokenNeuron) {
        super(id, t, tokenNeuron);
    }

    public TokenActivation(int id, Range tpRange, Range charRange, Document doc, TokenNeuron tokenNeuron) {
        this(id, doc, tokenNeuron);

        updateRanges(tpRange, charRange);

        registerPosRange(null, tpRange);
    }

    @Override
    protected void connectWeightUpdate() {
        // Input activations don't need weight updates
    }

    public boolean isInput() {
        return true;
    }
}
