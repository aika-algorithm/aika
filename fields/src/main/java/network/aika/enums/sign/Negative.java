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
package network.aika.enums.sign;

import network.aika.fielddefs.FieldDefinition;
import network.aika.fielddefs.FieldTag;
import network.aika.fielddefs.Type;
import network.aika.fields.Obj;
import network.aika.fields.InvertFunction;

import static network.aika.fielddefs.link.FieldLinkTypeDefinition.argLink;


/**
 *
 * @author Lukas Molzberger
 */
public class Negative implements Sign {

    public static final FieldTag NEGATION = () -> 0;

    @Override
    public Sign invert() {
        return POS;
    }

    @Override
    public <D extends Type<D, O>, O extends Obj<D, O>> FieldDefinition<D, O> getValue(D ref, FieldDefinition<D, O> v) {
        return InvertFunction.invert(ref, NEGATION)
                .in(v.getFieldOutput(), v.getFieldOutput().getFieldTag(), argLink(0));
    }

    @Override
    public int index() {
        return 1;
    }

    public String toString() {
        return "NEG";
    }
}
