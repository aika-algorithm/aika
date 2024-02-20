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
package network.aika.fields.link;

import network.aika.fields.FieldInput;
import network.aika.fields.FieldOutput;

/**
 * @author Lukas Molzberger
 */
public class FieldLink extends AbstractFieldLink<FieldInput> {

    public FieldLink(FieldOutput input, int arg, FieldInput output) {
        super(input, arg, output);
    }

    public static FieldLink linkAndConnect(FieldOutput in, Object argRef, FieldInput out) {
        FieldLink fl = link(in, out.getNextArg(), argRef, out);

        fl.connect(true);
        return fl;
    }

    public static FieldLink linkAndConnect(FieldOutput in, FieldInput out) {
        FieldLink fl = link(in, out.getNextArg(), out);

        fl.connect(true);
        return fl;
    }

    public static FieldLink link(FieldOutput in, FieldInput out) {
        return link(in, out.getNextArg(), out);
    }

    public static FieldLink linkAndConnect(FieldOutput in, int arg, FieldInput out) {
        FieldLink fl = link(in, arg, out);
        fl.connect(true);
        return fl;
    }

    public static FieldLink link(FieldOutput in, int arg, FieldInput out) {
        FieldLink fl = new FieldLink(in, arg, out);
        out.addInput(fl);
        in.addOutput(fl);
        return fl;
    }

    public static FieldLink link(FieldOutput in, int arg, Object argRef, FieldInput out) {
        ArgumentFieldLink fl = new ArgumentFieldLink(in, arg, argRef, out);
        out.addInput(fl);
        in.addOutput(fl);
        return fl;
    }

    public static void linkAndConnectAll(FieldOutput in, FieldInput... out) {
        assert in != null;

        for(FieldInput o : out) {
            if(o != null) {
                link(in, 0, o)
                        .connect(true);
            }
        }
    }

    public static void linkAll(FieldOutput in, FieldInput... out) {
        assert in != null;

        for(FieldInput o : out) {
            if(o != null) {
                link(in, 0, o);
            }
        }
    }

    @Override
    public void unlinkOutput() {
        output.removeInput(this);
    }
}
