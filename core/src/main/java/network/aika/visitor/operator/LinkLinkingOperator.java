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
package network.aika.visitor.operator;

import network.aika.enums.direction.Direction;
import network.aika.elements.synapses.Synapse;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.enums.Scope;
import network.aika.visitor.LinkingVisitor;

/**
 * @author Lukas Molzberger
 */
public class LinkLinkingOperator extends LinkingOperator {


    public LinkLinkingOperator(Activation fromAct, Synapse syn) {
        super(fromAct, syn);
    }

    @Override
    public Direction getRelationDir(Scope fromScope) {
        return fromScope.getRelationDir().invert();
    }

    @Override
    public void check(LinkingVisitor v, Link l, Activation act) {
        if(l == null)
            return;

        if(act.getNeuron() != syn.getOutput())
            return;

        if(act == fromAct)
            return;

        if(!v.compatible(syn.getScope(), l.getSynapse().getScope()))
            return;

        if(!syn.checkSingularLinkDoesNotExist(l.getOutput()))
            return;

        syn.link(fromAct, l.getOutput());
    }
}
