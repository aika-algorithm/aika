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
package network.aika.elements.activations;

import network.aika.elements.activations.types.PatternActivation;
import network.aika.enums.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Lukas Molzberger
 */
public class BindingSignalSlot {

    protected static final Logger log = LoggerFactory.getLogger(BindingSignalSlot.class);


    int sourcesCount;

    PatternActivation bindingSignal;

    Transition bsType;

    public BindingSignalSlot(Transition bsType) {
        this.bsType = bsType;
    }

    public boolean isSet() {
        return bindingSignal != null;
    }

    public PatternActivation getBindingSignal() {
        return bindingSignal;
    }

    public void connectBindingSignal(PatternActivation bs, boolean state) {
        if(bs == null)
            return;

        if(isSet() && bindingSignal != bs) {
            log.warn(bsType + " Binding-Signal is reset from:" + bindingSignal + " to:" + bs);
        }

        if(state) {
            sourcesCount++;
            bindingSignal = bs;
        } else {
            sourcesCount--;
            if(sourcesCount <= 0)
                this.bindingSignal = null;
        }
    }

    public String toString() {
        return "(" + bsType + ") : " + (bindingSignal != null ? bindingSignal : "--") + " (" + sourcesCount + ")";
    }
}
