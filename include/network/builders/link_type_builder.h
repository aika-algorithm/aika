#ifndef AIKA_LINK_TYPE_BUILDER_H
#define AIKA_LINK_TYPE_BUILDER_H

#include "fields/type.h"
#include "fields/relation.h"
#include "network/types/link_type.h"
#include <string>
#include <vector>

// Forward declarations
class TypeRegistry;
class SynapseTypeBuilder;
class ActivationTypeBuilder;
class LinkType;

/**
 * Builder for configuring LinkType instances.
 * Separates configuration concerns from runtime implementation.
 */
class LinkTypeBuilder {
public:
    // Static relation constants for configuration
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationOne SYNAPSE;
    static const RelationOne PAIR_IN;
    static const RelationOne PAIR_OUT;

    explicit LinkTypeBuilder(TypeRegistry* registry, const std::string& name);
    ~LinkTypeBuilder();

    std::string getName() const;
    TypeRegistry* getTypeRegistry() const;

    // Builder configuration methods
    LinkTypeBuilder& setSynapse(SynapseTypeBuilder* synapseType);
    LinkTypeBuilder& setInput(ActivationTypeBuilder* inputType);
    LinkTypeBuilder& setOutput(ActivationTypeBuilder* outputType);

    SynapseTypeBuilder* getSynapse() const;
    ActivationTypeBuilder* getInput() const;
    ActivationTypeBuilder* getOutput() const;

    // Build the actual implementation
    LinkType* build();

    // Type interface
    std::vector<Relation*> getRelations() const;
    std::string toString() const;

private:
    std::string name;
    TypeRegistry* registry;
    SynapseTypeBuilder* synapseType;
    ActivationTypeBuilder* inputType;
    ActivationTypeBuilder* outputType;
    LinkType* builtInstance;
    bool isBuilt;
};

#endif //AIKA_LINK_TYPE_BUILDER_H