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
package network.aika.elements.neurons;

import network.aika.Model;
import network.aika.Thought;
import network.aika.elements.activations.PatternActivation;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.elements.neurons.relations.Relation;
import network.aika.enums.direction.Direction;
import network.aika.fields.SumField;
import network.aika.elements.activations.LatentRelationActivation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.stream.Stream;

import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
public class LatentRelationNeuron extends BindingNeuron {

    private Relation relation;

    public LatentRelationNeuron(Model m) {
        super(m);

        setAllowTraining(false);
    }

    public LatentRelationNeuron(Model m, Relation rel) {
        this(m);
        this.relation = rel;
    }

    public Relation getRelation() {
        return relation;
    }

    @Override
    protected SumField initBias() {
        return new SumField(this, "bias", TOLERANCE)
                .setQueued(getThought(), TRAINING);
    }

    @Override
    public LatentRelationActivation createActivation(Thought t) {
        return new LatentRelationActivation(t.createActivationId(), t, this);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        relation.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        relation = Relation.read(in, m);
    }
}