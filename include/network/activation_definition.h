#ifndef NETWORK_ACTIVATION_DEFINITION_H
#define NETWORK_ACTIVATION_DEFINITION_H

#include "network/node_definition.h"
#include "fields/type.h"

class ActivationDefinition : public Type {
public:
    ActivationDefinition(TypeRegistry* registry, const std::string& name);
    virtual ~ActivationDefinition();

    // Add any additional methods or members specific to ActivationDefinition here
};

#endif // NETWORK_ACTIVATION_DEFINITION_H 