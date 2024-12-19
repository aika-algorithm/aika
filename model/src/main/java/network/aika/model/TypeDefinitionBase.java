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
package network.aika.model;

import network.aika.elements.typedef.*;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class TypeDefinitionBase implements TypeDefinition {

    protected TypeModel typeModel;
    protected TypeDefinitionBase superType;

    public TypeDefinitionBase(TypeModel typeModel, TypeDefinitionBase superType) {
        this.typeModel = typeModel;
        this.superType = superType;
    }

    public TypeModel getTypeModel() {
        return typeModel;
    }

    public TypeDefinitionBase getSuperType() {
        return superType;
    }

    @Override
    public SynapseSlotDefinition getInputSlot() {
        return superType.getInputSlot();
    }

    @Override
    public SynapseSlotDefinition getOutputSlot() {
        return superType.getOutputSlot();
    }

    @Override
    public ActivationDefinition getCategoryActivation() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NeuronDefinition getCategoryNeuron() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkDefinition getCategoryInputLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SynapseDefinition getCategoryInputSynapse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkDefinition getCategoryLink() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SynapseDefinition getCategorySynapse() {
        throw new UnsupportedOperationException();
    }
}
