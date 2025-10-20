#include "network/builders/synapse_type_builder.h"
#include "network/types/synapse_type.h"
#include "network/types/link_type.h"
#include "fields/type_registry.h"


SynapseTypeBuilder::SynapseTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), inputType(nullptr), outputType(nullptr), linkType(nullptr),
      pairedSynapseType(nullptr), builtInstance(nullptr), isBuilt(false) {
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

SynapseTypeBuilder& SynapseTypeBuilder::setPairedSynapseType(SynapseType* pairedSynapseType) {
    this->pairedSynapseType = pairedSynapseType;
    return *this;
}

SynapseType* SynapseTypeBuilder::getPairedSynapseType() const {
    return pairedSynapseType;
}

SynapseTypeBuilder& SynapseTypeBuilder::setTransitions(const std::vector<Transition*>& transitions) {
    this->transitions = transitions;
    return *this;
}

std::vector<Transition*> SynapseTypeBuilder::getTransitions() const {
    return transitions;
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

    if (pairedSynapseType) {
        builtInstance->setPairedSynapseType(pairedSynapseType);
        pairedSynapseType->setPairedSynapseType(builtInstance);
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

    isBuilt = true;
    return builtInstance;
}