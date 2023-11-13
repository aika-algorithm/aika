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
package network.aika.elements.activations.types;

import network.aika.Document;
import network.aika.elements.activations.Activation;
import network.aika.elements.activations.BindingSignalSlot;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.links.ConjunctiveLink;
import network.aika.elements.links.Link;
import network.aika.elements.links.types.InnerPositiveFeedbackLink;
import network.aika.elements.links.types.InputObjectLink;
import network.aika.enums.Transition;
import network.aika.fields.*;
import network.aika.elements.neurons.types.BindingNeuron;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.operator.SelfRefOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

import static network.aika.enums.Transition.INPUT;
import static network.aika.enums.Transition.SAME;
import static network.aika.fields.Fields.isTrue;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class BindingActivation extends ConjunctiveActivation<BindingNeuron> {

    protected static final Logger log = LoggerFactory.getLogger(BindingActivation.class);

    private BindingSignalSlot inputBS = new BindingSignalSlot(INPUT);
    private BindingSignalSlot sameBS = new BindingSignalSlot(SAME);

    private boolean isInput;

    public BindingActivation(int id, Document doc, BindingNeuron n) {
        super(id, doc, n);
    }

    @Override
    public PatternActivation getBindingSignal(Transition t) {
        return getBSSlot(t)
                .getBindingSignal();
    }

    @Override
    public void registerBindingSignalSlot(ConjunctiveLink l) {
        BindingSignalSlot bsSlot = getBSSlot(l.getSynapse().getTransition());
        if(bsSlot == null)
            return;

        PatternActivation bs = l.retrieveBindingSignal();
        bsSlot.connectBindingSignal(bs, true);
    }

    public BindingSignalSlot getBSSlot(Transition t) {
        return switch (t) {
            case SAME -> sameBS;
            case INPUT -> inputBS;
            default -> null;
        };
    }

    @Override
    public boolean isActiveTemplateInstance() {
        return isNewInstance || (
                !isAbstract() && isTrue(net, 0.0) && sameBS.isSet()
        );
    }

    public PatternActivation getInputPatternActivation() {
        return getInputLinksByType(InputObjectLink.class)
                .map(Link::getInput)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void connectWeightUpdate() {
        updateValue = new SumField(this, "updateValue", TOLERANCE);

        super.connectWeightUpdate();
    }

    public static boolean isSelfRef(BindingActivation in, BindingActivation out) {
        if(in == null)
            return false;

        if (in.isAbstract() && !out.isAbstract())
            return in.isSelfRef((BindingActivation) out.getTemplate());
        else if (!in.isAbstract() && out.isAbstract())
            return out.isSelfRef((BindingActivation) in.getTemplate());
        else
            return in.isSelfRef(out);
    }

    private boolean isSelfRef(BindingActivation out) {
        if(this == out)
            return true;

        if(log.isDebugEnabled())
            log.debug("Start checking SelfRef for (" + toKeyString() + ", " + out.toKeyString() + ")");

        SelfRefOperator op = new SelfRefOperator(out, new Transition[]{INPUT}, new Transition[]{SAME});
        new DownVisitor(
                doc,
                op
        ).start(this);

        if(log.isDebugEnabled())
            log.debug("Finished checking SelfRef for (" + toKeyString() + ", " + out.toKeyString() + ") : " + op.isSelfRef());

        return op.isSelfRef();
    }

    public boolean isInput() {
        return isInput;
    }

    public void setInput(boolean input) {
        isInput = input;
    }

    public void updateBias(double u) {
        getNet().receiveUpdate(null, false, u);
    }
}
