#ifndef NETWORK_NEURON_DEFINITION_H
#define NETWORK_NEURON_DEFINITION_H

#include "fields/relation.h"
#include "fields/type.h"
#include "fields/type_registry.h"
#include "network/types/activation_type.h"
#include "network/model.h"

#include <string>
#include <vector>

class NeuronType : public Type {
public:
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationMany ACTIVATION;

    NeuronType(TypeRegistry* registry, const std::string& name);

    ActivationType* getActivationType() const;
    void setActivationType(ActivationType* activationType);

    std::vector<int> getBindingSignals() const;
    void setBindingSignals(const std::vector<int>& bindingSignals);

    std::vector<Relation*> getRelations() const;
    Neuron* instantiate(Model* m);

private:
    std::vector<int> bindingSignals;

    ActivationType* activationType;
};

#endif // NETWORK_NEURON_DEFINITION_H 