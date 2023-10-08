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

import network.aika.Thought;
import network.aika.elements.Timestamp;
import network.aika.elements.Type;
import network.aika.elements.links.Link;
import network.aika.elements.links.positivefeedbackloop.InnerPositiveFeedbackLink;
import network.aika.elements.links.InputObjectLink;
import network.aika.elements.synapses.FeedbackSynapse;
import network.aika.enums.Scope;
import network.aika.fields.*;
import network.aika.elements.neurons.BindingNeuron;
import network.aika.queue.activation.LinkingOut;
import network.aika.visitor.Visitor;
import network.aika.visitor.operator.SelfRefOperator;
import network.aika.visitor.relations.UnboundDownVisitor;
import network.aika.visitor.types.VisitorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

import static network.aika.elements.Timestamp.NOT_SET;
import static network.aika.elements.Type.BINDING;
import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.func;
import static network.aika.fields.Fields.isTrue;
import static network.aika.utils.Utils.TOLERANCE;

/**
 * @author Lukas Molzberger
 */
public class BindingActivation extends ConjunctiveActivation<BindingNeuron> {

    protected static final Logger log = LoggerFactory.getLogger(BindingActivation.class);

    private boolean isInput;

    protected Timestamp firedUnsuppressed = NOT_SET;

    protected SumField netUnsuppressed;

    protected FieldOutput valueUnsuppressed;


    public BindingActivation(int id, Thought t, BindingNeuron n) {
        super(id, t, n);

        valueUnsuppressed = func(
                this,
                "valueUnsuppressed = f(netUnsuppressed)",
                TOLERANCE,
                netUnsuppressed,
                x -> getActivationFunction().f(x)
        );
    }

    @Override
    public Type getType() {
        return BINDING;
    }

    @Override
    public Field getDefaultNet() {
        return netUnsuppressed;
    }

    @Override
    protected void initNet() {
        netUnsuppressed = new SumField(this, "netUnsuppressed", TOLERANCE);

        super.initNet();

        linkAndConnect(netUnsuppressed, net);
    }

    @Override
    protected void initOnFiredListener() {
        super.initOnFiredListener();

        netUnsuppressed.addListener("onFiredUnsuppressed", (fl, nr, u) -> {
                    if (fl.getInput().exceedsThreshold() && firedUnsuppressed == NOT_SET) {
                        firedUnsuppressed = thought.getCurrentTimestamp();
                        LinkingOut.add(this, true);
                    }
                }
        );
    }

    public SumField getNetUnsuppressed() {
        return netUnsuppressed;
    }

    public FieldOutput getValueUnsuppressed() {
        return valueUnsuppressed;
    }


    @Override
    public boolean isActiveTemplateInstance() {
        return isNewInstance || (
                isTrue(net, 0.0) &&
                        getOutputPatternActivations()
                                .anyMatch(Activation::isFired)
        );
    }

    public Stream<PatternActivation> getOutputPatternActivations() {
        return getInputLinksByType(InnerPositiveFeedbackLink.class)
                .map(Link::getInput)
                .filter(Objects::nonNull);
    }

    public PatternActivation getInputPatternActivation() {
        return (PatternActivation) getInputLinksByType(InputObjectLink.class)
                .map(Link::getInput)
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void connectWeightUpdate() {
        updateValue = new SumField(this, "updateValue", TOLERANCE);

        super.connectWeightUpdate();
    }

    public static boolean isSelfRef(BindingActivation in, BindingActivation out, Scope identityRef) {
        if(in == null)
            return false;

        if (in.isAbstract() && !out.isAbstract())
            return in.isSelfRef((BindingActivation) out.getTemplate(), identityRef);
        else if (!in.isAbstract() && out.isAbstract())
            return out.isSelfRef((BindingActivation) in.getTemplate(), identityRef);
        else
            return in.isSelfRef(out, identityRef);
    }

    private boolean isSelfRef(BindingActivation out, Scope identityRef) {
        if(this == out)
            return true;

        if(log.isDebugEnabled())
            log.debug("Start checking SelfRef for (" + toKeyString() + ", " + out.toKeyString() + ")");

        SelfRefOperator op = new SelfRefOperator(out);
        new UnboundDownVisitor(
                thought,
                VisitorType.getSelfRefVisitorType(identityRef),
                op
        ).start(this);

        if(log.isDebugEnabled())
            log.debug("Finished checking SelfRef for (" + toKeyString() + ", " + out.toKeyString() + ") : " + op.isSelfRef());

        return op.isSelfRef();
    }

    @Override
    public void patternVisit(Visitor v, Link lastLink, int depth) {
        super.patternVisit(v, lastLink, depth);
        v.up(this, depth);
    }

    @Override
    public void patternCatVisit(Visitor v, Link lastLink, int depth) {
        if(v.isDown()) {
            v.setReferenceAct(this);
            super.patternCatVisit(v, lastLink, depth);
        } else {
            CategoryActivation cAct = getCategoryActivation();
            CategoryActivation refCAct = v.getReferenceAct().getCategoryActivation();

            if(cAct != null && cAct == refCAct)
                super.patternCatVisit(v, lastLink, depth);
        }
    }

    @Override
    protected void initInactiveLinks() {
        neuron.getInputSynapsesByType(FeedbackSynapse.class)
                .forEach(s ->
                        s.initDummyLink(this)
                );
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

    @Override
    public void disconnect() {
        if(netUnsuppressed != null)
            netUnsuppressed.disconnectAndUnlinkInputs(false);

        super.disconnect();
    }
}
