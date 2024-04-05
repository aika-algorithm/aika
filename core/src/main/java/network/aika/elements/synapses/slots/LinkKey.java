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
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.Link;
import network.aika.elements.neurons.Neuron;
import network.aika.enums.direction.Direction;
import network.aika.fields.link.ArgumentFieldLink;

/**
 *
 * @author Lukas Molzberger
 */
public class LinkKey implements Comparable<LinkKey> {

    private Neuron n;
    private Activation act;


    public LinkKey(Neuron n, Activation act) {
        this.n = n;
        this.act = act;
    }

    private LinkKey(Link l, Direction dir) {
        this(
                dir.getNeuron(l.getSynapse()),
                dir.getActivation(l)
        );
    }

    public LinkKey(ArgumentFieldLink<? extends Link> fl, Direction dir) {
        this(
                fl.getArgumentRef(),
                dir.invert()
        );
    }

    @Override
    public int compareTo(LinkKey lk) {
        int r = Long.compare(n.getId(), lk.n.getId());
        if(r != 0)
            return r;

        if(act == lk.act)
            return 0;
        else if(act == null)
            return -1;
        else if(lk.act == null)
            return 1;

        return Integer.compare(act.getId(), lk.act.getId());
    }

    @Override
    public String toString() {
        return n  + " - " + (act != null ? "" + act : "--");
    }
}
