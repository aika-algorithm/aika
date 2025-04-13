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
package network.aika.fields.softmax;

import network.aika.type.Type;
import network.aika.type.TypeRegistry;
import network.aika.type.relations.Relation;
import network.aika.type.relations.RelationMany;
import network.aika.type.relations.RelationOne;

import java.util.List;

import static network.aika.fields.softmax.SoftmaxInputType.INPUT_TO_NORM;
import static network.aika.fields.softmax.SoftmaxOutputType.OUTPUT_TO_NORM;

/**
 *
 * @author Lukas Molzberger
 */
public class SoftmaxNormType extends Type {

    public static RelationMany NORM_TO_INPUT = new RelationMany( 0, "NORM_TO_INPUT");
    public static RelationMany NORM_TO_OUTPUT = new RelationMany( 1, "NORM_TO_OUTPUT");

    static {
        NORM_TO_INPUT.setReversed(INPUT_TO_NORM);
        NORM_TO_OUTPUT.setReversed(OUTPUT_TO_NORM);
    }

    public SoftmaxNormType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public SoftmaxNormObj instantiate() {
        return new SoftmaxNormObj(this);
    }

    @Override
    public Relation[] getRelations() {
        return new Relation[] {NORM_TO_INPUT, NORM_TO_OUTPUT};
    }
}
