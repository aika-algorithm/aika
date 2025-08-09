#include "network/builders/synapse_type_builder.h"
#include "network/synapse_type.h"
#include "network/builders/neuron_type_builder.h"
#include "network/builders/link_type_builder.h"
#include "fields/type_registry.h"

// Static relation constants
const RelationSelf SynapseTypeBuilder::SELF(RelationSelf{});
const RelationMany SynapseTypeBuilder::INPUT(RelationMany{});
const RelationMany SynapseTypeBuilder::OUTPUT(RelationMany{});
const RelationOne SynapseTypeBuilder::LINK(RelationOne{});

SynapseTypeBuilder::SynapseTypeBuilder(TypeRegistry* registry, const std::string& name)
    : Type(registry, name), inputType(nullptr), outputType(nullptr), linkType(nullptr), 
      builtInstance(nullptr), isBuilt(false) {
}

SynapseTypeBuilder& SynapseTypeBuilder::setInput(NeuronTypeBuilder* inputType) {
    this->inputType = inputType;
    return *this;
}

SynapseTypeBuilder& SynapseTypeBuilder::setOutput(NeuronTypeBuilder* outputType) {
    this->outputType = outputType;
    return *this;
}

SynapseTypeBuilder& SynapseTypeBuilder::setLink(LinkTypeBuilder* linkType) {
    this->linkType = linkType;
    return *this;
}

NeuronTypeBuilder* SynapseTypeBuilder::getInput() const {
    return inputType;
}

NeuronTypeBuilder* SynapseTypeBuilder::getOutput() const {
    return outputType;
}

LinkTypeBuilder* SynapseTypeBuilder::getLink() const {
    return linkType;
}

SynapseType* SynapseTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }
    
    // Build the actual implementation
    builtInstance = new SynapseType(getRegistry(), getName());
    
    // Configure the implementation with builder settings
    if (inputType) {
        builtInstance->setInputType(inputType->build());
    }
    if (outputType) {
        builtInstance->setOutputType(outputType->build());
    }
    if (linkType) {
        builtInstance->setLinkType(linkType->build());
    }
    
    isBuilt = true;
    return builtInstance;
}

std::vector<Relation*> SynapseTypeBuilder::getRelations() const {
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationMany*>(&INPUT),
        const_cast<RelationMany*>(&OUTPUT),
        const_cast<RelationOne*>(&LINK)
    };
}

std::string SynapseTypeBuilder::toString() const {
    return getName();
}