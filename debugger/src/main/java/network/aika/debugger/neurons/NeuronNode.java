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
package network.aika.debugger.neurons;


import network.aika.debugger.Node;
import network.aika.elements.neurons.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lukas Molzberger
 */
public class NeuronNode extends Node<Neuron> {


    protected static Map<Class<? extends Neuron>, Class<? extends NeuronNode>> typeMap = new HashMap<>();

    static {
        typeMap.put(BindingNeuron.class, NeuronNode.class);
        typeMap.put(PatternNeuron.class, NeuronNode.class);
        typeMap.put(TokenNeuron.class, NeuronNode.class);
        typeMap.put(LatentRelationNeuron.class, NeuronNode.class);
        typeMap.put(InhibitoryNeuron.class, NeuronNode.class);
        typeMap.put(CategoryNeuron.class, NeuronNode.class);
    }

    public static NeuronNode create(Neuron n) {
        Class<? extends NeuronNode> clazz = typeMap.get(n.getClass());

        try {
            return clazz.getDeclaredConstructor(Neuron.class)
                    .newInstance(n);

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

    public NeuronNode(Neuron neuron) {
        super(neuron);
    }


}
