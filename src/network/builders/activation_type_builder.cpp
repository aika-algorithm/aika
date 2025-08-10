#include "network/builders/activation_type_builder.h"
#include "network/activation_type.h"
#include "network/builders/neuron_type_builder.h"
#include "fields/type_registry.h"

// Static relation constants
const RelationSelf ActivationType::SELF = RelationSelf(0, "SELF");
const RelationMany ActivationType::INPUT = RelationMany(1, "INPUT");
const RelationMany ActivationType::OUTPUT = RelationMany(2, "OUTPUT");
const RelationOne ActivationType::NEURON = RelationOne(3, "NEURON");

ActivationTypeBuilder::ActivationTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), neuronType(nullptr), builtInstance(nullptr), isBuilt(false) {
}

ActivationTypeBuilder& ActivationTypeBuilder::setNeuron(NeuronTypeBuilder* neuronType) {
    this->neuronType = neuronType;
    return *this;
}

std::string ActivationTypeBuilder::getName() const {
    return name;
}

TypeRegistry* ActivationTypeBuilder::getTypeRegistry() const {
    return registry;
}

NeuronTypeBuilder* ActivationTypeBuilder::getNeuron() const {
    return neuronType;
}

ActivationType* ActivationTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }
    
    // Build the actual implementation
    builtInstance = new ActivationType(getTypeRegistry(), getName());
    
    // Configure the implementation with builder settings
    if (neuronType) {
        builtInstance->setNeuronType(neuronType->build());
    }
    
    isBuilt = true;
    return builtInstance;
}

std::vector<Relation*> ActivationTypeBuilder::getRelations() const {
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationMany*>(&INPUT),
        const_cast<RelationMany*>(&OUTPUT),
        const_cast<RelationOne*>(&NEURON)
    };
}

std::string ActivationTypeBuilder::toString() const {
    return getName();
}