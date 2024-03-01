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
package network.aika.debugger.neurons.properties.slots;

import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.synapses.slots.AnnealingSynapseOutputSlot;
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.fields.link.ArgumentFieldLink;
import network.aika.utils.ApproximateComparisonValueUtil;

import java.util.Collection;


/**
 * @author Lukas Molzberger
 */
public class ConjunctiveSynapseSlotPropertyPanel<S extends ConjunctiveSynapseSlot> extends SynapseSlotPropertyPanel<S> {

    public ConjunctiveSynapseSlotPropertyPanel(S s) {
        super(s);
    }

    @Override
    public void initSlotProperties(S s) {
        super.initSlotProperties(s);

        addField(s);
    }

    @Override
    protected void initLinks(S s) {
        Collection<ArgumentFieldLink<ConjunctiveLink>> inputs = s.getInputs();

        addConstant("Links: ", "");
        inputs.stream().limit(10)
                .forEach(in ->
                        addConstant("",
                                "iv:" + ApproximateComparisonValueUtil.convert(in.getUpdatedInputValue()) +
                                        " f:" +  in.getArgumentRef().getFired() +
                                        " l:" + s.getDirection().invert().getActivation(in.getArgumentRef())
                        )
                );

        addFinal();
    }
    public static ConjunctiveSynapseSlotPropertyPanel create(ConjunctiveSynapseSlot s) {
        if(s instanceof AnnealingSynapseOutputSlot) {
            return AnnealingSynapseOutputSlotPropertyPanel.create((AnnealingSynapseOutputSlot) s);
        }

        return new ConjunctiveSynapseSlotPropertyPanel(s);
    }
}
