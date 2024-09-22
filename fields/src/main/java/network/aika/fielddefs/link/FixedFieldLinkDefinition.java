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

import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;
import network.aika.fields.link.FieldLink;
import network.aika.fields.link.FixedFieldLink;

/**
 * @author Lukas Molzberger
 */
public class FixedFieldLinkDefinition extends FieldLinkTypeDefinition {

    private Integer arg;

    public FixedFieldLinkDefinition(Integer arg, boolean propagateUpdates) {
        super(propagateUpdates);
        this.arg = arg;
    }

    public FieldLink instantiate(FieldOutput input, FieldInput output) {
        return new FixedFieldLink(input, arg, output);
    }

    public Integer getArg() {
        return arg;
    }
}
