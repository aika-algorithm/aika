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
package network.aika.elements.relations;

import network.aika.Model;
import network.aika.elements.PreActivation;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.direction.Direction;
import network.aika.text.TextReference;
import network.aika.utils.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class Relation implements Writable {

    public abstract Relation instantiate();

    public void linkRelationFromTemplate(Activation instanceOAct, Synapse instanceSyn, Link templateLink) {
    }

    public abstract int getRelationType();

    public abstract Stream<Activation> evaluateLatentRelation(Synapse s, TextReference ref, Activation fromAct, PreActivation toPreAct, Direction dir);

    public void createLatentRelation(Activation oAct, Activation fromOriginAct, Activation toOriginAct) {

    }

    public static Relation read(DataInput in, Model m) throws IOException {
        Relation rel = switch (in.readByte()) {
            case 1 -> new BeforeRelation();
            case 2 -> new ContainsRelation();
            case 3 -> new EqualsRelation();
            case 4 -> new NearRelation();
            case 5 -> new LatentProxyRelation();
            default -> null;
        };
        rel.readFields(in, m);
        return rel;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(getRelationType());
    }

    @Override
    public void readFields(DataInput in, Model m) throws IOException {
    }
}
