#include "network/neuron_definition.h"
#include "network/neuron.h"

const RelationSelf NeuronDefinition::SELF = RelationSelf(0, "SELF");
const RelationMany NeuronDefinition::INPUT = RelationMany(1, "INPUT");
const RelationMany NeuronDefinition::OUTPUT = RelationMany(2, "OUTPUT");
const RelationMany NeuronDefinition::ACTIVATION = RelationMany(3, "ACTIVATION");
// Cannot store abstract class Relation in vector, need to use pointers instead
// const std::vector<Relation> NeuronDefinition::RELATIONS = {SELF, INPUT, OUTPUT, ACTIVATION};

// Static initializer to set up reverse relationships
class NeuronDefinitionInitializer {
public:
    NeuronDefinitionInitializer() {
        // Set up bidirectional relationships
        const_cast<RelationMany&>(NeuronDefinition::INPUT).setReversed(const_cast<RelationMany*>(&NeuronDefinition::OUTPUT));
        const_cast<RelationMany&>(NeuronDefinition::OUTPUT).setReversed(const_cast<RelationMany*>(&NeuronDefinition::INPUT));
        
        // SELF and ACTIVATION are their own reverse
        const_cast<RelationSelf&>(NeuronDefinition::SELF).setReversed(const_cast<RelationSelf*>(&NeuronDefinition::SELF));
        const_cast<RelationMany&>(NeuronDefinition::ACTIVATION).setReversed(const_cast<RelationMany*>(&NeuronDefinition::ACTIVATION));
    }
};

static NeuronDefinitionInitializer neuronDefInit;

NeuronDefinition::NeuronDefinition(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation> NeuronDefinition::getRelations() const {
    // We can't return a vector of abstract class objects
    // For now, return an empty vector
    // The actual implementation should return a vector of pointers or use another approach
    return std::vector<Relation>();
}

Neuron* NeuronDefinition::instantiate(Model* m) {
    // Implementation for instantiating a Neuron
    return new Neuron(this, m);
}

ActivationDefinition* NeuronDefinition::getActivation() const {
    return activation;
}

NeuronDefinition* NeuronDefinition::setActivation(ActivationDefinition* activation) {
    this->activation = activation;
    return this;
} 