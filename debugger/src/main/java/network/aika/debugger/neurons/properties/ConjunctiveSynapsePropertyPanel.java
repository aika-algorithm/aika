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

import network.aika.elements.links.Link;
import network.aika.elements.synapses.*;

import static network.aika.utils.Utils.doubleToString;


/**
 * @author Lukas Molzberger
 */
public class ConjunctiveSynapsePropertyPanel<E extends ConjunctiveSynapse> extends SynapsePropertyPanel<E> {

    public ConjunctiveSynapsePropertyPanel(E s, Link ref) {
        super(s, ref);
    }

    public void initSynapseProperties(E s) {
        addField(s.getSynapseBias());

        super.initSynapseProperties(s);
        addConstant("Propagatable: ", "" + s.isPropagable());
        addConstant("isOptional", "" + s.isOptional());
        addConstant("Sum of Lower Weights:", doubleToString(s.getSumOfLowerWeights(), "#.######"));
        addConstant("Sorting Weight:", "" + s.getSortingWeight());
    }

    public static ConjunctiveSynapsePropertyPanel create(ConjunctiveSynapse s, Link ref) {
        if(s instanceof PositiveFeedbackSynapse) {
            return new PositiveFeedbackSynapsePropertyPanel((PositiveFeedbackSynapse) s, ref);
        } else if(s instanceof SameObjectSynapse) {
            return new SameObjectSynapsePropertyPanel((SameObjectSynapse) s, ref);
        } else if(s instanceof PatternSynapse) {
            return new PatternSynapsePropertyPanel((PatternSynapse) s, ref);
        } else if(s instanceof RelationInputSynapse) {
            return new RelationInputSynapsePropertyPanel((RelationInputSynapse) s, ref);
        }

        return new ConjunctiveSynapsePropertyPanel(s, ref);
    }
}
