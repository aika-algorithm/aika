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
package network.aika.elements.links;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.PositiveFeedbackSynapse;
import network.aika.fields.AbstractFunction;
import network.aika.fields.Field;
import network.aika.fields.IdentityFunction;
import network.aika.fields.MaxField;
import network.aika.visitor.Visitor;

import static network.aika.fields.FieldLink.linkAndConnect;
import static network.aika.fields.Fields.mul;
import static network.aika.fields.Fields.scale;

/**
 *
 * @author Lukas Molzberger
 */
public abstract class PositiveFeedbackLink<S extends PositiveFeedbackSynapse, IA extends Activation<?>, OA extends ConjunctiveActivation<?>>
        extends ConjunctiveLink<S, IA, OA> {

    protected long[] visited;

    protected AbstractFunction inputGradient;

    public PositiveFeedbackLink(S s, IA input, OA output) {
        super(s, input, output);
    }

    @Override
    public void visit(Visitor v, int state, int depth) {
        if(checkVisited(v))
            return;

        super.visit(v, state, depth);
    }

    private boolean checkVisited(Visitor v) {
        if(visited == null)
            visited = new long[2];

        int dir = v.getDirectionIndex();
        if(visited[dir] == v.getV())
            return true;

        visited[dir] = v.getV();
        return false;
    }

    @Override
    protected void initInputValue() {
        inputValue = new MaxField(this, "input-value-ft");

        linkAndConnect(getFeedbackTrigger(), 0, inputValue);
    }

    protected Field getFeedbackTrigger() {
        return getDocument().getFeedbackTrigger();
    }

    @Override
    public void initFromTemplate(Link template) {
        super.initFromTemplate(template);
        synapse.initDummyLink(output);
    }

    @Override
    protected void connectGradientFields() {
        super.connectGradientFields();

        inputGradient = new IdentityFunction(this, "input gradient");

        scale(
                this,
                "updateValue = lr * in.grad * f'(out.net)",
                getConfig().getLearnRate(output.getNeuron().isAbstract()),
                mul(
                        this,
                        "in.gradient * f'(out.net)",
                        inputGradient,
                        output.getNetOuterGradient()
                ),
                output.getUpdateValue()
        );

        if(input != null)
            linkAndConnect(input.getGradient(), 0, inputGradient);
    }
}
