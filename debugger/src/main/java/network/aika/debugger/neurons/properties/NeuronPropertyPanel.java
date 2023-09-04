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
package network.aika.debugger.neurons.properties;

import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.OuterInhibitoryNeuron;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.neurons.InnerInhibitoryNeuron;
import network.aika.utils.Utils;


/**
 * @author Lukas Molzberger
 */
public class NeuronPropertyPanel<E extends Neuron> extends AbstractPropertyPanel {


    public NeuronPropertyPanel(E n, Activation ref) {
        addTitle(n.getClass().getSimpleName() + " " + "\n");

        addConstant("Id: ", "" + n.getId());
        addConstant("Label: ", n.getLabel());

        addField(n.getBias());

        if(n.getTargetNet() != null) {
            addConstant("Target Net: ", "" + Utils.doubleToString(n.getTargetNet()));
            addConstant("Target Value: ", "" + Utils.doubleToString(n.getTargetValue()));
        }

        addConstant("Is Abstract: ", "" + n.isAbstract());
        addConstant("Is Training Allowed: ", "" + n.isTrainingAllowed());

        addConstant("Is Template Only: ", "" + n.isTemplateOnly());
        if(n.getTemplate() != null)
            addConstant("Template Neuron: ", n.getTemplate().getId() + ":" + n.getTemplate().getLabel());

        addConstant("Is Modified: ", "" + n.isModified());
    }

    public static NeuronPropertyPanel create(Neuron n, Activation ref) {
        if(n instanceof ConjunctiveNeuron<?>) {
            return ConjunctiveNeuronPropertyPanel.create((ConjunctiveNeuron) n, ref);
        } else if(n instanceof OuterInhibitoryNeuron) {
            return InputInhibitoryNeuronPropertyPanel.create((OuterInhibitoryNeuron) n, ref);
        } else if(n instanceof InnerInhibitoryNeuron) {
            return SameInhibitoryNeuronPropertyPanel.create((InnerInhibitoryNeuron) n, ref);
        }

        return new NeuronPropertyPanel(n, ref);
    }
}
