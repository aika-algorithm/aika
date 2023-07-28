package network.aika.elements.links;
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
import network.aika.elements.activations.*;
import network.aika.elements.synapses.CategorySynapse;
import network.aika.elements.synapses.Synapse;

/**
 *
 * @author Lukas Molzberger
 */
public interface CategoryInputLink {

    CategoryActivation getInput();

    Activation getOutput();

    Synapse getSynapse();

    CategorySynapse createCategorySynapse();

    default void instantiateTemplate(CategoryActivation iAct, Activation oAct, Link template) {
        if(iAct == null || oAct == null)
            return;

        Link l = iAct.getInputLink(oAct);
        if(l != null)
            return;

        CategorySynapse s = createCategorySynapse();
        s.initFromTemplate(oAct.getNeuron(), iAct.getNeuron(), getSynapse());

        s.createLinkFromTemplate(oAct, iAct, template);
    }
}
