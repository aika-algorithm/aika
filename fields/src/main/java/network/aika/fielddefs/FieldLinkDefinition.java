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


/**
 * @author Lukas Molzberger
 */
public class FieldLinkDefinition {

    boolean propagateUpdates;

    public FieldLinkDefinition(FieldOutputDefinition in, int arg, FieldInputDefinition out) {

    }

    public static FieldLinkDefinition link(FieldOutputDefinition in, int arg, FieldInputDefinition out) {
        FieldLinkDefinition fl = new FieldLinkDefinition(in, arg, out);
        out.addInput(fl);
        in.addOutput(fl);
        return fl;
    }

    public static FieldLinkDefinition link(FieldOutputDefinition in, FieldInputDefinition out) {
        return link(in, out.size(), out);
    }


    public static void linkAll(FieldOutputDefinition in, FieldInputDefinition... out) {
        assert in != null;

        for(FieldInputDefinition o : out) {
            if(o != null) {
                link(in, 0, o);
            }
        }
    }

    public void setPropagateUpdates(boolean propagateUpdates) {
        this.propagateUpdates = propagateUpdates;
    }
}
