#ifndef NETWORK_NEURON_DEFINITION_H
#define NETWORK_NEURON_DEFINITION_H

#include "fields/relation.h"
#include "fields/type.h"
#include "fields/type_registry.h"
#include "network/activation.h"
#include "network/model.h"
#include "network/neuron.h"

#include <string>
#include <vector>

class NeuronDefinition : public Type {
public:
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationMany ACTIVATION;
    // Cannot store abstract class Relation in vector, need to use pointers instead
    // static const std::vector<Relation> RELATIONS;

    NeuronDefinition(TypeRegistry* registry, const std::string& name);

    std::vector<Relation> getRelations() const;
    Neuron* instantiate(Model* m);

    ActivationType* getActivation() const;
    NeuronDefinition* setActivation(ActivationType* activation);

private:
    ActivationType* activation;
};

#endif // NETWORK_NEURON_DEFINITION_H 