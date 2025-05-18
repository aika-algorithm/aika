#ifndef NETWORK_LINK_DEFINITION_H
#define NETWORK_LINK_DEFINITION_H

#include "fields/type.h"
#include "fields/type_registry.h"
#include "fields/relation.h"

#include "network/activation.h"
#include "network/link.h"

#include <string>
#include <vector>

class Link;
class Synapse;
class SynapseDefinition;


class LinkDefinition : public Type {
public:
    static const RelationSelf SELF;
    static const RelationOne INPUT;
    static const RelationOne OUTPUT;
    static const RelationOne SYNAPSE;
    static const RelationOne CORRESPONDING_INPUT_LINK;
    static const RelationOne CORRESPONDING_OUTPUT_LINK;
    // We can't use vector of abstract class Relation directly
    // static const std::vector<Relation> RELATIONS;

    LinkDefinition(TypeRegistry* registry, const std::string& name);

    std::vector<Relation*> getRelations() const;
    Link* instantiate(Synapse* synapse, Activation* input, Activation* output);

    SynapseDefinition* getSynapse() const;
    LinkDefinition* setSynapse(SynapseDefinition* synapse);

    ActivationDefinition* getInput() const;
    LinkDefinition* setInput(ActivationDefinition* input);

    ActivationDefinition* getOutput() const;
    LinkDefinition* setOutput(ActivationDefinition* output);

    std::string toString() const;

private:
    SynapseDefinition* synapse;
    ActivationDefinition* input;
    ActivationDefinition* output;
};

#endif // NETWORK_LINK_DEFINITION_H 