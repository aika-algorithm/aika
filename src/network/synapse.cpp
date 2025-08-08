#include "network/synapse.h"
#include "network/synapse_type.h"
#include "network/direction.h" // Include the full NetworkDirection definition
#include <iostream>
#include <stdexcept>
#include <limits>

// Need to add the missing SynapseType* type member to Synapse class
// This will be a temporary fix until the proper header update is done

Synapse::Synapse(SynapseType* type) : Object(type), synapseId(0), input(nullptr), output(nullptr), propagable(false) {}

Synapse::Synapse(SynapseType* type, Neuron* input, Neuron* output)
    : Object(type), synapseId(0),
      input(new NeuronReference(input, RefType::SYNAPSE_IN)), 
      output(new NeuronReference(output, RefType::SYNAPSE_OUT)), 
      propagable(false) 
{
    link(input->getModel(), input, output);
}

Object* Synapse::followSingleRelation(const Relation* rel) const {
    if (rel->getRelationLabel() == "SELF") {
        return const_cast<ConjunctiveSynapse*>(this);
    } else if (rel->getRelationLabel() == "INPUT") {
        return getInput();
    } else if (rel->getRelationLabel() == "OUTPUT") {
        return getOutput();
    } else {
        throw std::runtime_error("Invalid Relation for ConjunctiveSynapse: " + rel->getRelationLabel());
    }
}

int Synapse::getSynapseId() const {
    return synapseId;
}

void Synapse::setSynapseId(int synapseId) {
    this->synapseId = synapseId;
}

std::map<int, BindingSignal*> Synapse::transitionForward(const std::map<int, BindingSignal*>& inputBindingSignals) {
    std::map<int, BindingSignal*> outputTransitions;
    auto transitions = static_cast<SynapseType*>(getType())->getTransition();
    
    for (auto t : transitions) {
        auto it = inputBindingSignals.find(t->from());
        if (it != inputBindingSignals.end()) {
            outputTransitions[t->to()] = it->second;
        }
    }
    
    return outputTransitions;
}

Synapse* Synapse::setPropagable(Model* m, bool propagable) {
    if (this->propagable != propagable) {
        input->getNeuron(m)->setModified();
    }
    
    getInput(m)->updatePropagable(output->getNeuron(m), propagable);
    this->propagable = propagable;
    
    return this;
}

bool Synapse::isPropagable() const {
    return propagable;
}

void Synapse::setModified(Model* m) {
    Neuron* n = getStoredAt()->getNeuron(m, this);
    if (n != nullptr) {
        n->setModified();
    }
}

void Synapse::setInput(Neuron* n) {
    input = new NeuronReference(n, RefType::SYNAPSE_IN);
}

void Synapse::setOutput(Neuron* n) {
    output = new NeuronReference(n, RefType::SYNAPSE_OUT);
}

Synapse* Synapse::link(Model* m, Neuron* input, Neuron* output) {
    synapseId = output->getNewSynapseId();
    
    setInput(input);
    setOutput(output);
    
    link(m);
    
    return this;
}

void Synapse::link(Model* m) {
    input->getNeuron(m)->addOutputSynapse(this);
    output->getNeuron(m)->addInputSynapse(this);
}

void Synapse::unlinkInput(Model* m) {
    getInput(m)->removeOutputSynapse(this);
}

void Synapse::unlinkOutput(Model* m) {
    getOutput(m)->removeInputSynapse(this);
}

Link* Synapse::createLink(Activation* input, Activation* output) {
    return createLink(input, transitionForward(input->getBindingSignals()), output);
}

Link* Synapse::createLink(Activation* input, const std::map<int, BindingSignal*>& bindingSignals, Activation* output) {
    if (output->hasConflictingBindingSignals(bindingSignals)) {
        return nullptr;
    } else if (output->hasNewBindingSignals(bindingSignals)) {
        output = output->branch(bindingSignals);
        output->linkIncoming(input);
    }
    
    return static_cast<SynapseType*>(getType())
        ->getLink()
        ->instantiate(this, input, output);
}

NetworkDirection* Synapse::getStoredAt() const {
    return static_cast<SynapseType*>(getType())->getStoredAt();
}

NeuronReference* Synapse::getInputRef() const {
    return input;
}

NeuronReference* Synapse::getOutputRef() const {
    return output;
}

Neuron* Synapse::getInput() const {
    if (!output || !output->getRawNeuron()) {
        return nullptr;
    }
    return getInput(output->getRawNeuron()->getModel());
}

Neuron* Synapse::getInput(Model* m) const {
    if (!input) {
        return nullptr;
    }
    return input->getNeuron(m);
}

Neuron* Synapse::getOutput() const {
    if (!input || !input->getRawNeuron()) {
        return nullptr;
    }
    return getOutput(input->getRawNeuron()->getModel());
}

Neuron* Synapse::getOutput(Model* m) const {
    if (!output) {
        return nullptr;
    }
    return output->getNeuron(m);
}

long Synapse::getCreated() const {
    // Return minimum timestamp value
    return 0; // Using 0 as MIN for now
}

long Synapse::getFired() const {
    // Return maximum timestamp value
    return std::numeric_limits<long>::max(); // Using max long as MAX for now
}

void Synapse::deleteSynapse(Model* m) {
    std::cout << "Delete synapse: " << toString() << std::endl;
    
    if (input) {
        getInput(m)->removeOutputSynapse(this);
    }
    if (output) {
        getOutput(m)->removeInputSynapse(this);
    }
}

Queue* Synapse::getQueue() const {
    return nullptr; // Not implemented yet
}

std::string Synapse::toString() const {
    return getType()->getName() +
        " in:[" + (input ? input->toKeyString() : "X") + "] " +
        " --> " +
        " out:[" + (output ? output->toKeyString() : "X") + "])";
}

std::string Synapse::toKeyString() const {
    return (input ? input->toKeyString() : "X") +
        " --> " +
        (output ? output->toKeyString() : "X");
}