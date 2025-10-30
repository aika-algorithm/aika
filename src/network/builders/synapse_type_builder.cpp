#include "network/builders/synapse_type_builder.h"
#include "network/types/synapse_type.h"
#include "network/types/link_type.h"
#include "fields/type_registry.h"


SynapseTypeBuilder::SynapseTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), inputType(nullptr), outputType(nullptr), linkType(nullptr),
      inputSidePairingConfig(), outputSidePairingConfig(),
      inputSideThisInputSide(false), inputSidePairedInputSide(false),
      outputSideThisInputSide(false), outputSidePairedInputSide(false),
      parentTypes(), builtInstance(nullptr), isBuilt(false) {
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

    this->outputSidePairingConfig = config;
    this->outputSideThisInputSide = false;
    this->outputSidePairedInputSide = false;  // BY_SYNAPSE uses default output-side pairing

    return *this;
}

SynapseTypeBuilder& SynapseTypeBuilder::pairByBindingSignal(SynapseType* pairedSynapseType, 
                                                          bool thisInputSide, 
                                                          bool pairedInputSide, 
                                                          int bindingSignalSlot) {
    PairingConfig config(pairedSynapseType, bindingSignalSlot);
    if (thisInputSide) {
        this->inputSidePairingConfig = config;
        this->inputSideThisInputSide = thisInputSide;
        this->inputSidePairedInputSide = pairedInputSide;
    } else {
        this->outputSidePairingConfig = config;
        this->outputSideThisInputSide = thisInputSide;
        this->outputSidePairedInputSide = pairedInputSide;
    }
    return *this;
}

SynapseTypeBuilder& SynapseTypeBuilder::setPairedSynapseType(SynapseType* pairedSynapseType) {
    this->outputSidePairingConfig = PairingConfig(pairedSynapseType);  // Default to BY_SYNAPSE pairing
    this->outputSideThisInputSide = false;  // Default to output-side
    this->outputSidePairedInputSide = false;  // BY_SYNAPSE uses default output-side pairing
    return *this;
}

// Getters for dual pairing configuration
const PairingConfig& SynapseTypeBuilder::getInputSidePairingConfig() const {
    return inputSidePairingConfig;
}

const PairingConfig& SynapseTypeBuilder::getOutputSidePairingConfig() const {
    return outputSidePairingConfig;
}

// Legacy getter (backward compatibility) - return first available pairing
const PairingConfig& SynapseTypeBuilder::getPairingConfig() const {
    // Return output-side pairing if available, otherwise input-side
    if (outputSidePairingConfig.type != PairingType::NONE) {
        return outputSidePairingConfig;
    }
    return inputSidePairingConfig;
}

SynapseType* SynapseTypeBuilder::getPairedSynapseType() const {
    // Return output-side paired synapse if available, otherwise input-side
    if (outputSidePairingConfig.pairedSynapseType != nullptr) {
        return outputSidePairingConfig.pairedSynapseType;
    }
    return inputSidePairingConfig.pairedSynapseType;
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

    // Set up dual pairing configuration
    if (inputSidePairingConfig.type != PairingType::NONE) {
        builtInstance->setInputSidePairingConfig(inputSidePairingConfig);
        
        // For bidirectional pairing, set up the reverse pairing on the paired synapse
        if (inputSidePairingConfig.pairedSynapseType) {
            PairingConfig reversePairingConfig;
            
            if (inputSidePairingConfig.type == PairingType::BY_SYNAPSE) {
                reversePairingConfig = PairingConfig(builtInstance);
            } else if (inputSidePairingConfig.type == PairingType::BY_BINDING_SIGNAL) {
                reversePairingConfig = PairingConfig(builtInstance, inputSidePairingConfig.bindingSignalSlot);
            }
            
            // Set up reverse pairing on the appropriate side of the paired synapse
            if (inputSidePairedInputSide) {
                inputSidePairingConfig.pairedSynapseType->setInputSidePairingConfig(reversePairingConfig);
            } else {
                inputSidePairingConfig.pairedSynapseType->setOutputSidePairingConfig(reversePairingConfig);
            }
        }
    }
    
    if (outputSidePairingConfig.type != PairingType::NONE) {
        builtInstance->setOutputSidePairingConfig(outputSidePairingConfig);
        
        // For bidirectional pairing, set up the reverse pairing on the paired synapse
        if (outputSidePairingConfig.pairedSynapseType) {
            PairingConfig reversePairingConfig;
            
            if (outputSidePairingConfig.type == PairingType::BY_SYNAPSE) {
                reversePairingConfig = PairingConfig(builtInstance);
            } else if (outputSidePairingConfig.type == PairingType::BY_BINDING_SIGNAL) {
                reversePairingConfig = PairingConfig(builtInstance, outputSidePairingConfig.bindingSignalSlot);
            }
            
            // Set up reverse pairing on the appropriate side of the paired synapse
            if (outputSidePairedInputSide) {
                outputSidePairingConfig.pairedSynapseType->setInputSidePairingConfig(reversePairingConfig);
            } else {
                outputSidePairingConfig.pairedSynapseType->setOutputSidePairingConfig(reversePairingConfig);
            }
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