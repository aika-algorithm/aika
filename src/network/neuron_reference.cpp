#include "network/neuron_reference.h"
#include "network/neuron.h" // Include the full neuron.h here to access methods

NeuronReference::NeuronReference(long neuronId, RefType refType) : id(neuronId), refType(refType), neuron(nullptr) {}

NeuronReference::NeuronReference(Neuron* n, RefType refType) : id(n->getId()), refType(refType), neuron(n) {}

long NeuronReference::getId() const {
    return id;
}

Neuron* NeuronReference::getRawNeuron() const {
    return neuron;
}

// This is implementing the template method from the header
// Define the non-template function that will be used
Neuron* NeuronReference::getNeuron(Model* m) {
    if (!neuron) {
        neuron = m->getNeuron(id);
        neuron->increaseRefCount(refType);
    }
    return neuron;
}

// Explicit template specialization for Neuron type
template<>
Neuron* NeuronReference::getNeuron<Neuron>(Model* m) {
    return getNeuron(m);
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