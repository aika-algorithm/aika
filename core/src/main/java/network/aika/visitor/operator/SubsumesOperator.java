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
import network.aika.elements.activations.types.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.relations.Relation;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.Scope;
import network.aika.enums.direction.Direction;
import network.aika.visitor.DownVisitor;
import network.aika.visitor.UpVisitor;


/**
 * @author Lukas Molzberger
 */
public class SubsumesOperator implements Operator {

    private PatternActivation target;

    private PatternActivation startBS;

    private boolean subsumes;

    public SubsumesOperator(PatternActivation target, PatternActivation startBS) {
        this.target = target;
        this.startBS = startBS;
    }

    @Override
    public PatternActivation getBindingSignal() {
        return startBS;
    }

    public static boolean subsumes(Scope s, PatternActivation a, PatternActivation b) {
        SubsumesOperator op = new SubsumesOperator(b, a);
        new DownVisitor(a.getDocument(), op)
                .start(a, s);

        return op.subsumes;
    }

    @Override
    public void check(Activation bsAct, Scope s, int depth) {
        if(bsAct == target)
            subsumes = true;
    }

    @Override
    public void visitorCheck(UpVisitor v, Link lastLink, Activation act, Scope s) {
    }
}
