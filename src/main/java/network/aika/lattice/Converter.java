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
package network.aika.lattice;

import network.aika.AbstractNode;
import network.aika.Document;
import network.aika.Model;
import network.aika.lattice.refinement.RefValue;
import network.aika.lattice.refinement.Refinement;
import network.aika.lattice.refinement.RelationsMap;
import network.aika.neuron.INeuron;
import network.aika.neuron.INeuron.SynapseSummary;
import network.aika.neuron.Synapse;
import network.aika.neuron.relation.Relation;

import java.util.*;

import static network.aika.neuron.Synapse.State.CURRENT;

/**
 * Converts the synapse weights of a neuron into a boolean logic representation of this neuron.
 *
 * @author Lukas Molzberger
 */
public class Converter {

    public static int MAX_AND_NODE_SIZE = 10;


    public static Comparator<Synapse> SYNAPSE_COMP = (s1, s2) -> {
        int r = Boolean.compare(
                s2.linksAnyOutput() || s2.isIdentity(),
                s1.linksAnyOutput() || s1.isIdentity()
        );
        if (r != 0) return r;
        r = Double.compare(s2.getWeight(), s1.getWeight());
        if (r != 0) return r;
        return Integer.compare(s1.getId(), s2.getId());
    };

    private INeuron neuron;
    private Model model;
    private Document doc;
    private OrNode outputNode;
    private Collection<Synapse> modifiedSynapses;


    public static boolean convert(Document doc, INeuron neuron, Collection<Synapse> modifiedSynapses) {
        return new Converter(doc, neuron, modifiedSynapses).convert();
    }


    private Converter(Document doc, INeuron neuron, Collection<Synapse> modifiedSynapses) {
        this.doc = doc;
        this.neuron = neuron;
        this.model = neuron.getModel();
        this.modifiedSynapses = modifiedSynapses;
    }


    private boolean convert() {
        outputNode = neuron.getInputNode().get();

        SynapseSummary ss = neuron.getSynapseSummary();

        if(neuron.getTotalBias(CURRENT) + ss.getPosDirSum() + ss.getPosRecSum() <= 0.0) {
            outputNode.removeParents(doc);
            return false;
        }


        switch(neuron.getType()) {
            case EXCITATORY:
                if(hasOnlyWeakSynapses()) {
                    convertWeakSynapses();
                } else {
                    convertConjunction();
                }
                break;
            case INHIBITORY:
                convertDisjunction();
                break;
            case INPUT:
                break;
        }

        return true;
    }

    private void convertConjunction() {
        SynapseSummary ss = neuron.getSynapseSummary();

        outputNode.removeParents(doc);

        List<Synapse> candidates = prepareCandidates();

        double sum = 0.0;
        NodeContext nodeContext = null;
        double remainingSum = ss.getPosDirSum();
        int i = 0;
        boolean optionalInputMode = false;

        for (Synapse s : candidates) {
            double v = s.getMaxInputValue();
            boolean belowThreshold = sum + v + remainingSum + ss.getPosRecSum() + ss.getPosPassiveSum() + neuron.getTotalBias(CURRENT) <= 0.0;
            if (belowThreshold) {
                return;
            }

            if(sum + remainingSum - v + ss.getPosRecSum() + ss.getPosPassiveSum() + neuron.getTotalBias(CURRENT) > 0.0) {
                optionalInputMode = true;
            }

            if (!optionalInputMode) {
                NodeContext nlNodeContext = expandNode(nodeContext, s);
                if (nlNodeContext == null) {
                    return;
                }
                nodeContext = nlNodeContext;

                remainingSum -= v;
                sum += v;
                i++;
            } else {
                NodeContext nlNodeContext = expandNode(nodeContext, s);
                if (nlNodeContext != null) {
                    outputNode.addInput(nlNodeContext.getSynapseIds(), doc, nlNodeContext.node, true);
                    remainingSum -= v;
                }
            }

            final boolean sumOfSynapseWeightsAboveThreshold = sum + ss.getPosRecSum() + ss.getPosPassiveSum() + neuron.getTotalBias(CURRENT) > 0.0;
            final boolean maxAndNodesReached = i >= MAX_AND_NODE_SIZE;
            if (sumOfSynapseWeightsAboveThreshold || maxAndNodesReached) {
                break;
            }
        }

        if(nodeContext != null && !optionalInputMode) {
            outputNode.addInput(nodeContext.getSynapseIds(), doc, nodeContext.node, true);
        }
    }


    private boolean hasOnlyWeakSynapses() {
        for(Synapse s: neuron.getInputSynapses()) {
            if(!s.isWeak(CURRENT)) {
                return false;
            }
        }
        return true;
    }


    private void convertWeakSynapses() {
        TreeSet<Synapse> synapsesSortedByWeight = new TreeSet<>((s1, s2) -> {
            int r = Double.compare(s2.getWeight(), s1.getWeight());
            if(r != 0) return r;
            return SYNAPSE_COMP.compare(s1, s2);
        });

        synapsesSortedByWeight.addAll(neuron.getInputSynapses());

        double sum = 0.0;
        for (Synapse s : synapsesSortedByWeight) {
            if (!s.isRecurrent()) {
                sum += s.getWeight();

                NodeContext nlNodeContext = expandNode(null, s);
                outputNode.addInput(nlNodeContext.getSynapseIds(), doc, nlNodeContext.node, true);

                if(sum > neuron.getBias()) {
//                    break;  // siehe: AIKA-1
                }
            }
        }
    }


    private void convertDisjunction() {
        for (Synapse s : modifiedSynapses) {
            if (!s.isRecurrent() && !s.isWeak(CURRENT)) {
                NodeContext nlNodeContext = expandNode(null, s);

                outputNode.addInput(nlNodeContext.getSynapseIds(), doc, nlNodeContext.node, false);
            }
        }
    }


    private List<Synapse> prepareCandidates() {
        Synapse syn = getStrongestSynapse(neuron.getInputSynapses());
        if(syn == null) {
            return Collections.EMPTY_LIST;
        }

        TreeSet<Integer> alreadyCollected = new TreeSet<>();
        ArrayList<Synapse> selectedCandidates = new ArrayList<>();
        TreeMap<Integer, Synapse> relatedSyns = new TreeMap<>();
        while(syn != null && selectedCandidates.size() < MAX_AND_NODE_SIZE) {
            relatedSyns.remove(syn.getId());
            selectedCandidates.add(syn);
            alreadyCollected.add(syn.getId());
            for(Map.Entry<Integer, Relation> me: syn.getRelations().entrySet()) {
                Integer relId = me.getKey();
                Relation rel = me.getValue();
                if(!alreadyCollected.contains(relId)) {
                    Synapse rs = syn.getOutput().getSynapseById(relId);
                    if(rs != null) {
                        relatedSyns.put(relId, rs);
                    }
                }
            }

            syn = getStrongestSynapse(relatedSyns.values());
        }

        return selectedCandidates;
    }


    private Synapse getStrongestSynapse(Collection<Synapse> synapses) {
        Synapse maxSyn = null;
        for(Synapse s: synapses) {
            if(!s.isNegative(CURRENT) && !s.isRecurrent() && !s.isInactive() && !s.getInput().get().isPassiveInputNeuron()) {
                if(maxSyn == null || SYNAPSE_COMP.compare(maxSyn, s) > 0) {
                    maxSyn = s;
                }
            }
        }
        return maxSyn;
    }

    private NodeContext expandNode(NodeContext nc, Synapse s) {
        return expandNodeInternal(nc, s);
    }

    private NodeContext expandNodeInternal(NodeContext nc, Synapse s) {
        if (nc == null) {
            NodeContext nln = new NodeContext();
            nln.node = s.getInput().get().getOutputNode().get();

            nln.offsets = new Synapse[] {s};
            return nln;
        } else {
            Relation[] relations = new Relation[nc.offsets.length];
            for(int i = 0; i < nc.offsets.length; i++) {
                Synapse linkedSynapse = nc.offsets[i];
                relations[i] = s.getRelationById(linkedSynapse.getId());
            }

            NodeContext nln = new NodeContext();
            nln.offsets = new Synapse[nc.offsets.length + 1];
            Refinement ref = new Refinement(new RelationsMap(relations), s.getInput().get().getOutputNode());
            RefValue rv = nc.node.expand(doc, ref);
            if(rv == null) {
                return null;
            }

            nln.node = rv.child.get(doc);

            for (int i = 0; i < nc.offsets.length; i++) {
                nln.offsets[rv.offsets[i]] = nc.offsets[i];
            }
            for (int i = 0; i < nln.offsets.length; i++) {
                if (nln.offsets[i] == null) {
                    nln.offsets[i] = s;
                }
            }


            return nln;
        }
    }


    private class NodeContext {
        Node node;

        Synapse[] offsets;

        int[] getSynapseIds() {
            int[] result = new int[offsets.length];
            for(int i = 0; i < result.length; i++) {
                result[i] = offsets[i].getId();
            }
            return result;
        }
    }
}
