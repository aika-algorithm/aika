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
package network.aika.debugger.properties;

import network.aika.enums.direction.Direction;
import network.aika.fields.link.AbstractFieldLink;
import network.aika.fields.link.FieldLink;
import network.aika.fields.FieldOutput;

import java.awt.*;

import static network.aika.debugger.properties.FieldOutputProperty.createFieldProperty;
import static network.aika.enums.direction.Direction.INPUT;


/**
 * @author Lukas Molzberger
 */
public class FieldLinkProperty {

    protected AbstractFieldLink fieldLink;
    protected Container parent;

    protected AbstractProperty fieldProperty;

    public FieldLinkProperty(Container parent, FieldLink fl, Direction dir) {
        fieldLink = fl;
        this.parent = parent;

        if(dir == INPUT) {
            fieldProperty = createFieldProperty(parent, fl.getInput(), true, fl);
        } else {
            fieldProperty = createFieldProperty(parent, (FieldOutput) fl.getOutput(), true, fl);
        }
    }

    public void addField(int pos, Insets insets) {
        if(fieldProperty != null)
            fieldProperty.addField(pos, insets);
    }
}
