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
package network.aika.debugger.activations.properties.links;

import network.aika.debugger.properties.AbstractPropertyPanel;
import network.aika.elements.activations.Activation;
import network.aika.elements.links.*;
import network.aika.elements.links.types.InhibitoryLink;
import network.aika.enums.direction.Direction;
import network.aika.queue.steps.LinkUpdate;


/**
 * @author Lukas Molzberger
 */
public class LinkPropertyPanel<E extends Link> extends AbstractPropertyPanel {


    public LinkPropertyPanel(E l) {
        addTitle(l.getClass().getSimpleName());

        addTitle("Input:",SUB_TITLE_SIZE);
        initInputIdentitySection(l);

        addTitle("Output:",SUB_TITLE_SIZE);
        initOutputIdentitySection(l);

        addTitle("Inference:",SUB_TITLE_SIZE);
        initInferenceSection(l);

        addTitle("Inference Out:",SUB_TITLE_SIZE - 2);
        initInferenceOutSection(l);

        addTitle("Training:",SUB_TITLE_SIZE);
        initTrainingSection(l);
    }


    public void initInputIdentitySection(E l) {
        addConstant("Type: ", "" + l.getInputType());
        addConstant("Activation: ", getShortString(l.getInput()));
        addConstant("IsActive: ", "" + l.isInputSideActive());

        LinkUpdate lu = l.getLinkUpdateStep(Direction.INPUT);
        addConstant(
                "Link Update: ",
                "CS:" + lu.getCurrentState() +
                        "TS:" + lu.getTargetState() +
                        " isQueued:" + lu.isQueued()
        );
    }

    public void initOutputIdentitySection(E l) {
        addConstant("Type: ", "" + l.getOutputType());
        addConstant("Activation: ", getShortString(l.getOutput()));
        addConstant("IsActive: ", "" + l.isOutputSideActive());

        LinkUpdate lu = l.getLinkUpdateStep(Direction.OUTPUT);
        addConstant(
                "Link Update: ",
                "CS:" + lu.getCurrentState() +
                        "TS:" + lu.getTargetState() +
                        " isQueued:" + lu.isQueued()
        );
    }

    public void initInferenceSection(E l) {
        addField(l.getInputValue());
        addField(l.getWeightedInput());
    }

    public void initInferenceOutSection(E l) {
        Activation oAct = l.getOutput();

        addField(oAct.getValue());
        addField(oAct.getNet(l.outputState()));
    }

    public void initTrainingSection(E l) {
        addField(l.getGradient());
    }

    public static LinkPropertyPanel create(Link l) {
        if(l instanceof ConjunctiveLink) {
            return ConjunctiveLinkPropertyPanel.create((ConjunctiveLink) l);
        } else if(l instanceof InhibitoryLink) {
            return new InhibitoryLinkPropertyPanel((InhibitoryLink) l);
        }

        return new LinkPropertyPanel(l);
    }
}
