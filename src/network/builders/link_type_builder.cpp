#include "network/builders/link_type_builder.h"
#include "network/types/link_type.h"
#include "network/builders/synapse_type_builder.h"
#include "network/builders/activation_type_builder.h"
#include "fields/type_registry.h"

// Static relation constants
const RelationSelf LinkType::SELF = RelationSelf(0, "SELF");
const RelationOne LinkType::INPUT = RelationOne(1, "INPUT");
const RelationOne LinkType::OUTPUT = RelationOne(2, "OUTPUT");
const RelationOne LinkType::SYNAPSE = RelationOne(3, "SYNAPSE");
const RelationOne LinkType::PAIR_IN = RelationOne(4, "PAIR_IN");
const RelationOne LinkType::PAIR_OUT = RelationOne(5, "PAIR_OUT");

LinkTypeBuilder::LinkTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), synapseType(nullptr), inputType(nullptr), outputType(nullptr),
      builtInstance(nullptr), isBuilt(false) {
}

LinkTypeBuilder& LinkTypeBuilder::setSynapse(SynapseTypeBuilder* synapseType) {
    this->synapseType = synapseType;
    return *this;
}

std::string LinkTypeBuilder::getName() const {
    return name;
}

TypeRegistry* LinkTypeBuilder::getTypeRegistry() const {
    return registry;
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
    builtInstance = new LinkType(getTypeRegistry(), getName());
    
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