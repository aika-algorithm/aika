#ifndef NETWORK_LINK_DEFINITION_H
#define NETWORK_LINK_DEFINITION_H

#include "network/activation.h"
#include "network/link.h"
#include "network/relation.h"
#include "network/relation_one.h"
#include "network/relation_self.h"
#include "network/synapse.h"
#include "network/type.h"
#include "network/type_registry.h"

#include <string>
#include <vector>

class LinkDefinition : public Type {
public:
    static const RelationSelf SELF;
    static const RelationOne INPUT;
    static const RelationOne OUTPUT;
    static const RelationOne SYNAPSE;
    static const RelationOne CORRESPONDING_INPUT_LINK;
    static const RelationOne CORRESPONDING_OUTPUT_LINK;
    static const std::vector<Relation> RELATIONS;

    LinkDefinition(TypeRegistry* registry, const std::string& name);

    std::vector<Relation> getRelations() const;
    Link* instantiate(Synapse* synapse, Activation* input, Activation* output);

    SynapseDefinition* getSynapse() const;
    LinkDefinition* setSynapse(SynapseDefinition* synapse);

    ActivationDefinition* getInput() const;
    LinkDefinition* setInput(ActivationDefinition* input);

    ActivationDefinition* getOutput() const;
    LinkDefinition* setOutput(ActivationDefinition* output);

    std::string toString() const override;

private:
    SynapseDefinition* synapse;
    ActivationDefinition* input;
    ActivationDefinition* output;
};

#endif // NETWORK_LINK_DEFINITION_H 