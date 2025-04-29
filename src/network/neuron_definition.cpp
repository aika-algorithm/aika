#include "network/neuron_definition.h"

const RelationSelf NeuronDefinition::SELF = RelationSelf();
const RelationMany NeuronDefinition::INPUT = RelationMany();
const RelationMany NeuronDefinition::OUTPUT = RelationMany();
const RelationMany NeuronDefinition::ACTIVATION = RelationMany();
const std::vector<Relation> NeuronDefinition::RELATIONS = {SELF, INPUT, OUTPUT, ACTIVATION};

NeuronDefinition::NeuronDefinition(TypeRegistry* registry, const std::string& name) : Type(registry, name) {}

std::vector<Relation> NeuronDefinition::getRelations() const {
    return RELATIONS;
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