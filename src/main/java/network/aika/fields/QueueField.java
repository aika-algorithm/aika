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


import network.aika.steps.FieldStep;
import network.aika.steps.InnerQueue;
import network.aika.steps.Step;

/**
 * @author Lukas Molzberger
 */
public class QueueField extends Field {

    private boolean isQueued;
    private FieldStep step;

    public QueueField(InnerQueue e, String label) {
        super(e, label);
        step = new FieldStep(e, this);
    }

    public QueueField(InnerQueue e, String label, double initialValue) {
        super(e, label, initialValue);
        step = new FieldStep(e, this);
    }

    public void setStep(FieldStep s) {
        this.step = s;
    }

    public void triggerUpdate() {
        if(!isQueued) {
            Step.add(step);
            isQueued = true;
        }
    }

    public void process() {
        isQueued = false;
        triggerInternal();
    }
}
