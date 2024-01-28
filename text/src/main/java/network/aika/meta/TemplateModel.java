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
import network.aika.callbacks.InstantiationCallback;
import network.aika.debugger.AIKADebugger;
import network.aika.elements.activations.Activation;
import network.aika.elements.neurons.Neuron;
import network.aika.meta.exceptions.FailedInstantiationException;
import network.aika.queue.steps.InstantiationTrigger;
import network.aika.utils.Writable;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class TemplateModel<T extends TemplateModel> implements InstantiationCallback, Writable {

    protected String label;

    protected T parent;

    public abstract Model getModel();

    public abstract void initTemplateNeurons();

    public abstract void initOuterSynapses();

    public abstract T createInstanceModel(String label, TemplateModel instM);

    public abstract void prepareInstantiation();

    public abstract void enable();

    public abstract void disable();

    public abstract void prepareExampleDoc(Document doc, String label);

    public Document createDocument(String l) {
        Document doc = new Document(getModel(), l);

        boolean flag = false;

        if(flag)
            AIKADebugger.createAndShowGUI()
                    .setDocument(doc);


        doc.setInstantiationCallback(this);
        return doc;
    }

    public abstract void postProcess(Document doc);

    public abstract void mapResults(Document doc);

    public T instantiate(String l, TemplateModel instM) {
        enable();

        prepareInstantiation();

        getModel()
                .getConfig()
                .setTrainingEnabled(true)
                .setMetaInstantiationEnabled(true);

        TemplateModel im = createInstanceModel(l, instM);
        im.parent = this;

        Document doc = im.createDocument(l);

        try {
            InstantiationTrigger.add(doc);

            prepareExampleDoc(doc, l);

            doc.process();

            postProcess(doc);

            im.mapResults(doc);

            im.disable();
        } catch(Exception e) {
            throw new FailedInstantiationException(label, e);
        } finally {
            doc.disconnect();
        }

        disable();

        return (T) im;
    }

    @Override
    public void onInstantiation(Activation tAct, Activation iAct) {
        parent.generateLabel(tAct, iAct, label);
    }

    protected void generateLabel(Activation tAct, Activation iAct, String l) {
        iAct.getNeuron().setLabel(
                tAct.getLabel().replace(label, l + getLabelPostfix())
        );
    }

    protected abstract String getLabelPostfix();

    protected static <N extends Neuron<?, ?>> N lookupInstance(Document doc, N templateN) {
        return (N) templateN.getActivations(doc)
                .stream()
                .findFirst()
                .orElse(null)
                .getActiveTemplateInstance()
                .getNeuron();
    }
}
