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
package network.aika.visitor.binding;

import network.aika.elements.activations.PatternActivation;
import network.aika.elements.links.Link;
import network.aika.elements.synapses.Synapse;
import network.aika.enums.direction.Direction;

import static network.aika.enums.direction.Direction.OUTPUT;

/**
 * @author Lukas Molzberger
 */
public class RelationUpBindingVisitor extends RelationBindingVisitor {

    private PatternActivation downBindingSource;
    private PatternActivation upBindingSource;

    private Synapse relation;

    private Direction relationDir;

    protected RelationUpBindingVisitor(
            RelationBindingVisitor downVisitor,
            PatternActivation downBindingSource,
            PatternActivation upBindingSource,
            Synapse relation,
            Direction relDir
    ) {
        super(downVisitor, downBindingSource);
        this.downBindingSource = downBindingSource;
        this.upBindingSource = upBindingSource;
        this.relation = relation;
        this.relationDir = relDir;

        assert downBindingSource != null;
        assert upBindingSource != null;
        assert relation != null;
    }

    public PatternActivation getDownBindingSource() {
        return downBindingSource;
    }

    public PatternActivation getUpBindingSource() {
        return upBindingSource;
    }

    @Override
    public boolean compatible(Synapse from, Synapse to) {
        return relationDir == OUTPUT || relation == to;
    }

    @Override
    public void createLatentRelation(Link l) {
        relation.createLatentRelation(
                l.getOutput(),
                relationDir.getInput(downBindingSource, upBindingSource),
                relationDir.getOutput(downBindingSource, upBindingSource)
        );
    }
}