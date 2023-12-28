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
package network.aika.elements.synapses.slots;

import network.aika.elements.activations.Activation;
import network.aika.elements.links.ConjunctiveCategoryInputLink;
import network.aika.elements.synapses.ConjunctiveCategoryInputSynapse;
import network.aika.fields.Field;
import network.aika.fields.InputField;

/**
 *
 * @author Lukas Molzberger
 */
public class CategoryInputSynapseOutputSlot extends SynapseOutputSlot<ConjunctiveCategoryInputSynapse, ConjunctiveCategoryInputLink> {

    protected Field feedbackTrigger;

    public CategoryInputSynapseOutputSlot(Activation act, ConjunctiveCategoryInputSynapse synapse) {
        super(act, synapse);
    }

    protected void initFeedbackTrigger() {
        feedbackTrigger = new InputField(this, "instantiation feedback trigger", 0.0);

//        linkAndConnect(feedbackTrigger, 0, inputValue);
    }

    public Field getFeedbackTrigger() {
        return feedbackTrigger;
    }
}
