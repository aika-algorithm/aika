#include "network/builders/neuron_type_builder.h"
#include "network/types/neuron_type.h"
#include "network/types/activation_type.h"
#include "fields/type_registry.h"


NeuronTypeBuilder::NeuronTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), activationType(nullptr), builtInstance(nullptr), parentType(nullptr), numberOfBSSlots(0), isBuilt(false) {
}

NeuronTypeBuilder::~NeuronTypeBuilder() {
    // Note: We don't delete builtInstance here as it's managed by the TypeRegistry
}

std::string NeuronTypeBuilder::getName() const {
    return name;
}

NeuronTypeBuilder& NeuronTypeBuilder::setParent(NeuronType* parent) {
    if (!isBuilt) {
        this->parentType = parent;
    }
    return *this;
}

NeuronType* NeuronTypeBuilder::getParent() const {
    return parentType;
}

NeuronTypeBuilder& NeuronTypeBuilder::setNumberOfBSSlots(int numberOfBSSlots) {
    if (!isBuilt) {
        this->numberOfBSSlots = numberOfBSSlots;
    }
    return *this;
}

int NeuronTypeBuilder::getNumberOfBSSlots() const {
    return numberOfBSSlots;
}

NeuronType* NeuronTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }

    activationType = new ActivationType(registry, name);
    builtInstance = new NeuronType(registry, name);

    builtInstance->setActivationType(activationType);
    builtInstance->setNumberOfBSSlots(numberOfBSSlots);
    activationType->setNeuronType(builtInstance);

    // Set up inheritance hierarchy
    if (parentType) {
        builtInstance->addParent(parentType);
        activationType->addParent(parentType->getActivationType());
    }
    
    isBuilt = true;
    return builtInstance;
}
