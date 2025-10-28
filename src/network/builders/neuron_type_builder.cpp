#include "network/builders/neuron_type_builder.h"
#include "network/types/neuron_type.h"
#include "network/types/activation_type.h"
#include "fields/type_registry.h"


NeuronTypeBuilder::NeuronTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), activationType(nullptr), builtInstance(nullptr), parentTypes(), isBuilt(false) {
}

NeuronTypeBuilder::~NeuronTypeBuilder() {
    // Note: We don't delete builtInstance here as it's managed by the TypeRegistry
}

std::string NeuronTypeBuilder::getName() const {
    return name;
}

NeuronTypeBuilder& NeuronTypeBuilder::addParent(NeuronType* parentType) {
    if (parentType && !isBuilt) {
        parentTypes.push_back(parentType);
    }
    return *this;
}

std::vector<NeuronType*> NeuronTypeBuilder::getParents() const {
    return parentTypes;
}

NeuronType* NeuronTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }

    activationType = new ActivationType(registry, name);
    builtInstance = new NeuronType(registry, name);

    builtInstance->setActivationType(activationType);
    activationType->setNeuronType(builtInstance);
    
    // Set up inheritance hierarchy
    for (NeuronType* parentType : parentTypes) {
        builtInstance->addParent(parentType);
        activationType->addParent(parentType->getActivationType());
    }
    
    isBuilt = true;
    return builtInstance;
}
