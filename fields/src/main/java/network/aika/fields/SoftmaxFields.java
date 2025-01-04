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

import network.aika.fields.defs.FieldDefinition;
import network.aika.type.Obj;
import network.aika.type.Type;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import static network.aika.fields.Division.div;
import static network.aika.fields.ExponentialFunction.exp;
import static network.aika.fields.SumField.sum;

/**
 * @author Lukas Molzberger
 */
public class SoftmaxFields<
        IT extends Type<IT, IO>,
        IO extends Obj<IT, IO>,
        NT extends Type<NT, NO>,
        NO extends Obj<NT, NO>,
        OT extends Type<OT, OO>,
        OO extends Obj<OT, OO>
        > {

    public static <
            IT extends Type<IT, IO>,
            IO extends Obj<IT, IO>,
            NT extends Type<NT, NO>,
            NO extends Obj<NT, NO>,
            OT extends Type<OT, OO>,
            OO extends Obj<OT, OO>
            > SoftmaxFields<IT, IO, NT, NO, OT, OO> softmax(
                    IT inputRef,
                    NT normRef,
                    OT outputRef,
                    RelationMany<NT, NO, IT, IO> normInputRelation,
                    RelationOne<OT, OO, NT, NO> normOutputRelation,
                    RelationOne<OT, OO, IT, IO> inputRelation,
                    String name
    ) {
        return new SoftmaxFields<>(
                inputRef,
                normRef,
                outputRef,
                normInputRelation,
                normOutputRelation,
                inputRelation,
                name);
    }

    private final ExponentialFunction<IT, IO> inputs;
    private final FieldDefinition<NT, NO> norm;
    private final FieldDefinition<OT, OO> outputs;


    public SoftmaxFields(
            IT inputRef,
            NT normRef,
            OT outputRef,
            RelationMany<NT, NO, IT, IO> normInputRelation,
            RelationOne<OT, OO, NT, NO> normOutputRelation,
            RelationOne<OT, OO, IT, IO> inputRelation,
            String name
    ) {
        inputs = exp(inputRef, name);
        norm = sum(normRef, name)
                .in(normInputRelation, inputs);

        outputs = div(outputRef, name)
                .in(inputRelation, inputs, 0)
                .in(normOutputRelation, norm, 1);
    }

    public ExponentialFunction<IT, IO> getInputs() {
        return inputs;
    }

    public FieldDefinition<NT, NO> getNorm() {
        return norm;
    }

    public FieldDefinition<OT, OO> getOutputs() {
        return outputs;
    }
}
