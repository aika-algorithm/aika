#include "network/builders/neuron_type_builder.h"
#include "network/neuron_type.h"
#include "network/builders/activation_type_builder.h"
#include "fields/type_registry.h"

// Static relation constants
const RelationSelf NeuronTypeBuilder::SELF(RelationSelf{});
const RelationMany NeuronTypeBuilder::INPUT(RelationMany{});
const RelationMany NeuronTypeBuilder::OUTPUT(RelationMany{});
const RelationOne NeuronTypeBuilder::ACTIVATION(RelationOne{});

NeuronTypeBuilder::NeuronTypeBuilder(TypeRegistry* registry, const std::string& name)
    : Type(registry, name), activationType(nullptr), builtInstance(nullptr), isBuilt(false) {
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
    builtInstance = new NeuronType(getRegistry(), getName());
    
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