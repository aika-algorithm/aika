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
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.Thought;
import network.aika.elements.Type;
import network.aika.elements.activations.CategoryActivation;
import network.aika.elements.activations.InnerInhibitoryCategoryActivation;
import network.aika.visitor.types.VisitorType;

import static network.aika.elements.Type.INNER_INHIBITORY;
import static network.aika.visitor.types.VisitorType.INNER_INHIB_VISITOR_TYPE;

/**
 * @author Lukas Molzberger
 */
public class InnerInhibitoryCategoryNeuron extends CategoryNeuron {


    public InnerInhibitoryCategoryNeuron(Model m) {
        super(m);
    }

    @Override
    public Type getType() {
        return INNER_INHIBITORY;
    }

    @Override
    public VisitorType getVisitorType() {
        return INNER_INHIB_VISITOR_TYPE;
    }

    @Override
    public CategoryActivation createActivation(Thought t) {
        return new InnerInhibitoryCategoryActivation(t.createActivationId(), t, this);
    }
}