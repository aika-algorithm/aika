#include "network/builders/neuron_type_builder.h"
#include "network/types/neuron_type.h"
#include "network/builders/activation_type_builder.h"
#include "fields/type_registry.h"

// Static relation constants
const RelationSelf NeuronType::SELF = RelationSelf(0, "SELF");
const RelationMany NeuronType::INPUT = RelationMany(1, "INPUT");
const RelationMany NeuronType::OUTPUT = RelationMany(2, "OUTPUT");
const RelationMany NeuronType::ACTIVATION = RelationMany(3, "ACTIVATION");

NeuronTypeBuilder::NeuronTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), activationType(nullptr), builtInstance(nullptr), isBuilt(false) {
}

std::string NeuronTypeBuilder::getName() const {
    return name;
}

TypeRegistry* NeuronTypeBuilder::getTypeRegistry() const {
    return registry;
}

NeuronTypeBuilder& NeuronTypeBuilder::setActivation(ActivationTypeBuilder* activationType) {
    this->activationType = activationType;
    return *this;
}

ActivationTypeBuilder* NeuronTypeBuilder::getActivation() const {
    return activationType;
}

NeuronType* NeuronTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }
    
    // Build the actual implementation
    builtInstance = new NeuronType(getTypeRegistry(), getName());
    
    // Configure the implementation with builder settings
    if (activationType) {
        builtInstance->setActivationType(activationType->build());
    }
    
    isBuilt = true;
    return builtInstance;
}

std::vector<Relation*> NeuronTypeBuilder::getRelations() const {
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationMany*>(&INPUT),
        const_cast<RelationMany*>(&OUTPUT),
        const_cast<RelationOne*>(&ACTIVATION)
    };
}

std::string NeuronTypeBuilder::toString() const {
    return getName();
}