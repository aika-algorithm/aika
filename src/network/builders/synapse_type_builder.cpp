#include "network/builders/synapse_type_builder.h"
#include "network/types/synapse_type.h"
#include "network/types/link_type.h"
#include "fields/type_registry.h"


SynapseTypeBuilder::SynapseTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), inputType(nullptr), outputType(nullptr), linkType(nullptr),
      pairingConfigs(), transitions(), parentTypes(), builtInstance(nullptr), isBuilt(false) {
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

// Pairing methods - side bits determine storage location
SynapseTypeBuilder& SynapseTypeBuilder::pairBySynapse(SynapseType* pairedSynapseType) {
    PairingConfig config(pairedSynapseType);
    this->pairingConfigs.push_back(config);

    return *this;
}

SynapseTypeBuilder& SynapseTypeBuilder::pairByBindingSignal(SynapseType* pairedSynapseType,
                                                          int bindingSignalSlot) {
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

SynapseTypeBuilder& SynapseTypeBuilder::addParent(SynapseType* parentType) {
    if (parentType && !isBuilt) {
        parentTypes.push_back(parentType);
    }
    return *this;
}

std::vector<SynapseType*> SynapseTypeBuilder::getParents() const {
    return parentTypes;
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
        
        // Set up bidirectional reverse pairing on the paired synapse
        PairingConfig reversePairingConfig;
        if (config.type == PairingType::BY_SYNAPSE) {
            reversePairingConfig = PairingConfig(builtInstance);
        } else if (config.type == PairingType::BY_BINDING_SIGNAL) {
            reversePairingConfig = PairingConfig(builtInstance, config.bindingSignalSlot);
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
    for (SynapseType* parentType : parentTypes) {
        builtInstance->addParent(parentType);
        linkType->addParent(parentType->getLinkType());
    }

    isBuilt = true;
    return builtInstance;
}