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
package network.aika.elements.synapses;

import network.aika.elements.Type;
import network.aika.elements.neurons.Neuron;
import network.aika.enums.LinkingMode;
import network.aika.enums.Transition;

/**
 *
 * @author Lukas Molzberger
 */
public class SynapseTypeHolder {
    private Type inputType;

    private Type outputType;

    private Transition transition;

    private Transition[] required;

    private Transition[] forbidden;

    private LinkingMode linkingMode;

    public SynapseTypeHolder(SynapseType synTypeAnno) {
        inputType = synTypeAnno.inputType();
        outputType = synTypeAnno.outputType();
        transition = synTypeAnno.transition();
        required = synTypeAnno.required();
        forbidden = synTypeAnno.forbidden();
        linkingMode = synTypeAnno.linkingMode();
    }

    public Type getInputType() {
        return inputType;
    }

    public Type getOutputType() {
        return outputType;
    }

    public Transition getTransition() {
        return transition;
    }

    public Transition[] getRequired() {
        return required;
    }

    public Transition[] getForbidden() {
        return forbidden;
    }

    public LinkingMode getLinkingMode() {
        return linkingMode;
    }
}