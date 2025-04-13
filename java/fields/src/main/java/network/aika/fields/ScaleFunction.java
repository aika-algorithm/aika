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

import network.aika.fields.defs.FieldLinkDefinitionOutputSide;
import network.aika.type.Type;
import network.aika.type.Obj;

/**
 * @author Lukas Molzberger
 */
public class ScaleFunction extends AbstractFunctionDefinition {

    public static ScaleFunction scale(Type ref, String name, double scale) {
        return new ScaleFunction(
                ref,
                name,
                scale
        );
    }

    private final double scale;

    public ScaleFunction(Type ref, String name, double scale) {
        super(ref, name, 1);

        this.scale = scale;
    }

    @Override
    protected double computeUpdate(Obj obj, FieldLinkDefinitionOutputSide fl, double u) {
        return scale * u;
    }
}
