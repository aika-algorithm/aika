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
import network.aika.elements.activations.BindingCategoryActivation;
import network.aika.elements.activations.CategoryActivation;
import network.aika.visitor.types.VisitorType;

import static network.aika.elements.Type.BINDING;
import static network.aika.visitor.types.VisitorType.BINDING_VISITOR_TYPE;

/**
 * @author Lukas Molzberger
 */
public class BindingCategoryNeuron extends CategoryNeuron {

    public BindingCategoryNeuron(Model m) {
        super(m);
    }

    @Override
    public Type getType() {
        return BINDING;
    }

    @Override
    public VisitorType getVisitorType() {
        return BINDING_VISITOR_TYPE;
    }

    @Override
    public CategoryActivation createActivation(Thought t) {
        return new BindingCategoryActivation(t.createActivationId(), t, this);
    }
}
