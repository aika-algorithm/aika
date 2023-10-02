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
package network.aika.debugger.activations.properties;

import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.activations.*;
import network.aika.text.Document;
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
        addConstant("Label: ", act.getLabel());

        TextReference gr = act.getGroundRef();
        if(gr != null) {
            addConstant("Char Range: ", gr.getCharRange() != null ? "" + gr.getCharRange() : NOT_SET_STR);
            addConstant("Absolute Char Range: ", act.getAbsoluteCharRange() != null ? "" + act.getAbsoluteCharRange() : NOT_SET_STR);
            addConstant("Token Position Range: ", "" + gr.getTokenPosRange());

            Document doc = (Document) act.getThought();
            addConstant("Covered Text: ", gr.getCharRange() != null ? "" + doc.getTextSegment(gr.getCharRange()) : NOT_SET_STR);
        }
    }

    public void initInferenceSection(E act) {
        addField(act.getValue());
        addField(act.getNet());
        addField(act.getNetPreAnneal());
        addConstant("CreationTS: ", "" + act.getCreated());
        addConstant("FiredTS: ", "" + act.getFired());
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
        } else if(act instanceof OuterInhibitoryActivation) {
            return new OuterInhibitoryActivationPropertyPanel((OuterInhibitoryActivation) act);
        } else if(act instanceof InnerInhibitoryActivation) {
            return new InnerInhibitoryActivationPropertyPanel((InnerInhibitoryActivation) act);
        }

        return new ActivationPropertyPanel(act);
    }
}
