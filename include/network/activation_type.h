#ifndef NETWORK_ACTIVATION_DEFINITION_H
#define NETWORK_ACTIVATION_DEFINITION_H

#include "network/node_definition.h"
#include "fields/type.h"
#include "fields/relation.h"

class ActivationType : public Type {
public:
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationOne NEURON;

    ActivationType(TypeRegistry* registry, const std::string& name);
    virtual ~ActivationType();

    std::vector<Relation*> getRelations() const;

    // Add any additional methods or members specific to ActivationType here
};

#endif // NETWORK_ACTIVATION_DEFINITION_H 