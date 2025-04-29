#include "network/node_definition.h"

NodeDefinition::NodeDefinition(TypeRegistry* registry, const std::string& name) : registry(registry), name(name), neuron(nullptr), activation(nullptr) {}

NodeDefinition* NodeDefinition::addParent(NodeDefinition* parent) {
    // Implementation for adding a parent node
    return this;
} 