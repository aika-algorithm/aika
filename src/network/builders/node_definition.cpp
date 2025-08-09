#include "network/builders/node_definition.h"

NodeDefinition::NodeDefinition(TypeRegistry* registry, const std::string& name) : neuron(nullptr), activation(nullptr) {
    // registry and name parameters are no longer members, just store what we need
}

NodeDefinition* NodeDefinition::addParent(NodeDefinition* parent) {
    // Implementation for adding a parent node
    return this;
} 