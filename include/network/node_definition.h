#ifndef NETWORK_NODE_DEFINITION_H
#define NETWORK_NODE_DEFINITION_H

#include "fields/type_registry.h"
#include "network/activation_definition.h"
#include "network/neuron_definition.h"

class NodeDefinition {
public:
    NodeDefinition(TypeRegistry* registry, const std::string& name);

    NodeDefinition* addParent(NodeDefinition* parent);

private:
    NeuronDefinition* neuron;
    ActivationDefinition* activation;
};

#endif // NETWORK_NODE_DEFINITION_H 