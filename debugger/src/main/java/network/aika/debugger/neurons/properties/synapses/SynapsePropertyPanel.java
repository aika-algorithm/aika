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
package network.aika.debugger.neurons.properties.synapses;

import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.ConjunctiveSynapse;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.synapses.SynapseTypeHolder;
import network.aika.utils.Utils;

import java.util.Arrays;
import java.util.stream.Collectors;

import static network.aika.utils.Utils.doubleToString;


/**
 * @author Lukas Molzberger
 */
public class SynapsePropertyPanel<E extends Synapse> extends AbstractPropertyPanel {

    public SynapsePropertyPanel(E s, Link ref) {
        addTitle(s.getClass().getSimpleName());
        addConstant("Synapse Id: ", "" + s.getSynapseId());

        addConstant("Input: ", s.getInput().toString());
        addConstant("Output: ", s.getOutput().toString());

        addTitle("Synapse Properties", SUB_TITLE_SIZE);
        initSynapseProperties(s);

        addTitle("Synapse-Type Properties", SUB_TITLE_SIZE);
        initSynapseTypeProperties(s.getSynapseType());
    }

    protected void initSynapseProperties(E s) {
        addField(s.getWeight());

        addConstant("Is Training Allowed: ", "" + s.isTrainingAllowed());
        addConstant("Stored At: ", "" + s.getStoredAt());
        addConstant("Propagable: ", "" + s.isPropagable());

        if(s.getRelation() != null)
            addConstant("Relation: ", "" + s.getRelation());

        addConstant("Instantiable: ", "" + s.isInstantiable());
        addConstant("Initial Instance Weight: ", "" + Utils.doubleToString(s.getInitialInstanceWeight()));
    }

    protected void initSynapseTypeProperties(SynapseTypeHolder st) {
        addConstant("InputType: ", "" + st.getInputType());
        addConstant("OutputType: ", "" + st.getOutputType());
        addConstant("Transitions: ", "" +
                Arrays.stream(st.getTransition())
                .map(t -> "" + t).collect(Collectors.joining(", "))
        );
        addConstant("Required: ", "" + st.getRequired());
        addConstant("Trigger: ", "" + st.getTrigger());
        addConstant("StateType: ", "" + st.outputState());
    }

    public static SynapsePropertyPanel create(Synapse s, Link ref) {
        if(s instanceof ConjunctiveSynapse) {
            return ConjunctiveSynapsePropertyPanel.create((ConjunctiveSynapse) s, ref);
        }

        return new SynapsePropertyPanel(s, ref);
    }
}
