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
import network.aika.fielddefs.Path;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkDefinition extends TypeDefinition<LinkDefinition, Link> {

    private SynapseDefinition synapse;
    private ActivationDefinition input;
    private ActivationDefinition output;

    private FieldDefinition<LinkDefinition> inputValue;

    private FieldDefinition<LinkDefinition> inputIsFired;

    private FieldDefinition<LinkDefinition> negInputIsFired;


    public LinkDefinition(String name, Class<? extends Link> clazz) {
        super(name, clazz);
    }

    public FieldDefinition<LinkDefinition> getInputValue() {
        return inputValue;
    }

    public void setInputValue(FieldDefinition<LinkDefinition> inputValue) {
        this.inputValue = inputValue;
    }

    public FieldDefinition<LinkDefinition> getInputIsFired() {
        return inputIsFired;
    }

    public void setInputIsFired(FieldDefinition<LinkDefinition> inputIsFired) {
        this.inputIsFired = inputIsFired;
    }

    public FieldDefinition<LinkDefinition> getNegInputIsFired() {
        return negInputIsFired;
    }

    public void setNegInputIsFired(FieldDefinition<LinkDefinition> negInputIsFired) {
        this.negInputIsFired = negInputIsFired;
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

    public SynapseDefinition getSynapse() {
        return synapse;
    }

    public SynapseDefinition getSynapse(Path p) {
        p.add(synapse);
        return synapse;
    }

    public LinkDefinition setSynapse(SynapseDefinition synapse) {
        this.synapse = synapse;

        return this;
    }

    public ActivationDefinition getInput(Path p) {
        p.add(input);

        return input;
    }

    public ActivationDefinition getInput() {
        return input;
    }

    public LinkDefinition setInput(ActivationDefinition input) {
        this.input = input;

        return this;
    }

    public ActivationDefinition getOutput(Path p) {
        p.add(output);
        return output;
    }

    public ActivationDefinition getOutput() {
        return output;
    }

    public LinkDefinition setOutput(ActivationDefinition output) {
        this.output = output;

        return this;
    }
}