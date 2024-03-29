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
package network.aika.elements.neurons.types;

import network.aika.Model;
import network.aika.Document;
import network.aika.elements.neurons.NeuronProvider;
import network.aika.elements.neurons.NeuronType;
import network.aika.elements.neurons.RefType;
import network.aika.elements.relations.Relation;
import network.aika.fields.Field;
import network.aika.fields.SumField;
import network.aika.elements.activations.types.LatentRelationActivation;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import static network.aika.ActivationFunction.RECTIFIED_HYPERBOLIC_TANGENT;
import static network.aika.elements.Type.BINDING;
import static network.aika.queue.Phase.TRAINING;
import static network.aika.utils.Utils.TOLERANCE;

/**
 *
 * @author Lukas Molzberger
 */
@NeuronType(
        type = BINDING,
        activationFunction = RECTIFIED_HYPERBOLIC_TANGENT,
        bindingSignalSlots = {}
)
public class LatentRelationNeuron extends BindingNeuron {

    private Relation relation;

    public LatentRelationNeuron(NeuronProvider np) {
        super(np);
    }

    public LatentRelationNeuron(Model m, RefType rt) {
        super(m, rt);

        setAllowTraining(false);
    }

    public LatentRelationNeuron(Model m, Relation rel, RefType rt) {
        this(m, rt);
        this.relation = rel;
    }

    public Relation getRelation() {
        return relation;
    }

    @Override
    protected Field initBias() {
        return new SumField(this, "bias", TOLERANCE)
                .setQueued(getQueue(), TRAINING, false);
    }

    @Override
    public LatentRelationActivation createActivation(Document doc) {
        return new LatentRelationActivation(doc.createActivationId(), doc, this);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);

        relation.write(out);
    }

    @Override
    public void readFields(DataInput in, Model m) throws Exception {
        super.readFields(in, m);

        relation = Relation.read(in, m);
    }
}