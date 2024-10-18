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
package network.aika.fielddefs.link;


import network.aika.fielddefs.*;
import network.aika.fields.Field;
import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.Obj;
import network.aika.fields.link.FieldLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @author Lukas Molzberger
 */
public class InputFieldLinkDefinition<T extends Type<T, O>, O extends Obj<T, O>> extends FieldLinkDefinition {

    private static final Logger log = LoggerFactory.getLogger(InputFieldLinkDefinition.class);

    private Function<O, FieldOutput> pathProvider;

    public InputFieldLinkDefinition(Function<O, FieldOutput> pathProvider, FieldLinkTypeDefinition typeDefinition) {
        super(typeDefinition);
        this.pathProvider = pathProvider;
    }

    public void link(Function<O, FieldOutput> pathProvider) {
        this.pathProvider = pathProvider;
    }

    public Function<O, FieldOutput> getPathProvider() {
        return pathProvider;
    }

    public void instantiate(Field f) {
        FieldOutput input = pathProvider.apply((O) f.getObject());
        instantiateAndLink(input, f);
    }

    public FieldLink instantiateAndLink(FieldOutput input, FieldInput output) {
        if(input == null || output == null) {
            log.warn("Unable to instantiate field link " + this + " because input or output is null.");
            return null;
        }

        FieldLink fl = instantiate(input, output);
        output.getInputs().addInput(fl);
        input.addOutput(fl);

        return fl;
    }
}
