#include "network/synapse.h"
#include "network/types/synapse_type.h"
#include "network/direction.h" // Include the full NetworkDirection definition
#include "network/linker.h"
#include <iostream>
#include <stdexcept>
#include <limits>

// Need to add the missing SynapseType* type member to Synapse class
// This will be a temporary fix until the proper header update is done

Synapse::Synapse(SynapseType* type) : Object(type), synapseId(0), input(nullptr), output(nullptr), propagable(false), pairedInputSynapseId(-1), pairedOutputSynapseId(-1) {}

Synapse::Synapse(SynapseType* type, Neuron* input, Neuron* output)
    : Object(type), synapseId(0),
      input(new NeuronReference(input, RefType::SYNAPSE_IN)), 
      output(new NeuronReference(output, RefType::SYNAPSE_OUT)), 
      propagable(false), pairedInputSynapseId(-1), pairedOutputSynapseId(-1)
{
    initFields();
    link(input->getModel(), input, output);
}

RelatedObjectIterable* Synapse::followManyRelation(Relation* rel) const {
    throw std::runtime_error("Invalid Relation: " + rel->getRelationLabel());
}

Object* Synapse::followSingleRelation(const Relation* rel) const {
    if (rel->getRelationLabel() == "SELF") {
        return const_cast<Synapse*>(this);
    } else if (rel->getRelationLabel() == "INPUT") {
        return getInput();
    } else if (rel->getRelationLabel() == "OUTPUT") {
        return getOutput();
    } else {
        throw std::runtime_error("Invalid Relation for Synapse: " + rel->getRelationLabel());
    }
}

int Synapse::getSynapseId() const {
    return synapseId;
}

void Synapse::setSynapseId(int synapseId) {
    this->synapseId = synapseId;
}

int Synapse::getPairedInputSynapseId() const {
    return pairedInputSynapseId;
}

void Synapse::setPairedInputSynapseId(int pairedInputSynapseId) {
    this->pairedInputSynapseId = pairedInputSynapseId;
}

int Synapse::getPairedOutputSynapseId() const {
    return pairedOutputSynapseId;
}

void Synapse::setPairedOutputSynapseId(int pairedOutputSynapseId) {
    this->pairedOutputSynapseId = pairedOutputSynapseId;
}

Synapse* Synapse::getPairedSynapse() const {
    SynapseType* synapseType = static_cast<SynapseType*>(getType());
    SynapseType* pairedSynapseType = synapseType->getPairedSynapseType();
    
    if (!pairedSynapseType) {
        return nullptr;
    }
    
    // Find the paired synapse instance that connects the paired input neuron to the same output neuron
    Neuron* outputNeuron = getOutput();
    if (!outputNeuron) {
        return nullptr;
    }
    
    // Look through the output neuron's input synapses to find the paired one
    for (Synapse* inputSyn : outputNeuron->getInputSynapsesAsStream()) {
        if (inputSyn && inputSyn->getType() == pairedSynapseType) {
            return inputSyn;
        }
    }
    
    return nullptr;
}

std::map<int, BindingSignal*> Synapse::transitionForward(const std::map<int, BindingSignal*>& inputBindingSignals) {
    std::map<int, BindingSignal*> outputTransitions;
    SynapseType* synapseType = static_cast<SynapseType*>(getType());
    
    // Iterate over input binding signals and map each one forward
    for (const auto& pair : inputBindingSignals) {
        int fromType = pair.first;
        BindingSignal* bindingSignal = pair.second;
        
        int toType = synapseType->mapTransitionForward(fromType);
        if (toType != -1) {
            outputTransitions[toType] = bindingSignal;
        }
    }
    
    return outputTransitions;
}

std::map<int, BindingSignal*> Synapse::transitionBackward(const std::map<int, BindingSignal*>& outputBindingSignals) {
    std::map<int, BindingSignal*> inputTransitions;
    SynapseType* synapseType = static_cast<SynapseType*>(getType());
    
    // Iterate over output binding signals and map each one backward
    for (const auto& pair : outputBindingSignals) {
        int toType = pair.first;
        BindingSignal* bindingSignal = pair.second;
        
        int fromType = synapseType->mapTransitionBackward(toType);
        if (fromType != -1) {
            inputTransitions[fromType] = bindingSignal;
        }
    }
    
    return inputTransitions;
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
    if (Linker::matchBindingSignals(output, bindingSignals)) {
        // TODO
        Linker::linkIncoming(output, input);
    }
    
    return static_cast<SynapseType*>(getType())
        ->getLinkType()
        ->instantiate(this, input, output);
}

bool Synapse::hasLink(Activation* input, Activation* output) const {
    if (!input || !output) {
        return false;
    }
    
    // Check if the input activation has an output link to the output activation via this synapse
    auto outputLinks = input->getOutputLinks(const_cast<Synapse*>(this));
    for (Link* link : outputLinks) {
        if (link && link->getOutput() == output && link->getSynapse() == this) {
            return true;
        }
    }
    
    return false;
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