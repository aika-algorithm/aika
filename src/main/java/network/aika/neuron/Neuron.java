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
package network.aika.neuron;


import network.aika.*;
import network.aika.lattice.Converter;
import network.aika.neuron.activation.Activation;
import network.aika.neuron.activation.Position;
import network.aika.neuron.relation.Relation;
import network.aika.neuron.INeuron.Type;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * The {@code Neuron} class is a proxy implementation for the real neuron implementation in the class {@code INeuron}.
 * Aika uses the provider pattern to store and reload rarely used neurons or logic nodes.
 *
 * @author Lukas Molzberger
 */
public class Neuron extends Provider<INeuron> {

    public static final Neuron MIN_NEURON = new Neuron(null, Integer.MIN_VALUE);
    public static final Neuron MAX_NEURON = new Neuron(null, Integer.MAX_VALUE);


    ReadWriteLock lock = new ReadWriteLock();

    NavigableMap<Integer, Synapse> inputSynapsesById = new TreeMap<>();
    NavigableMap<Synapse, Synapse> activeInputSynapses = new TreeMap<>(Synapse.INPUT_SYNAPSE_COMP);
    NavigableMap<Synapse, Synapse> activeOutputSynapses = new TreeMap<>(Synapse.OUTPUT_SYNAPSE_COMP);


    public Neuron(Model m, int id) {
        super(m, id);
    }


    public Neuron(Model m, INeuron n) {
        super(m, n);
    }


    public String getLabel() {
        return get().getLabel();
    }


    public Type getType() {
        return get().getType();
    }


    public void setType(Type t) {
        get().setType(t);
    }

    public void setActivationFunction(ActivationFunction actF) {
        get().setActivationFunction(actF);
    }



    /**
     * Propagate an input activation into the network.
     *
     * @param doc   The current document
     * @param begin The range begin
     * @param end   The range end
     */
    public Activation addInput(Document doc, int begin, int end) {
        return addInput(doc, begin, end, true);
    }

    public Activation addInput(Document doc, int begin, int end, boolean propagate) {
        return addInput(doc,
                new Activation.Builder()
                        .setRange(begin, end)
                        .setPropagate(propagate)
        );
    }

    /**
     * Propagate an input activation into the network.
     *
     * @param doc   The current document
     * @param inputAct
     */
    public Activation addInput(Document doc, Activation.Builder inputAct) {
        return get(doc).addInput(doc, inputAct);
    }


    public static Neuron init(Neuron n, Builder... inputs) {
        return init(new Document(n.getModel(), ""), n, inputs);
    }


    public static Neuron init(Document doc, Neuron n, Builder... inputs) {
        if(n.init(doc, null, getSynapseBuilders(inputs), getRelationBuilders(inputs))) {
            return n;
        } else return null;
    }


    /**
     * Creates a neuron with the given bias.
     *
     * @param n
     * @param bias
     * @param inputs
     * @return
     */
    public static Neuron init(Neuron n, double bias, Builder... inputs) {
        return init(n, bias, getSynapseBuilders(inputs), getRelationBuilders(inputs));
    }


    /**
     * Creates a neuron with the given bias.
     *
     * @param n
     * @param bias
     * @param inputs
     * @return
     */
    public static Neuron init(Document doc, Neuron n, double bias, Builder... inputs) {
        return init(doc, n, bias, getSynapseBuilders(inputs), getRelationBuilders(inputs));
    }



    /**
     * Initializes a neuron with the given bias.
     *
     * @param n
     * @param bias
     * @param synapseBuilders
     * @param relationBuilders
     * @return
     */
    public static Neuron init(Neuron n, double bias, Collection<Synapse.Builder> synapseBuilders, Collection<Relation.Builder> relationBuilders) {
        if(n.init(new Document(n.getModel(), ""), bias, synapseBuilders, relationBuilders)) return n;
        return null;
    }


    public static Neuron init(Neuron n, double bias, Collection<Neuron.Builder> inputs) {
        if(n.init(new Document(n.getModel(), ""), bias, getSynapseBuilders(inputs), getRelationBuilders(inputs))) return n;
        return null;
    }

    /**
     * Initializes a neuron with the given bias.
     *
     * @param n
     * @param bias
     * @param synapseBuilders
     * @param relationBuilders
     * @return
     */
    public static Neuron init(Document doc, Neuron n, double bias, Collection<Synapse.Builder> synapseBuilders, Collection<Relation.Builder> relationBuilders) {
        if(n.init(doc, bias, synapseBuilders, relationBuilders)) return n;
        return null;
    }


    public static Neuron init(Document doc, Neuron n, double bias, Collection<Neuron.Builder> inputs) {
        if(n.init(doc, bias, getSynapseBuilders(inputs), getRelationBuilders(inputs))) return n;
        return null;
    }


    private boolean init(Document doc, Double bias, Collection<Synapse.Builder> synapseBuilders, Collection<Relation.Builder> relationBuilders) {
        INeuron n = get();

        if(bias != null) {
            n.setBias(bias);
        }

        ArrayList<Synapse> modifiedSynapses = new ArrayList<>();
        // s.link requires an updated n.biasSumDelta value.
        synapseBuilders.forEach(input -> {
            Synapse s = input.getSynapse(this);
            s.update(doc, input.weight, input.limit);
            modifiedSynapses.add(s);
        });

        modifiedSynapses.forEach(s -> s.link());

        relationBuilders.forEach(input -> input.connect(this));

        n.commit(modifiedSynapses);

        return Converter.convert(doc, n, modifiedSynapses);
    }

    public PassiveInputFunction getPassiveInputFunction() {
        return get().passiveInputFunction;
    }


    public void setPassiveInputFunction(PassiveInputFunction f) {
        get().passiveInputFunction = f;
        model.passiveActivationFunctions.put(id, f);

        for(Synapse s: get().outputSynapses.values()) {
            s.getOutput().get().registerPassiveInputSynapse(s);
        }
    }


    public void setOutputText(String outputText) {
        get().setOutputText(outputText);
    }


    public Synapse getSynapseById(int synapseId) {
        return inputSynapsesById.get(synapseId);
    }

    /**
     * {@code getFinalActivations} is a convenience method to retrieve all activations of the given neuron that
     * are part of the final interpretation. Before calling this method, the {@code doc.process()} needs to
     * be called first. {@code getFinalActivations} requires that the {@code doc.process()} method has been called first.
     *
     * @param doc The current document
     * @return A collection with all final activations of this neuron.
     */
    public Stream<Activation> getActivations(Document doc, boolean onlyFinal) {
        INeuron n = getIfNotSuspended();
        if(n == null) return Stream.empty();
        return n.getActivations(doc, onlyFinal);
    }


    public Stream<Activation> getActivations(Document doc, int slot, Position pos, boolean onlyFinal) {
        INeuron n = getIfNotSuspended();
        if(n == null) return Stream.empty();
        return n.getActivations(doc, slot, pos, onlyFinal);
    }


    public Activation getActivation(Document doc, int begin, int end, boolean onlyFinal) {
        return getActivations(doc, Activation.BEGIN, doc.lookupFinalPosition(begin), onlyFinal)
                .filter(act -> act.lookupSlot(Activation.END).getFinalPosition() == end)
                .findFirst()
                .orElse(null);
    }


    public Synapse selectInputSynapse(Neuron inputNeuron, Predicate<Synapse> filter) {
        lock.acquireWriteLock();
        Synapse synapse = activeInputSynapses.subMap(
                new Synapse(inputNeuron, this, Integer.MIN_VALUE), true,
                new Synapse(inputNeuron, this, Integer.MAX_VALUE), true
        )
                .keySet()
                .stream()
                .filter(filter)
                .findAny()
                .orElse(null);

        lock.releaseWriteLock();
        return synapse;
    }


    void addActiveInputSynapse(Synapse s) {
        lock.acquireWriteLock();
        activeInputSynapses.put(s, s);
        inputSynapsesById.put(s.getId(), s);
        lock.releaseWriteLock();
    }


    void removeActiveInputSynapse(Synapse s) {
        lock.acquireWriteLock();
        activeInputSynapses.remove(s);
        inputSynapsesById.remove(s.getId());
        lock.releaseWriteLock();
    }


    void addActiveOutputSynapse(Synapse s) {
        lock.acquireWriteLock();
        activeOutputSynapses.put(s, s);
        lock.releaseWriteLock();
    }


    void removeActiveOutputSynapse(Synapse s) {
        lock.acquireWriteLock();
        activeOutputSynapses.remove(s);
        lock.releaseWriteLock();
    }


    public String toString() {
        if(this == MIN_NEURON) return "MIN_NEURON";
        if(this == MAX_NEURON) return "MAX_NEURON";

        return super.toString();
    }


    public int getNewSynapseId() {
        return get().getNewSynapseId();
    }


    public void registerSynapseId(int synId) {
        get().registerSynapseId(synId);
    }


    /**
     * Active input synapses are those synapses that are currently available in the main memory.
     *
     * @return
     */
    public Collection<Synapse> getActiveInputSynapses() {
        return activeInputSynapses.values();
    }


    public Collection<Synapse> getActiveOutputSynapses() {
        return activeOutputSynapses.values();
    }


    private static Collection<Synapse.Builder> getSynapseBuilders(Collection<Builder> builders) {
        ArrayList<Synapse.Builder> result = new ArrayList<>();
        for(Builder b: builders) {
            if(b instanceof Synapse.Builder) {
                result.add((Synapse.Builder) b);
            }
        }
        return result;
    }


    private static Collection<Relation.Builder> getRelationBuilders(Collection<Builder> builders) {
        ArrayList<Relation.Builder> result = new ArrayList<>();
        for(Builder b: builders) {
            if(b instanceof Relation.Builder) {
                result.add((Relation.Builder) b);
            }
        }
        return result;
    }


    private static Collection<Synapse.Builder> getSynapseBuilders(Builder... builders) {
        ArrayList<Synapse.Builder> result = new ArrayList<>();
        for(Builder b: builders) {
            if(b instanceof Synapse.Builder) {
                result.add((Synapse.Builder) b);
            }
        }
        return result;
    }


    private static Collection<Relation.Builder> getRelationBuilders(Builder... builders) {
        ArrayList<Relation.Builder> result = new ArrayList<>();
        for(Builder b: builders) {
            if(b instanceof Relation.Builder) {
                result.add((Relation.Builder) b);
            }
        }
        return result;
    }


    public interface Builder {
        void registerSynapseIds(Neuron n);
    }

}