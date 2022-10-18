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
package network.aika.neuron.conjunctive;

import network.aika.Thought;
import network.aika.neuron.Neuron;
import network.aika.neuron.activation.AbstractPatternLink;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.PatternActivation;
import network.aika.neuron.visitor.linking.LinkingDownVisitor;
import network.aika.neuron.visitor.linking.LinkingOperator;
import network.aika.neuron.visitor.linking.pattern.PatternDownVisitor;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class AbstractPatternSynapse<S extends AbstractPatternSynapse, I extends Neuron, L extends AbstractPatternLink<S, IA>, IA extends Activation<?>> extends ConjunctiveSynapse<
        S,
        I,
        PatternNeuron,
        L,
        IA,
        PatternActivation
        >
{

    public AbstractPatternSynapse() {
        super(Scope.SAME);
    }

    @Override
    public boolean isPropagate() {
        return true;
    }

    @Override
    public boolean propagateCheck(IA iAct) {
        return true;
    }

    @Override
    public void startVisitor(LinkingOperator c, Activation bs) {
        new PatternDownVisitor(bs.getThought(), c)
                .start(bs);
    }
}