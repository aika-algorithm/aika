#include "network/types/neuron_type.h"
#include "network/neuron.h"

const RelationSelf NeuronType::SELF = RelationSelf(0, "SELF");
const RelationMany NeuronType::INPUT = RelationMany(1, "INPUT");
const RelationMany NeuronType::OUTPUT = RelationMany(2, "OUTPUT");
const RelationMany NeuronType::ACTIVATION = RelationMany(3, "ACTIVATION");
// Cannot store abstract class Relation in vector, need to use pointers instead
// const std::vector<Relation> NeuronType::RELATIONS = {SELF, INPUT, OUTPUT, ACTIVATION};

// Static initializer to set up reverse relationships
class NeuronTypeInitializer {
public:
    NeuronTypeInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationMany&>(NeuronType::INPUT).setReversed(const_cast<RelationMany*>(&NeuronType::OUTPUT));
        const_cast<RelationMany&>(NeuronType::OUTPUT).setReversed(const_cast<RelationMany*>(&NeuronType::INPUT));
        
        // SELF and ACTIVATION are their own reverse
        const_cast<RelationSelf&>(NeuronType::SELF).setReversed(const_cast<RelationSelf*>(&NeuronType::SELF));
        const_cast<RelationMany&>(NeuronType::ACTIVATION).setReversed(const_cast<RelationMany*>(&NeuronType::ACTIVATION));
    }
};

static NeuronTypeInitializer neuronTypeInit;

NeuronType::NeuronType(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<int> NeuronType::getBindingSignals() const {
    return bindingSignals;
}

NeuronType* NeuronType::setBindingSignals(const std::vector<int>& bindingSignals) {
    this->bindingSignals = bindingSignals;
    return this;
}

std::vector<Relation> NeuronType::getRelations() const {
    // We can't return a vector of abstract class objects
    // For now, return an empty vector
    // The actual implementation should return a vector of pointers or use another approach
    return std::vector<Relation>();
}

Neuron* NeuronType::instantiate(Model* m) {
    // Implementation for instantiating a Neuron
    return new Neuron(this, m);
}

ActivationType* NeuronType::getActivationType() const {
    return activationType;
}

NeuronType* NeuronType::setActivationType(ActivationType* activationType) {
    this->activationType = activationType;
    return this;
} 