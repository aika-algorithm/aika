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
package network.aika.fields;


import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.synapses.Synapse;

import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * @author Lukas Molzberger
 */
public class SynapseOutputSlot extends MaxField {

    public SynapseOutputSlot(Synapse ref, String label, Double tolerance) {
        super(ref, label, tolerance);
    }

    @Override
    protected void updateConnection(FieldLink si, boolean state) {
        if(si == null)
            return;
        ConjunctiveLink l = getLink(si);
        l.updateLinkState(OUTPUT, state);
    }

    public ConjunctiveLink getSelectedLink() {
        return getLink(getSelectedInput());
    }

    public static ConjunctiveLink getLink(FieldLink fl) {
        if(fl == null)
            return null;

        return (ConjunctiveLink) fl.getInput().getReference();
    }

    @Override
    protected boolean isCandidate(FieldLink fl) {
        return getLink(fl).getInput() != null;
    }
}