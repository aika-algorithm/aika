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


import network.aika.debugger.EventType;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;

/**
 * @author Lukas Molzberger
 */
public class SynapseOutputSlot extends MaxField {

    private Activation inputAct;
    private int synapseId;

    public SynapseOutputSlot(Synapse ref, Activation iAct, String label) {
        super(ref, label);
        this.inputAct = iAct;
        this.synapseId = ref.getSynapseId();
    }

    protected void checkListener(FieldLink lastSelectedInput, FieldLink selectedInput) {
        updateConnection(lastSelectedInput, false);
        updateConnection(selectedInput, true);
    }

    private void updateConnection(FieldLink si, boolean state) {
        if(si == null)
            return;
        Link l = getLink(si);
        FieldLink fl = l.getInputValueLink();
        if (state)
            fl.connect(true);
        else
            fl.disconnect(true);

        if(state != l.isOutputSideActive()) {
            l.getDocument().onElementEvent(EventType.UPDATE, l);
        }
    }

    public Link getSelectedLink() {
        return getLink(getSelectedInput());
    }

    public Link getLink(FieldLink fl) {
        return ((Activation) fl.getInput().getReference())
                .getInputLink(inputAct, synapseId);
    }
}
