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
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.direction.Direction;
import network.aika.fields.Field;
import network.aika.fields.FieldObject;

import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public interface SynapseSlot<S extends Synapse, L extends Link> extends FieldObject {

    void init();

    void addLink(L l);

    Stream<L> getLinks();

    L getLink(Activation act);

    Field getInputField();

    Field getOutputField();

    L getSelectedLink();

    S getSynapse();

    Activation getActivation();

    Direction getDirection();
}
