#include "network/types/neuron_type.h"
#include "network/neuron.h"

const RelationSelf NeuronType::SELF = RelationSelf(0, "SELF");
const RelationMany NeuronType::INPUT = RelationMany(1, "INPUT");
const RelationMany NeuronType::OUTPUT = RelationMany(2, "OUTPUT");
const RelationMany NeuronType::ACTIVATION = RelationMany(3, "ACTIVATION");


// Static initializer to set up reverse relationships
class NeuronTypeInitializer {
public:
    NeuronTypeInitializer() {
        const_cast<RelationSelf&>(NeuronType::SELF).setReversed(const_cast<RelationSelf*>(&NeuronType::SELF));
        const_cast<RelationMany&>(NeuronType::INPUT).setReversed(const_cast<RelationMany*>(&NeuronType::OUTPUT));
        const_cast<RelationMany&>(NeuronType::OUTPUT).setReversed(const_cast<RelationMany*>(&NeuronType::INPUT));
        const_cast<RelationMany&>(NeuronType::ACTIVATION).setReversed(const_cast<RelationOne*>(&ActivationType::NEURON));
    }
};

static NeuronTypeInitializer neuronTypeInit;

NeuronType::NeuronType(TypeRegistry* registry, const std::string& name) : Type(registry, name), wildcardBSSlot(-1) {}

int NeuronType::getWildcardBSSlot() const {
    return wildcardBSSlot;
}

void NeuronType::setWildcardBSSlot(int wildcardBSSlot) {
    this->wildcardBSSlot = wildcardBSSlot;
}

std::vector<Relation*> NeuronType::getRelations() const {
    // Return a vector of pointers to avoid the abstract class issue
    return {
        const_cast<RelationSelf*>(&SELF),
        const_cast<RelationMany*>(&INPUT),
        const_cast<RelationMany*>(&OUTPUT),
        const_cast<RelationMany*>(&ACTIVATION)
    };
}

Neuron* NeuronType::instantiate(Model* m) {
    // Implementation for instantiating a Neuron
    return new Neuron(this, m);
}

ActivationType* NeuronType::getActivationType() const {
    return activationType;
}

void NeuronType::setActivationType(ActivationType* activationType) {
    this->activationType = activationType;
} 