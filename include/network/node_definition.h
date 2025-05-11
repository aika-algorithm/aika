#ifndef NETWORK_NODE_DEFINITION_H
#define NETWORK_NODE_DEFINITION_H

#include "fields/type_registry.h"

class NeuronDefinition;
class ActivationDefinition;

class NodeDefinition {
public:
    NodeDefinition(TypeRegistry* registry, const std::string& name);

    NodeDefinition* addParent(NodeDefinition* parent);

private:
    NeuronDefinition* neuron;
    ActivationDefinition* activation;
};

#endif // NETWORK_NODE_DEFINITION_H 