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

class NeuronType : public Type {
public:
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationMany ACTIVATION;
    // Cannot store abstract class Relation in vector, need to use pointers instead
    // static const std::vector<Relation> RELATIONS;

    NeuronType(TypeRegistry* registry, const std::string& name);

    std::vector<int> getBindingSignals() const;
    NeuronType* setBindingSignals(const std::vector<int>& bindingSignals);

    std::vector<Relation> getRelations() const;
    Neuron* instantiate(Model* m);

    ActivationType* getActivation() const;
    NeuronType* setActivation(ActivationType* activation);

private:
    std::vector<int> bindingSignals;

    ActivationType* activation;
};

#endif // NETWORK_NEURON_DEFINITION_H 