#ifndef AIKA_SYNAPSE_TYPE_BUILDER_H
#define AIKA_SYNAPSE_TYPE_BUILDER_H

#include "fields/type.h"
#include "fields/relation.h"
#include <string>
#include <vector>

// Forward declarations
class TypeRegistry;
class NeuronTypeBuilder;
class LinkTypeBuilder;
class SynapseType;

/**
 * Builder for configuring SynapseType instances.
 * Separates configuration concerns from runtime implementation.
 */
class SynapseTypeBuilder {
public:
    // Static relation constants for configuration
    static const RelationSelf SELF;
    static const RelationMany INPUT;
    static const RelationMany OUTPUT;
    static const RelationOne LINK;

    explicit SynapseTypeBuilder(TypeRegistry* registry, const std::string& name);
    ~SynapseTypeBuilder() override = default;

    // Builder configuration methods
    SynapseTypeBuilder& setInput(NeuronTypeBuilder* inputType);
    SynapseTypeBuilder& setOutput(NeuronTypeBuilder* outputType);
    SynapseTypeBuilder& setLink(LinkTypeBuilder* linkType);

    NeuronTypeBuilder* getInput() const;
    NeuronTypeBuilder* getOutput() const;
    LinkTypeBuilder* getLink() const;

    // Build the actual implementation
    SynapseType* build();

    // Type interface
    std::vector<Relation*> getRelations() const;
    std::string toString() const;

private:
    NeuronTypeBuilder* inputType;
    NeuronTypeBuilder* outputType;
    LinkTypeBuilder* linkType;
    SynapseType* builtInstance;
    bool isBuilt;
};

#endif //AIKA_SYNAPSE_TYPE_BUILDER_H