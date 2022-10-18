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
package network.aika.neuron.activation;

import network.aika.neuron.conjunctive.AbstractPatternSynapse;
import network.aika.neuron.conjunctive.PatternSynapse;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractPatternLink<S extends AbstractPatternSynapse, IA extends Activation<?>> extends ConjunctiveLink<S, IA, PatternActivation> {

    public AbstractPatternLink(S s, IA input, PatternActivation output) {
        super(s, input, output);
    }


    /*
    OP-2
    public void trackBindingSignal(Visitor v, Predicate<Activation> p) {
        if(v.getDir() == Direction.INPUT)
            return;

        super.trackBindingSignal(v, p);
    }
     */
}