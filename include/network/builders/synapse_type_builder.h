#ifndef AIKA_SYNAPSE_TYPE_BUILDER_H
#define AIKA_SYNAPSE_TYPE_BUILDER_H

#include "fields/type.h"
#include "fields/relation.h"
#include "network/types/synapse_type.h"
#include "network/transition.h"
#include <string>
#include <vector>

// Forward declarations
class TypeRegistry;
class SynapseType;

/**
 * Builder for configuring SynapseType instances.
 * Separates configuration concerns from runtime implementation.
 */
class SynapseTypeBuilder {
public:
    explicit SynapseTypeBuilder(TypeRegistry* registry, const std::string& name);
    ~SynapseTypeBuilder();

    std::string getName() const;
    TypeRegistry* getTypeRegistry() const;

    // Builder configuration methods
    SynapseTypeBuilder& setInput(NeuronType* inputType);
    NeuronType* getInput() const;

    SynapseTypeBuilder& setOutput(NeuronType* outputType);
    NeuronType* getOutput() const;

    SynapseTypeBuilder& setPairedSynapseType(SynapseType* pairedSynapseType);
    SynapseType* getPairedSynapseType() const;

    SynapseTypeBuilder& setTransitions(const std::vector<Transition*>& transitions);
    std::vector<Transition*> getTransitions() const;

    // Build the actual implementation
    SynapseType* build();

private:
    std::string name;
    TypeRegistry* registry;
    NeuronType* inputType;
    NeuronType* outputType;
    LinkType* linkType;
    SynapseType* pairedSynapseType;
    std::vector<Transition*> transitions;
    SynapseType* builtInstance;
    bool isBuilt;
};

#endif //AIKA_SYNAPSE_TYPE_BUILDER_H