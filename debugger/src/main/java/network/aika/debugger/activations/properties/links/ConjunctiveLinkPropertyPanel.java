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


/**
 * @author Lukas Molzberger
 */
public class ConjunctiveLinkPropertyPanel<E extends ConjunctiveLink> extends LinkPropertyPanel<E> {

    public ConjunctiveLinkPropertyPanel(E l) {
        super(l);
    }

    @Override
    public void initInputIdentitySection(E l) {
        super.initInputIdentitySection(l);

        addField(l.getSynInputSlot());
        addConstant("Slot Selection: ", "" + l.getSynInputSlot().getSelectedLink());
    }

    @Override
    public void initOutputIdentitySection(E l) {
        super.initOutputIdentitySection(l);

        addField(l.getSynOutputSlot());
        addConstant("Slot Selection: ", "" + l.getSynOutputSlot().getSelectedLink());
    }

    public void initTrainingSection(E l) {
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
