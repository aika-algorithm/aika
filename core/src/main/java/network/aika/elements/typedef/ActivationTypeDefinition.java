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
package network.aika.elements.typedef;

import network.aika.Document;
import network.aika.elements.NeuronType;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.StateType;
import network.aika.elements.neurons.Neuron;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.FieldOutputDefinition;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lukas Molzberger
 */
public class ActivationTypeDefinition extends TypeDefinition<ActivationTypeDefinition, Activation> {

    private NeuronTypeDefinition neuronType;

    private List<StateTypeDefinition> stateTypes = new ArrayList<>();


    public ActivationTypeDefinition(String name, Class<? extends Activation> clazz) {
        super(name, clazz);
    }

    public ActivationTypeDefinition setNeuronType(NeuronTypeDefinition neuronType) {
        this.neuronType = neuronType;

        return this;
    }

    public NeuronType getType() {
        return neuronType.getType();
    }

    public StateTypeDefinition[] getStateTypes() {
        return stateTypes.toArray(new StateTypeDefinition[0]);
    }

    public ActivationTypeDefinition addStateType(StateTypeDefinition stateType) {
        stateTypes.add(stateType);
        stateType.setActivationType(this);

        return this;
    }

    public Activation instantiate(int id, Document doc, Neuron n) {
        try {
            Activation instance = clazz
                    .getConstructor(Integer.class, Document.class, Neuron.class)
                    .newInstance(id, doc, n);

            instance.setTypeDefinition(this);
            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
