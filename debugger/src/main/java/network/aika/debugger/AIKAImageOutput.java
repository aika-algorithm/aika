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
package network.aika.debugger;

import network.aika.debugger.activations.ActivationViewManager;
import network.aika.debugger.neurons.NeuronViewManager;
import network.aika.debugger.stepmanager.DummyStepManager;
import network.aika.Document;

/**
 * @author Lukas Molzberger
 */
public class AIKAImageOutput implements AIKADebugManager {

    private ActivationViewManager actViewManager;
    private NeuronViewManager neuronViewManager;


    public AIKAImageOutput(Document doc) {
        actViewManager = new ActivationViewManager(doc, null, this);
        actViewManager.setStepManager(new DummyStepManager());

        neuronViewManager = new NeuronViewManager(doc.getModel(), null);

        actViewManager.getView();
    }

    public ActivationViewManager getActivationViewManager() {
        return actViewManager;
    }

    public NeuronViewManager getNeuronViewManager() {
        return neuronViewManager;
    }

    @Override
    public void showNeuronView() {
    }
}
