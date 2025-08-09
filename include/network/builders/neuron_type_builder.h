#ifndef AIKA_NEURON_TYPE_BUILDER_H
#define AIKA_NEURON_TYPE_BUILDER_H

#include "fields/type.h"
#include "fields/relation.h"
#include <string>
#include <vector>

// Forward declarations
class TypeRegistry;
class ActivationTypeBuilder;

/**
 * Builder for configuring NeuronType instances.
 * Separates configuration concerns from runtime implementation.
 */
class NeuronTypeBuilder {
public:
    // Static relation constants for configuration
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationOne ACTIVATION;

    explicit NeuronTypeBuilder(TypeRegistry* registry, const std::string& name);
    ~NeuronTypeBuilder() override = default;

    // Builder configuration methods
    NeuronTypeBuilder& setActivation(ActivationTypeBuilder* activationType);
    ActivationTypeBuilder* getActivation() const;

    // Build the actual implementation
    NeuronType* build();

    // Type interface
    std::vector<Relation*> getRelations() const;
    std::string toString() const;

private:
    ActivationTypeBuilder* activationType;
    NeuronType* builtInstance;
    bool isBuilt;
};

#endif //AIKA_NEURON_TYPE_BUILDER_H