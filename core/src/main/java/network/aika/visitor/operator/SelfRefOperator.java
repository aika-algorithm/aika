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
package network.aika.visitor.operator;

import network.aika.elements.activations.Activation;
import network.aika.elements.activations.types.BindingActivation;
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Transition;
import network.aika.enums.direction.Direction;
import network.aika.statistic.SampleSpace;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.UpVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static network.aika.enums.Transition.INPUT;
import static network.aika.enums.Transition.SAME;

/**
 * @author Lukas Molzberger
 */
public class SelfRefOperator implements Operator {

    private static final Logger log = LoggerFactory.getLogger(SelfRefOperator.class);

    private Activation target;

    private Transition[] forbidden;

    private boolean isSelfRef;

    public SelfRefOperator(Activation target, Transition[] forbidden) {
        this.target = target;
        this.forbidden = forbidden;
    }

    public static boolean isSelfRef(BindingActivation in, BindingActivation out) {
        if(in == null)
            return false;

        if (in.isAbstract() && !out.isAbstract())
            return isSelfRefIntern(in, (BindingActivation) out.getTemplate());
        else if (!in.isAbstract() && out.isAbstract())
            return isSelfRefIntern(out, (BindingActivation) in.getTemplate());
        else
            return isSelfRefIntern(in, out);
    }

    private static boolean isSelfRefIntern(BindingActivation in, BindingActivation out) {
        if(in == out)
            return true;

        if(log.isDebugEnabled())
            log.debug("Start checking SelfRef for (" + in.toKeyString() + ", " + out.toKeyString() + ")");

        SelfRefOperator op = new SelfRefOperator(out, new Transition[]{SAME});
        new DownVisitor(
                in.getDocument(),
                op
        ).start(in);

        if(log.isDebugEnabled())
            log.debug("Finished checking SelfRef for (" + in.toKeyString() + ", " + out.toKeyString() + ") : " + op.isSelfRef);

        return op.isSelfRef;
    }

    @Override
    public boolean checkForbiddenTransitions(Link l, Direction dir) {
        for(Transition ft: forbidden)
            if (l.getSynapse().isForbiddenTransition(ft))
                return false;

        return true;
    }

    @Override
    public boolean check(Activation bsAct, int state, int depth) {
        return bsAct instanceof PatternActivation;
    }

    @Override
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, int state) {
        if(act == target)
            isSelfRef = true;
    }

    @Override
    public void relationCheck(Synapse relSyn, Activation relAct, Direction relDir) {
        throw new UnsupportedOperationException();
    }
}
