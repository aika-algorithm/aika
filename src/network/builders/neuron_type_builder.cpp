#include "network/builders/neuron_type_builder.h"
#include "network/types/neuron_type.h"
#include "network/types/activation_type.h"
#include "fields/type_registry.h"


NeuronTypeBuilder::NeuronTypeBuilder(TypeRegistry* registry, const std::string& name)
    : registry(registry), name(name), activationType(nullptr), builtInstance(nullptr), isBuilt(false) {
}

std::string NeuronTypeBuilder::getName() const {
    return name;
}

NeuronType* NeuronTypeBuilder::build() {
    if (isBuilt) {
        return builtInstance;
    }

    activationType = new ActivationType(registry, name);
    builtInstance = new NeuronType(registry, name);

    builtInstance->setActivationType(activationType);
    activationType->setNeuronType(builtInstance);
    
    isBuilt = true;
    return builtInstance;
}
