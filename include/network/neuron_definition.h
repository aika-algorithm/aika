#ifndef NETWORK_NEURON_DEFINITION_H
#define NETWORK_NEURON_DEFINITION_H

#include "network/activation.h"
#include "network/model.h"
#include "network/neuron.h"
#include "network/relation.h"
#include "network/relation_many.h"
#include "network/relation_self.h"
#include "network/type.h"
#include "network/type_registry.h"

#include <string>
#include <vector>

class NeuronDefinition : public Type {
public:
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationMany ACTIVATION;
    static const std::vector<Relation> RELATIONS;

    NeuronDefinition(TypeRegistry* registry, const std::string& name);

    std::vector<Relation> getRelations() const;
    Neuron* instantiate(Model* m);

    ActivationDefinition* getActivation() const;
    NeuronDefinition* setActivation(ActivationDefinition* activation);

private:
    ActivationDefinition* activation;
};

#endif // NETWORK_NEURON_DEFINITION_H 