#ifndef AIKA_ACTIVATION_TYPE_BUILDER_H
#define AIKA_ACTIVATION_TYPE_BUILDER_H

#include "fields/type.h"
#include "fields/relation.h"
#include "network/activation_type.h"
#include <string>
#include <vector>

// Forward declarations
class TypeRegistry;
class NeuronTypeBuilder;
class ActivationType;

/**
 * Builder for configuring ActivationType instances.
 * Separates configuration concerns from runtime implementation.
 */
class ActivationTypeBuilder {
public:
    // Static relation constants for configuration
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationOne NEURON;

    explicit ActivationTypeBuilder(TypeRegistry* registry, const std::string& name);
    ~ActivationTypeBuilder();

    std::string getName() const;
    TypeRegistry* getTypeRegistry() const;

    // Builder configuration methods
    ActivationTypeBuilder& setNeuron(NeuronTypeBuilder* neuronType);
    NeuronTypeBuilder* getNeuron() const;

    // Build the actual implementation
    ActivationType* build();

    // Type interface
    std::vector<Relation*> getRelations() const;
    std::string toString() const;

private:
    std::string name;
    TypeRegistry* registry;
    NeuronTypeBuilder* neuronType;
    ActivationType* builtInstance;
    bool isBuilt;
};

#endif //AIKA_ACTIVATION_TYPE_BUILDER_H