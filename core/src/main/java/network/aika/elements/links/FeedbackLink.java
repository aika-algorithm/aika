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
import network.aika.elements.activations.BindingActivation;
import network.aika.elements.activations.ConjunctiveActivation;
import network.aika.elements.synapses.FeedbackSynapse;
import network.aika.visitor.Visitor;

/**
 * @author Lukas Molzberger
 *
 */
public abstract class FeedbackLink<S extends FeedbackSynapse, IA extends Activation<?>, OA extends ConjunctiveActivation<?>> extends ConjunctiveLink<S, IA, OA> {

    protected long[] visited;

    public FeedbackLink(S s, IA input, OA output) {
        super(s, input, output);
    }

    @Override
    public void bindingVisit(Visitor v, int state, int depth) {
        if(checkVisited(v))
            return;

        super.bindingVisit(v, state, depth);
    }

    @Override
    public void patternVisit(Visitor v, int state, int depth) {
        if(checkVisited(v))
            return;

        super.patternVisit(v, state, depth);
    }

    @Override
    public void innerInhibVisit(Visitor v, int state, int depth) {
        if(checkVisited(v))
            return;

        super.innerInhibVisit(v, state, depth);
    }

    @Override
    public void outerInhibVisit(Visitor v, int state, int depth) {
        if(checkVisited(v))
            return;

        super.outerInhibVisit(v, state, depth);
    }

    @Override
    public void innerSelfRefVisit(Visitor v, int state, int depth) {
        if(checkVisited(v))
            return;

        super.innerInhibVisit(v, state, depth);
    }

    @Override
    public void outerSelfRefVisit(Visitor v, int state, int depth) {
        if(checkVisited(v))
            return;

        super.outerInhibVisit(v, state, depth);
    }

    @Override
    public void patternCatVisit(Visitor v, int state, int depth) {
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
}
