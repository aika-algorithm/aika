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

import network.aika.neuron.activation.*;
import network.aika.neuron.disjunctive.CategoryNeuron;
import network.aika.neuron.disjunctive.PatternCategoryNeuron;

/**
 * The Same Pattern Binding Neuron Synapse is an inner synapse between two binding neurons of the same pattern.
 *
 * @author Lukas Molzberger
 */
public class PatternCategoryInputSynapse extends AbstractPatternSynapse<
        PatternCategoryInputSynapse,
        PatternCategoryNeuron,
        PatternCategoryInputLink,
        CategoryActivation<?>
        > implements CategoryInputSynapse<PatternCategoryInputSynapse>
{
    @Override
    public PatternCategoryInputLink createUnconnectedLink(CategoryActivation input, PatternActivation output) {
        return new PatternCategoryInputLink(this, input, output);
    }
/*
    @Override
    protected boolean checkCausal(CategoryActivation iAct, PatternActivation oAct) {
        return true; // Workaround
    }
 */
}