#include "network/neuron_reference.h"

NeuronReference::NeuronReference(long neuronId, RefType refType) : id(neuronId), refType(refType), neuron(nullptr) {}

NeuronReference::NeuronReference(Neuron* n, RefType refType) : id(n->getId()), refType(refType), neuron(n) {}

long NeuronReference::getId() const {
    return id;
}

Neuron* NeuronReference::getRawNeuron() const {
    return neuron;
}

Neuron* NeuronReference::getNeuron(Model* m) {
    if (!neuron) {
        neuron = m->getNeuron(id);
        neuron->increaseRefCount(refType);
    }
    return neuron;
}

void NeuronReference::suspendNeuron() {
    if (neuron) {
        neuron->decreaseRefCount(refType);
        neuron = nullptr;
    }
}

std::string NeuronReference::toString() const {
    return "p(" + (neuron ? neuron->toString() : std::to_string(id) + ":SUSPENDED") + ")";
}

std::string NeuronReference::toKeyString() const {
    return "p(" + (neuron ? neuron->toKeyString() : std::to_string(id) + ":SUSPENDED") + ")";
} 