#ifndef NETWORK_NODE_DEFINITION_H
#define NETWORK_NODE_DEFINITION_H

#include "fields/type_registry.h"

class NeuronType;
class ActivationType;

class NodeDefinition {
public:
    NodeDefinition(TypeRegistry* registry, const std::string& name);

    NodeDefinition* addParent(NodeDefinition* parent);

private:
    NeuronType* neuron;
    ActivationType* activation;
};

#endif // NETWORK_NODE_DEFINITION_H 