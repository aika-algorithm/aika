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

    // Wildcard binding signal slot for spanning neuron pairs
    int getWildcardBSSlot() const;
    void setWildcardBSSlot(int wildcardBSSlot);

    // Number of binding signal slots
    int getNumberOfBSSlots() const;
    void setNumberOfBSSlots(int numberOfBSSlots);

    std::vector<Relation*> getRelations() const;
    Neuron* instantiate(Model* m);

private:
    int wildcardBSSlot; // -1 indicates not set (default)
    int numberOfBSSlots; // Number of binding signal slots for fixed array size

    ActivationType* activationType;
};

#endif // NETWORK_NEURON_DEFINITION_H 