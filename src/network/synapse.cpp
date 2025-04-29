#include "network/synapse.h"

Synapse::Synapse(SynapseDefinition* type) : type(type), synapseId(0), input(nullptr), output(nullptr), propagable(false) {}

Synapse::Synapse(SynapseDefinition* type, Neuron* input, Neuron* output) : type(type), synapseId(0), input(new NeuronReference(input, RefType::SYNAPSE_IN)), output(new NeuronReference(output, RefType::SYNAPSE_OUT)), propagable(false) {}

int Synapse::getSynapseId() const {
    return synapseId;
}

void Synapse::setSynapseId(int synapseId) {
    this->synapseId = synapseId;
}

std::map<BSType*, BindingSignal*> Synapse::transitionForward(const std::map<BSType*, BindingSignal*>& inputBindingSignals) {
    // Implementation for transitioning forward
    return std::map<BSType*, BindingSignal*>(); // Placeholder
}

Synapse* Synapse::setPropagable(Model* m, bool propagable) {
    this->propagable = propagable;
    return this;
}

bool Synapse::isPropagable() const {
    return propagable;
}

void Synapse::setModified(Model* m) {
    // Implementation for setting modified
}

void Synapse::setInput(Neuron* n) {
    input = new NeuronReference(n, RefType::SYNAPSE_IN);
}

void Synapse::setOutput(Neuron* n) {
    output = new NeuronReference(n, RefType::SYNAPSE_OUT);
}

Synapse* Synapse::link(Model* m, Neuron* input, Neuron* output) {
    // Implementation for linking
    return this;
}

void Synapse::link(Model* m) {
    // Implementation for linking
}

void Synapse::unlinkInput(Model* m) {
    // Implementation for unlinking input
}

void Synapse::unlinkOutput(Model* m) {
    // Implementation for unlinking output
}

Link* Synapse::createLink(Activation* input, Activation* output) {
    // Implementation for creating a link
    return nullptr; // Placeholder
}

Link* Synapse::createLink(Activation* input, const std::map<BSType*, BindingSignal*>& bindingSignals, Activation* output) {
    // Implementation for creating a link with binding signals
    return nullptr; // Placeholder
}

Direction* Synapse::getStoredAt() const {
    return nullptr; // Placeholder
}

NeuronReference* Synapse::getInputRef() const {
    return input;
}

NeuronReference* Synapse::getOutputRef() const {
    return output;
}

Neuron* Synapse::getInput() const {
    return input->getRawNeuron();
}

Neuron* Synapse::getInput(Model* m) const {
    return input->getNeuron(m);
}

Neuron* Synapse::getOutput() const {
    return output->getRawNeuron();
}

Neuron* Synapse::getOutput(Model* m) const {
    return output->getNeuron(m);
}

void Synapse::deleteSynapse(Model* m) {
    // Implementation for deleting a synapse
}

Queue* Synapse::getQueue() const {
    return nullptr; // Placeholder
}

std::string Synapse::toString() const {
    return "Synapse: " + std::to_string(synapseId);
}

std::string Synapse::toKeyString() const {
    return "SynapseKey: " + std::to_string(synapseId);
} 