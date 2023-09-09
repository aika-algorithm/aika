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
package network.aika.debugger.neurons;

import network.aika.debugger.AbstractGraphManager;
import network.aika.debugger.AbstractParticleLink;
import network.aika.elements.neurons.ConjunctiveNeuron;
import network.aika.elements.neurons.Neuron;
import network.aika.elements.synapses.Synapse;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.springbox.NodeParticle;

/**
 * @author Lukas Molzberger
 */
public class NeuronGraphManager extends AbstractGraphManager<Neuron, Synapse, NeuronParticle> {

    public NeuronGraphManager(Graph graph) {
        super(graph);

        k = STANDARD_DISTANCE_X;
        K1Init = 0.06f;
        K1Final = 0.01f;
        K2 = 0.005f;
    }

    @Override
    protected Long getAikaNodeId(Neuron n) {
        return n.getId();
    }

    @Override
    protected NeuronParticle createParticle(Neuron key, Node node) {
        return new NeuronParticle(this, node, key);
    }

    @Override
    protected ParticleSynapse createParticleLink(Synapse synapse, Edge edge) {
        return new ParticleSynapse(synapse, edge, this);
    }

    @Override
    protected AbstractParticleLink getParticleLink(Synapse synapse) {
        return getParticle(synapse.getInput())
                .getOutputParticleLink(
                        synapse.getOutput().getId()
                );
    }

    @Override
    public ParticleSynapse<Synapse> lookupParticleLink(Synapse s) {
        return (ParticleSynapse) lookupParticleLink(s, s.getInput(), s.getOutput());
    }

    public Edge getEdge(Synapse s) {
        return getEdge(s.getInput(), s.getOutput());
    }

    @Override
    public Synapse getLink(Edge e) {
        Neuron<?, ?> in = getAikaNode(e.getSourceNode());
        Neuron<?, ?> on = getAikaNode(e.getTargetNode());

        if(on instanceof ConjunctiveNeuron<?, ?>) {
            return on.getInputSynapsesAsStream()
                    .filter(s -> s.getInput() == in)
                    .findAny()
                    .orElse(null);
        } else {
            return in.getOutputSynapsesAsStream()
                    .filter(s -> s.getOutput() == on)
                    .findAny()
                    .orElse(null);
        }
    }

    @Override
    public NodeParticle newNodeParticle(String id) {
        return getParticle(
                getNode(id)
        );
    }
}
