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
public class ArgumentFieldLink<A> extends FieldLink {

    private final A argumentRef;

    public ArgumentFieldLink(FieldOutput input, int arg, A argRef, FieldInput<ArgumentFieldLink<A>> output) {
        super(input, arg, output);

        argumentRef = argRef;
    }

    public A getArgumentRef() {
        return argumentRef;
    }

    public static <A> ArgumentFieldLink<A> link(FieldOutput in, int arg, A argRef, FieldInput<ArgumentFieldLink<A>> out) {
        ArgumentFieldLink<A> fl = new ArgumentFieldLink(in, arg, argRef, out);
        out.addInput(fl);
        in.addOutput(fl);
        return fl;
    }

    public static <A> ArgumentFieldLink<A> linkAndConnect(FieldOutput in, A argRef, FieldInput<ArgumentFieldLink<A>> out) {
        ArgumentFieldLink<A> fl = link(in, out.size(), argRef, out);

        fl.connect(true);
        return fl;
    }
}
