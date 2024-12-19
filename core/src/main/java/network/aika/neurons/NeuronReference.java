package network.aika.neurons;

import network.aika.Model;

public class NeuronReference {

    private final long id;
    private final RefType refType;

    private Neuron neuron;


    public NeuronReference(long neuronId, RefType refType) {
        this.id = neuronId;
        this.refType = refType;
    }

    public NeuronReference(Neuron n, RefType refType) {
        this.id = n.getId();
        this.refType = refType;
        this.neuron = n;
    }

    public long getId() {
        return id;
    }

    public Neuron getRawNeuron() {
        return neuron;
    }

    public synchronized <N extends Neuron> N getNeuron(Model m) {
        if (neuron == null) {
            neuron = m.getNeuron(id);
            neuron.increaseRefCount(refType);
        }

        return (N) neuron;
    }

    public void suspendNeuron() {
        assert neuron != null;

        neuron.decreaseRefCount(refType);
        neuron = null;
    }

    public String toString() {
        return "p(" + (neuron != null ? neuron : id + ":" + "SUSPENDED") + ")";
    }

    public String toKeyString() {
        return "p(" + (neuron != null ? neuron.toKeyString() : id + ":" + "SUSPENDED") + ")";
    }
}
