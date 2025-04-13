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
import network.aika.type.relations.RelationOne;

import java.util.List;

import static network.aika.fields.softmax.SoftmaxInputType.CORRESPONDING_OUTPUT_LINK;
import static network.aika.fields.softmax.SoftmaxNormType.NORM_TO_OUTPUT;

/**
 *
 * @author Lukas Molzberger
 */
public class SoftmaxOutputType extends Type {

    public static RelationOne OUTPUT_TO_NORM = new RelationOne(0, "OUTPUT_TO_NORM");
    public static RelationOne CORRESPONDING_INPUT_LINK = new RelationOne(1, "CORRESPONDING_INPUT_LINK");

    static {
        OUTPUT_TO_NORM.setReversed(NORM_TO_OUTPUT);
        CORRESPONDING_INPUT_LINK.setReversed(CORRESPONDING_OUTPUT_LINK);
    }

    public SoftmaxOutputType(TypeRegistry registry, String name) {
        super(registry, name);
    }

    public SoftmaxOutputObj instantiate(int bsId) {
        return new SoftmaxOutputObj(this, bsId);
    }

    @Override
    public Relation[] getRelations() {
        return new Relation[] {OUTPUT_TO_NORM, CORRESPONDING_INPUT_LINK};
    }
}
