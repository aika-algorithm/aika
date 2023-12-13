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
package network.aika.debugger.activations.properties.activations;

import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.InhibitoryActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.text.TextReference;

import static network.aika.debugger.AbstractConsole.NOT_SET_STR;


/**
 * @author Lukas Molzberger
 */
public class ActivationPropertyPanel<E extends Activation> extends AbstractPropertyPanel {


    public ActivationPropertyPanel(E act) {
        addTitle(act.getClass().getSimpleName() + " " + "\n");

        addTitle("Identity:", SUB_TITLE_SIZE);
        initIdentitySection(act);

        addTitle("Inference:", SUB_TITLE_SIZE);
        initInferenceSection(act);

        addTitle("Training:", SUB_TITLE_SIZE);
        initTrainingSection(act);
    }

    public void initIdentitySection(E act) {
        addConstant("Id: ", "" + act.getId());
        addConstant("Type: ", "" + act.getType());
        addConstant("Label: ", act.getLabel());

        act.getBindingSignalSlots().forEach(bsSlot ->
                addConstant("BS-Slot: ", "" + bsSlot)
        );

        TextReference gr = act.getTextReference();
        if(gr != null) {
            addConstant("Char Range: ", gr.getCharRange() != null ? "" + gr.getCharRange() : NOT_SET_STR);
            addConstant("Absolute Char Range: ", act.getAbsoluteCharRange() != null ? "" + act.getAbsoluteCharRange() : NOT_SET_STR);
            addConstant("Token Position Range: ", "" + gr.getTokenPosRange());

            Document doc = (Document) act.getDocument();
            addConstant("Covered Text: ", gr.getCharRange() != null ? "" + doc.getTextSegment(gr.getCharRange()) : NOT_SET_STR);
        }
    }

    public void initInferenceSection(E act) {
        addField(act.getValue());

        addConstant("CreationTS: ", "" + act.getCreated());
        addConstant("FiredTS: ", "" + act.getFired());
        addConstant("isNewInstance: ", "" + act.isNewInstance());
    }

    public void initTrainingSection(E act) {
        addField(act.getGradient());
        addField(act.getNetOuterGradient());
        addField(act.getUpdateValue());
        addField(act.getNegUpdateValue());

        if(act.getTemplate() != null)
            addConstant("Template: ", getShortString(act.getTemplate()));

        Activation ati = act.getActiveTemplateInstance();
        if(ati != null)
            addConstant("Active Template-Instance: ", getShortString(ati));
    }

    public static ActivationPropertyPanel create(Activation act) {
        if(act instanceof PatternActivation) {
            return new PatternActivationPropertyPanel((PatternActivation) act);
        } else if(act instanceof BindingActivation) {
            return BindingActivationPropertyPanel.create((BindingActivation) act);
        } else if(act instanceof InhibitoryActivation) {
            return InhibitoryActivationPropertyPanel.create((InhibitoryActivation) act);
        }

        return new ActivationPropertyPanel(act);
    }
}
