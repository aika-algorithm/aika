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
package network.aika;

import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.meta.exceptions.FailedInstantiationException;
import network.aika.queue.Step;
import network.aika.queue.steps.FeedbackTrigger;
import network.aika.queue.steps.InstantiationTrigger;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class InstantiationModel<I extends InstantiationModel> {

    public static <N extends ConjunctiveNeuron<?, ?>> N lookupInstance(Document doc, N templateN) {
        return (N) templateN.getActivations(doc)
                .stream()
                .findFirst()
                .orElse(null)
                .getActiveTemplateInstance()
                .getNeuron();
    }

    public abstract TemplateModel getTemplateModel();

    public Model getModel() {
        return getTemplateModel().getModel();
    }

    public I instantiate(String label) {
        getTemplateModel().prepareInstantiation();

        getModel()
                .getConfig()
                .setTrainingEnabled(true)
                .setMetaInstantiationEnabled(true);

        Document doc = createDocument(label);

        try {
            InstantiationTrigger.add(doc);

            getTemplateModel().prepareExampleDoc(doc, label);

            doc.process(this::stepFilter);

            mapResults(doc);
        } catch(Exception e) {
            throw new FailedInstantiationException(label, e);
        } finally {
            doc.disconnect();
        }

        return (I) this;
    }

    protected boolean stepFilter(Step s) {
        if(!(s instanceof FeedbackTrigger))
            return true;

        FeedbackTrigger ft = (FeedbackTrigger) s;

        return getTemplateModel().stepFilter(ft.getElement().getNeuron());
    }

    protected abstract Document createDocument(String label);

    protected abstract void mapResults(Document doc);
}
