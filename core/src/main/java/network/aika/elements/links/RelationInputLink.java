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
package network.aika.elements.links;

import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.LatentRelationActivation;
import network.aika.elements.synapses.RelationInputSynapse;
import network.aika.visitor.Visitor;
import network.aika.visitor.types.PatternCategoryVisitor;
import network.aika.visitor.types.PatternVisitor;

/**
 * @author Lukas Molzberger
 */
public class RelationInputLink extends BindingNeuronLink<RelationInputSynapse, LatentRelationActivation> {

    public RelationInputLink(RelationInputSynapse s, LatentRelationActivation input, BindingActivation output) {
        super(s, input, output);
    }

    @Override
    public void propagateRanges() {
    }

    @Override
    public void patternVisit(Visitor v, int depth) {
    }

    @Override
    public void patternCatVisit(Visitor v, int depth) {
    }
}
