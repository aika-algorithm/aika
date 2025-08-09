#include "network/builders/link_type_builder.h"
#include "network/link_type.h"
#include "network/builders/synapse_type_builder.h"
#include "network/builders/activation_type_builder.h"
#include "fields/type_registry.h"

// Static relation constants
const RelationSelf LinkTypeBuilder::SELF(RelationSelf{});
const RelationMany LinkTypeBuilder::INPUT(RelationMany{});
const RelationMany LinkTypeBuilder::OUTPUT(RelationMany{});
const RelationOne LinkTypeBuilder::SYNAPSE(RelationOne{});
const RelationOne LinkTypeBuilder::PAIR_IN(RelationOne{});
const RelationOne LinkTypeBuilder::PAIR_OUT(RelationOne{});

LinkTypeBuilder::LinkTypeBuilder(TypeRegistry* registry, const std::string& name)
    : Type(registry, name), synapseType(nullptr), inputType(nullptr), outputType(nullptr),
      builtInstance(nullptr), isBuilt(false) {
}

LinkTypeBuilder& LinkTypeBuilder::setSynapse(SynapseTypeBuilder* synapseType) {
    this->synapseType = synapseType;
    return *this;
}

LinkTypeBuilder& LinkTypeBuilder::setInput(ActivationTypeBuilder* inputType) {
    this->inputType = inputType;
    return *this;
}

LinkTypeBuilder& LinkTypeBuilder::setOutput(ActivationTypeBuilder* outputType) {
    this->outputType = outputType;
    return *this;
}

SynapseTypeBuilder* LinkTypeBuilder::getSynapse() const {
    return synapseType;
}

ActivationTypeBuilder* LinkTypeBuilder::getInput() const {
    return inputType;
}

ActivationTypeBuilder* LinkTypeBuilder::getOutput() const {
    return outputType;
}

LinkType* LinkTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }
    
    // Build the actual implementation
    builtInstance = new LinkType(getRegistry(), getName());
    
    // Configure the implementation with builder settings
    if (synapseType) {
        builtInstance->setSynapseType(synapseType->build());
    }
    if (inputType) {
        builtInstance->setInputType(inputType->build());
    }
    if (outputType) {
        builtInstance->setOutputType(outputType->build());
    }
    
    isBuilt = true;
    return builtInstance;
}

std::vector<Relation*> LinkTypeBuilder::getRelations() const {
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationMany*>(&INPUT),
        const_cast<RelationMany*>(&OUTPUT),
        const_cast<RelationOne*>(&SYNAPSE),
        const_cast<RelationOne*>(&PAIR_IN),
        const_cast<RelationOne*>(&PAIR_OUT)
    };
}

std::string LinkTypeBuilder::toString() const {
    return getName();
}