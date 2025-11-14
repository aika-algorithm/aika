#include "network/builders/synapse_type_builder.h"
#include "network/types/synapse_type.h"
#include "network/types/link_type.h"
#include "fields/type_registry.h"


SynapseTypeBuilder::SynapseTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), inputType(nullptr), outputType(nullptr), linkType(nullptr),
      pairingConfigs(), transitions(), inputSideParentType(nullptr), outputSideParentType(nullptr),
      builtInstance(nullptr), isBuilt(false) {
}

SynapseTypeBuilder::~SynapseTypeBuilder() {
    // Note: We don't delete builtInstance here as it's managed by the TypeRegistry
}

std::string SynapseTypeBuilder::getName() const {
    return name;
}

SynapseTypeBuilder& SynapseTypeBuilder::setInput(NeuronType* inputType) {
    this->inputType = inputType;
    return *this;
}

NeuronType* SynapseTypeBuilder::getInput() const {
    return inputType;
}

SynapseTypeBuilder& SynapseTypeBuilder::setOutput(NeuronType* outputType) {
    this->outputType = outputType;
    return *this;
}

NeuronType* SynapseTypeBuilder::getOutput() const {
    return outputType;
}

// Pairing methods
SynapseTypeBuilder& SynapseTypeBuilder::pair(SynapseType* pairedSynapseType) {
    PairingConfig config(pairedSynapseType);
    this->pairingConfigs.push_back(config);

    return *this;
}

SynapseTypeBuilder& SynapseTypeBuilder::pair(SynapseType* pairedSynapseType, int bindingSignalSlot) {
    PairingConfig config(pairedSynapseType, bindingSignalSlot);
    this->pairingConfigs.push_back(config);

    return *this;
}

std::vector<PairingConfig> SynapseTypeBuilder::getPairingConfigs() const {
    return pairingConfigs;
}


SynapseTypeBuilder& SynapseTypeBuilder::addTransition(Transition* transition) {
    if (transition) {
        this->transitions.push_back(transition);
    }
    return *this;
}

std::vector<Transition*> SynapseTypeBuilder::getTransitions() const {
    return transitions;
}

SynapseTypeBuilder& SynapseTypeBuilder::setInputSideParent(SynapseType* parentType) {
    if (!isBuilt) {
        this->inputSideParentType = parentType;
    }
    return *this;
}

SynapseType* SynapseTypeBuilder::getInputSideParent() const {
    return inputSideParentType;
}

SynapseTypeBuilder& SynapseTypeBuilder::setOutputSideParent(SynapseType* parentType) {
    if (!isBuilt) {
        this->outputSideParentType = parentType;
    }
    return *this;
}

SynapseType* SynapseTypeBuilder::getOutputSideParent() const {
    return outputSideParentType;
}

SynapseType* SynapseTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }

    // Build the actual implementation
    builtInstance = new SynapseType(registry, name);
    linkType = new LinkType(registry, name);

    // Configure the implementation with builder settings
    linkType->setSynapseType(builtInstance);
    builtInstance->setLinkType(linkType);

    // Set up pairing configuration using elegant common neuron type detection
    for (const PairingConfig& config : pairingConfigs) {
        if (!config.pairedSynapseType) {
            continue;
        }
        
        // Determine which side to attach the pairing config based on common neuron type
        bool attachToInputSide = false;
        
        // Get neuron types of the paired synapse
        NeuronType* pairedInputType = config.pairedSynapseType->getInputType();
        NeuronType* pairedOutputType = config.pairedSynapseType->getOutputType();
        
        // Check if we share a common neuron type and determine the side
        if (inputType && inputType == pairedInputType) {
            // Common input neuron type -> attach to input side
            attachToInputSide = true;
        } else if (inputType && inputType == pairedOutputType) {
            // Our input connects to their output -> attach to input side
            attachToInputSide = true;
        } else if (outputType && outputType == pairedInputType) {
            // Our output connects to their input -> attach to output side
            attachToInputSide = false;
        } else if (outputType && outputType == pairedOutputType) {
            // Common output neuron type -> attach to output side
            attachToInputSide = false;
        } else {
            // Default: attach to output side if no clear common neuron type found
            attachToInputSide = false;
        }
        
        // Attach the pairing config to the determined side
        if (attachToInputSide) {
            builtInstance->setInputSidePairingConfig(config);
        } else {
            builtInstance->setOutputSidePairingConfig(config);
        }
        
        // Check if both synapse types share a common output neuron type
        // If so, set allowLatentLinking to true for both synapse types
        if (outputType && outputType == pairedOutputType) {
            // Both synapses have the same output neuron type - enable latent linking
            builtInstance->setAllowLatentLinking(true);
            config.pairedSynapseType->setAllowLatentLinking(true);
        }
        
        // Check if the pair spans over a neuron type (for wildcardBSSlot setting)
        // Spanning occurs when: one synapse's output connects to another's input, or vice versa
        NeuronType* spanningNeuron = nullptr;
        
        if (outputType && outputType == pairedInputType) {
            // Our output connects to their input: A→B, B→C (spans over B)
            spanningNeuron = outputType;
        } else if (inputType && inputType == pairedOutputType) {
            // Our input connects from their output: A→B, C→A (spans over A) 
            spanningNeuron = inputType;
        }
        
        // If spanning detected and this is a binding signal pairing, set wildcardBSSlot
        if (spanningNeuron && config.bindingSignalSlot >= 0) {
            spanningNeuron->setWildcardBSSlot(config.bindingSignalSlot);
        }
        
        // Set up bidirectional reverse pairing on the paired synapse
        PairingConfig reversePairingConfig;
        if (config.bindingSignalSlot >= 0) {
            // Binding signal pairing
            reversePairingConfig = PairingConfig(builtInstance, config.bindingSignalSlot);
        } else {
            // Simple pairing
            reversePairingConfig = PairingConfig(builtInstance);
        }
        
        // Attach reverse pairing to the opposite side of the paired synapse
        if (attachToInputSide) {
            // Input side pairing creates output side reverse pairing
            config.pairedSynapseType->setOutputSidePairingConfig(reversePairingConfig);
        } else {
            // Output side pairing creates input side reverse pairing
            config.pairedSynapseType->setInputSidePairingConfig(reversePairingConfig);
        }
    }

    if (inputType) {
        builtInstance->setInputType(inputType);
        linkType->setInputType(inputType->getActivationType());
    }
    if (outputType) {
        builtInstance->setOutputType(outputType);
        linkType->setOutputType(outputType->getActivationType());
    }
    
    if (!transitions.empty()) {
        builtInstance->setTransitions(transitions);
    }

    // Set up inheritance hierarchy
    if (inputSideParentType) {
        builtInstance->addParent(inputSideParentType);
        linkType->addParent(inputSideParentType->getLinkType());
    }
    if (outputSideParentType) {
        builtInstance->addParent(outputSideParentType);
        linkType->addParent(outputSideParentType->getLinkType());
    }

    isBuilt = true;
    return builtInstance;
}