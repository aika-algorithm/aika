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

import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.fielddefs.FieldDefinition;
import network.aika.fields.AbstractFunction;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkTypeDefinition extends TypeDefinition<LinkTypeDefinition, Link> {

    ActivationTypeDefinition inputDef;
    ActivationTypeDefinition outputDef;

    public FieldDefinition inputValue;
    public FieldDefinition inputIsFired;
    public FieldDefinition negInputIsFired;
    public FieldDefinition weightedInput;

    public FieldDefinition gradient;

    public LinkTypeDefinition(String name, Class<? extends Link> clazz) {
        super(name, clazz);
    }

    public Link instantiate(Synapse s, Activation iAct, Activation oAct) {
        try {
            Link instance = clazz
                    .getConstructor(Synapse.class, Activation.class, Activation.class)
                    .newInstance(s, iAct, oAct);

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

    public ActivationTypeDefinition getInputDef() {
        return inputDef;
    }

    public LinkTypeDefinition setInputDef(ActivationTypeDefinition inputDef) {
        this.inputDef = inputDef;

        return this;
    }

    public ActivationTypeDefinition getOutputDef() {
        return outputDef;
    }

    public LinkTypeDefinition setOutputDef(ActivationTypeDefinition outputDef) {
        this.outputDef = outputDef;

        return this;
    }
}
