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
package network.aika.debugger.activations.properties.links;

import network.aika.elements.links.*;
import network.aika.elements.links.types.InputObjectLink;
import network.aika.elements.links.types.PatternLink;
import network.aika.elements.synapses.slots.SynapseInputSlot;
import network.aika.elements.synapses.slots.SynapseOutputSlot;


/**
 * @author Lukas Molzberger
 */
public class ConjunctiveLinkPropertyPanel<L extends ConjunctiveLink<?, ?, ?>> extends LinkPropertyPanel<L> {

    public ConjunctiveLinkPropertyPanel(L l) {
        super(l);
    }

    @Override
    public void initInputIdentitySection(L l) {
        super.initInputIdentitySection(l);

        SynapseInputSlot slot = l.getSynInputSlot();
        if(slot != null) {
            ConjunctiveLink sl = slot.getSelectedLink();
            addConstant(
                    "Slot Selection: ",
                    sl != null ?
                            "" + sl.getOutput() :
                            "--"
            );
        }
    }

    @Override
    public void initOutputIdentitySection(L l) {
        super.initOutputIdentitySection(l);

        SynapseOutputSlot slot = l.getSynOutputSlot();
        if(slot != null) {
            ConjunctiveLink sl = slot.getSelectedLink();
            addConstant(
                    "Slot Selection: ",
                    sl != null ?
                            "" + sl.getInput() :
                            "--"
            );
        }
    }

    public void initTrainingSection(L l) {
        addField(l.getWeightUpdatePosCase());
        addField(l.getWeightUpdateNegCase());
        addField(l.getBiasUpdateNegCase());

        super.initTrainingSection(l);
    }

    public static ConjunctiveLinkPropertyPanel create(ConjunctiveLink l) {
        if(l instanceof PatternLink) {
            return new PatternLinkPropertyPanel((PatternLink)l);
        } else if(l instanceof InputObjectLink) {
            return InputPatternLinkPropertyPanel.create((InputObjectLink)l);
        }

        return new ConjunctiveLinkPropertyPanel(l);
    }
}
