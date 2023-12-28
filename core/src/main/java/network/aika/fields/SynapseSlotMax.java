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
import network.aika.elements.synapses.slots.ConjunctiveSynapseSlot;
import network.aika.enums.direction.Direction;
import network.aika.fields.link.ArgumentFieldLink;
import network.aika.fields.link.FieldLink;
import network.aika.queue.steps.LinkUpdate;

import static network.aika.enums.direction.Direction.INPUT;

/**
 * @author Lukas Molzberger
 */
public class SynapseSlotMax<L extends ConjunctiveLink> extends MaxField {

    private Direction dir;

    public SynapseSlotMax(ConjunctiveSynapseSlot ref, String label, Direction dir, Double tolerance) {
        super(ref, label, tolerance);
        this.dir = dir;
    }

    @Override
    protected void updateSelectedInput(FieldLink si, boolean state) {
        if(si == null)
            return;

        LinkUpdate.add(
                getLink(si),
                dir,
                state
        );
    }

    public L getSelectedLink() {
        return getLink(getSelectedInput());
    }

    public L getLink(FieldLink fl) {
        if(fl == null)
            return null;

        ArgumentFieldLink afl = (ArgumentFieldLink) fl;
        return (L) afl.getArgumentRef();
    }

    @Override
    protected boolean isCandidate(FieldLink fl) {
        return dir == INPUT || getLink(fl).getInput() != null;
    }
}
