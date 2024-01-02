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
package network.aika.meta;

import network.aika.Document;
import network.aika.Model;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.Neuron;
import network.aika.meta.exceptions.FailedInstantiationException;
import network.aika.queue.Step;
import network.aika.queue.steps.FeedbackTrigger;
import network.aika.queue.steps.InstantiationTrigger;
import network.aika.utils.Writable;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class TemplateModel<T extends TemplateModel> implements Writable {

    public abstract Model getModel();

    public abstract void initTemplateNeurons();

    public abstract T createInstanceModel();

    public abstract boolean stepFilter(Neuron n);

    public abstract void prepareInstantiation();

    public abstract void enable();

    public abstract void disable();

    public abstract void prepareExampleDoc(Document doc, String label);

    public abstract Document createDocument(String label);

    public abstract void mapResults(T templateModel, Document doc);

    protected boolean stepFilter(Step s) {
        if(!(s instanceof FeedbackTrigger))
            return true;

        FeedbackTrigger ft = (FeedbackTrigger) s;

        return stepFilter(ft.getElement().getNeuron());
    }

    public T instantiate(String label) {
        enable();

        prepareInstantiation();

        getModel()
                .getConfig()
                .setTrainingEnabled(true)
                .setMetaInstantiationEnabled(true);

        TemplateModel im = createInstanceModel();

        Document doc = im.createDocument(label);

        try {
            InstantiationTrigger.add(doc);

            prepareExampleDoc(doc, label);

            doc.process(this::stepFilter);

            im.mapResults(this, doc);

            disable();
        } catch(Exception e) {
            throw new FailedInstantiationException(label, e);
        } finally {
            doc.disconnect();
        }

        disable();

        return (T) im;
    }

    protected static <N extends ConjunctiveNeuron<?, ?>> N lookupInstance(Document doc, N templateN) {
        return (N) templateN.getActivations(doc)
                .stream()
                .findFirst()
                .orElse(null)
                .getActiveTemplateInstance()
                .getNeuron();
    }
}
