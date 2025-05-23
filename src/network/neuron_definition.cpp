#include "network/neuron_definition.h"

const RelationSelf NeuronDefinition::SELF = RelationSelf(0, "SELF");
const RelationMany NeuronDefinition::INPUT = RelationMany(1, "INPUT");
const RelationMany NeuronDefinition::OUTPUT = RelationMany(2, "OUTPUT");
const RelationMany NeuronDefinition::ACTIVATION = RelationMany(3, "ACTIVATION");
// Cannot store abstract class Relation in vector, need to use pointers instead
// const std::vector<Relation> NeuronDefinition::RELATIONS = {SELF, INPUT, OUTPUT, ACTIVATION};

NeuronDefinition::NeuronDefinition(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation> NeuronDefinition::getRelations() const {
    // We can't return a vector of abstract class objects
    // For now, return an empty vector
    // The actual implementation should return a vector of pointers or use another approach
    return std::vector<Relation>();
}

Neuron* NeuronDefinition::instantiate(Model* m) {
    // Implementation for instantiating a Neuron
    return nullptr; // Placeholder
}

ActivationDefinition* NeuronDefinition::getActivation() const {
    return activation;
}

NeuronDefinition* NeuronDefinition::setActivation(ActivationDefinition* activation) {
    this->activation = activation;
    return this;
} 