#ifndef AIKA_NEURON_TYPE_BUILDER_H
#define AIKA_NEURON_TYPE_BUILDER_H

#include "fields/type.h"
#include "fields/relation.h"
#include "network/types/neuron_type.h"
#include <string>
#include <vector>

// Forward declarations
class TypeRegistry;

/**
 * Builder for configuring NeuronType instances.
 * Separates configuration concerns from runtime implementation.
 */
class NeuronTypeBuilder {
public:
    explicit NeuronTypeBuilder(TypeRegistry* registry, const std::string& name);
    ~NeuronTypeBuilder();

    std::string getName() const;

    // Builder configuration methods
    NeuronTypeBuilder& addParent(NeuronType* parentType);
    std::vector<NeuronType*> getParents() const;
    
    // Configure number of binding signal slots
    NeuronTypeBuilder& setNumberOfBSSlots(int numberOfBSSlots);
    int getNumberOfBSSlots() const;

    // Build the actual implementation
    NeuronType* build();

private:
    std::string name;
    TypeRegistry* registry;
    ActivationType* activationType;
    NeuronType* builtInstance;
    std::vector<NeuronType*> parentTypes;
    int numberOfBSSlots;
    bool isBuilt;
};

#endif //AIKA_NEURON_TYPE_BUILDER_H