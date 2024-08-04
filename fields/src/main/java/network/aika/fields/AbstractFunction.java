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

import network.aika.fields.link.FixedFieldLink;
import network.aika.fields.link.FixedFieldInputs;

/**
 * @author Lukas Molzberger
 */
public abstract class AbstractFunction<O extends Obj> extends Field<O, FixedFieldInputs, FixedFieldLink> {

    public AbstractFunction(int numArgs) {
        super(new FixedFieldInputs(numArgs));
    }

    protected abstract double computeUpdate(FixedFieldLink fl, double u);

    @Override
    public void receiveUpdate(FixedFieldLink fl, double u) {
        double update = computeUpdate(fl, u);

        if(interceptor != null) {
            interceptor.receiveUpdate(update, true);
            return;
        }

        if(update == 0.0)
            return;

        triggerUpdate(update);
    }
}
