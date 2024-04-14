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
package network.aika.elements.links.types;


/**
 * @author Lukas Molzberger
 */
/*
public class InhibitoryLink extends DisjunctiveLink<InhibitorySynapse, BindingActivation, InhibitoryActivation> {

    protected FieldOutput value;

    protected Field net;

    public InhibitoryLink(InhibitorySynapse inhibitorySynapse, BindingActivation input, InhibitoryActivation output) {
        super(inhibitorySynapse, input, output);

        net = new SumField(this, "net", null);
        linkAndConnect(weightedInput, net);
        linkAndConnect(output.getNeuron().getBias(), net)
                .setPropagateUpdates(false);

        value = Fields.func(
                this,
                "value = f(net)",
                TOLERANCE,
                net,
                x -> output.getActivationFunction().f(x)
        ).setQueued(getQueue(), NEGATIVE_FEEDBACK, false);

        InhibitoryActivation.connectFields(
                Stream.of(this),
                output.getAllNegativeFeedbackLinks()
        );
    }

    public void connectFields(NegativeFeedbackLink out) {
        if(isSelfRef(getInput(), out.getOutput()))
            return;

        linkAndConnect(getNet(), out.getInputValue());
    }

    private boolean isSelfRef(BindingActivation input, BindingActivation output) {
        if(input == output)
            return true;

        if(input.getTemplate() == output || output.getTemplate() == input)
            return true;

        if(!input.getBindingSignalSlot(SAME).isSet() ||
                !output.getBindingSignalSlot(SAME).isSet())
            return false;

        PatternActivation inputBS = input.getBindingSignal(SAME);
        PatternActivation outputBS = output.getBindingSignal(SAME);

        return subsumes(SAME, inputBS, outputBS) ||
                subsumes(SAME, outputBS, inputBS);
    }

    @Override
    public void disconnect() {
        super.disconnect();
        net.unlinkInputs();
    }

    public FieldOutput getValue() {
        return value;
    }

    public FieldOutput getNet() {
        return net;
    }
}
*/