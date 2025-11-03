#include "network/synapse.h"
#include "network/types/synapse_type.h"
#include "network/types/neuron_type.h"
#include "network/direction.h" // Include the full NetworkDirection definition
#include "network/linker.h"
#include <iostream>
#include <stdexcept>
#include <limits>


Synapse::Synapse(SynapseType* type) : Object(type), synapseId(0), input(nullptr), output(nullptr), propagable(false), pairedSynapseInputSide(nullptr), pairedSynapseOutputSide(nullptr) {}

Synapse::Synapse(SynapseType* type, Neuron* input, Neuron* output)
    : Object(type), synapseId(output->getNewSynapseId()),
      input(new NeuronReference(input, RefType::SYNAPSE_IN)), 
      output(new NeuronReference(output, RefType::SYNAPSE_OUT)), 
      propagable(false), pairedSynapseInputSide(nullptr), pairedSynapseOutputSide(nullptr)
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
    } else if (rel->getRelationLabel() == "PAIR") {
        return pairedSynapseOutputSide;
    } else if (rel->getRelationLabel() == "PAIR_IN") {
        return pairedSynapseInputSide;
    } else if (rel->getRelationLabel() == "PAIR_OUT") {
        return pairedSynapseOutputSide;
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

Synapse* Synapse::getPairedSynapseInputSide() const {
    return pairedSynapseInputSide;
}

void Synapse::setPairedSynapseInputSide(Synapse* pairedSynapseInputSide) {
    this->pairedSynapseInputSide = pairedSynapseInputSide;
}

Synapse* Synapse::getPairedSynapseOutputSide() const {
    return pairedSynapseOutputSide;
}

void Synapse::setPairedSynapseOutputSide(Synapse* pairedSynapseOutputSide) {
    this->pairedSynapseOutputSide = pairedSynapseOutputSide;
}

BindingSignal** Synapse::transitionForward(const BindingSignal** inputBindingSignals) {
    SynapseType* synapseType = static_cast<SynapseType*>(getType());
    
    // Get the output neuron to determine the number of binding signal slots
    Neuron* outputNeuron = getOutput();
    if (!outputNeuron) {
        return nullptr;
    }
    
    // Allocate output array using the neuron's method
    BindingSignal** outputBindingSignals = outputNeuron->createBindingSignalArray();
    
    // Get input and output neuron array sizes for bounds checking
    Neuron* inputNeuron = getInput();
    int inputBSSlots = inputNeuron->getNumberOfBSSlots();
    int outputBSSlots = outputNeuron->getNumberOfBSSlots();
    
    // Map each input binding signal forward
    for (int fromSlot = 0; fromSlot < inputBSSlots; fromSlot++) {
        if (inputBindingSignals[fromSlot] != nullptr) {
            int toSlot = synapseType->mapTransitionForward(fromSlot);
            if (toSlot >= 0 && toSlot < outputBSSlots) {
                outputBindingSignals[toSlot] = const_cast<BindingSignal*>(inputBindingSignals[fromSlot]);
            }
        }
    }
    
    return outputBindingSignals;
}

BindingSignal** Synapse::transitionBackward(const BindingSignal** outputBindingSignals) {
    SynapseType* synapseType = static_cast<SynapseType*>(getType());
    
    // Get the input neuron to determine the number of binding signal slots
    Neuron* inputNeuron = getInput();
    if (!inputNeuron) {
        return nullptr;
    }
    
    // Allocate input array using the neuron's method
    BindingSignal** inputBindingSignals = inputNeuron->createBindingSignalArray();
    
    // Get input and output neuron array sizes for bounds checking
    int inputBSSlots = inputNeuron->getNumberOfBSSlots();
    
    Neuron* outputNeuron = getOutput();
    int outputBSSlots = outputNeuron->getNumberOfBSSlots();
    
    // Map each output binding signal backward
    for (int toSlot = 0; toSlot < outputBSSlots; toSlot++) {
        if (outputBindingSignals[toSlot] != nullptr) {
            int fromSlot = synapseType->mapTransitionBackward(toSlot);
            if (fromSlot >= 0 && fromSlot < inputBSSlots) {
                inputBindingSignals[fromSlot] = const_cast<BindingSignal*>(outputBindingSignals[toSlot]);
            }
        }
    }
    
    return inputBindingSignals;
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