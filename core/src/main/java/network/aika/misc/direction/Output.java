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
package network.aika.misc.direction;

import network.aika.Model;
import network.aika.activations.Activation;
import network.aika.activations.Link;
import network.aika.neurons.Neuron;
import network.aika.neurons.Synapse;
import network.aika.bindingsignal.BSType;
import network.aika.bindingsignal.Transition;

import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Lukas Molzberger
 */
public class Output implements Direction {

    @Override
    public Direction invert() {
        return INPUT;
    }

    @Override
    public <I> I getInput(I from, I to) {
        return from;
    }

    @Override
    public <O> O getOutput(O from, O to) {
        return to;
    }

    @Override
    public Neuron getNeuron(Model m, Synapse s) {
        return s.getOutput(m);
    }

    @Override
    public Activation getActivation(Link l) {
        return l != null ?
                l.getOutput() :
                null;
    }

    @Override
    public BSType transition(BSType s, Transition[] trns) {
        for(Transition t: trns)
            if(t.from() == s)
                return t.to();

        return null;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeBoolean(true);
    }

    public String toString() {
        return "OUTPUT";
    }
}
