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
package network.aika.fielddefs;


import java.util.function.BiConsumer;

/**
 * @author Lukas Molzberger
 */
public class FieldLinkDefinition {

    private Path objectPath;

    private FieldOutputDefinition in;

    private FieldInputDefinition out;

    boolean propagateUpdates;

    public FieldLinkDefinition(Path path, FieldOutputDefinition in, int arg, FieldInputDefinition out) {
        this.objectPath = path;
        this.in = in;
        this.out = out;
    }

    public static <O extends FieldObjectDefinition> FieldLinkDefinition link(O o, BiConsumer<O, Path> pathProvider, String inLabel, Integer arg, String outLabel) {
        Path objectPath = new Path();
        pathProvider.accept(o, objectPath);

        FieldOutputDefinition in = o.getFieldDef(inLabel);
        FieldInputDefinition out = objectPath.getToObject().getFieldDef(outLabel);

        FieldLinkDefinition fl = new FieldLinkDefinition(objectPath, in, arg, out);
        out.addInput(fl);
        in.addOutput(fl);
        return fl;
    }

    public static <O extends FieldObjectDefinition> FieldLinkDefinition link(O o, BiConsumer<O, Path> pathProvider, String inLabel, String outLabel) {
        return link(o, pathProvider, inLabel, null, outLabel);
    }


    public static <O extends FieldObjectDefinition> void linkAll(O o, BiConsumer<O, Path> pathProvider, String inLabel, String... outLabels) {
        for(String outLabel : outLabels) {
            if(o != null) {
                link(o, pathProvider, inLabel, 0, outLabel);
            }
        }
    }

    public void setPropagateUpdates(boolean propagateUpdates) {
        this.propagateUpdates = propagateUpdates;
    }
}
