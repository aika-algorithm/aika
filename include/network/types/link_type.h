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
class SynapseType;


class LinkType : public Type {
public:
    static const RelationSelf SELF;
    static const RelationOne INPUT;
    static const RelationOne OUTPUT;
    static const RelationOne SYNAPSE;
    static const RelationOne PAIR_IN;
    static const RelationOne PAIR_OUT;
    // We can't use vector of abstract class Relation directly
    // static const std::vector<Relation> RELATIONS;

    LinkType(TypeRegistry* registry, const std::string& name);

    std::vector<Relation*> getRelations() const;
    Link* instantiate(Synapse* synapse, Activation* input, Activation* output);

    SynapseType* getSynapseType() const;
    void setSynapseType(SynapseType* synapseType);

    ActivationType* getInputType() const;
    void setInputType(ActivationType* inputType);

    ActivationType* getOutputType() const;
    void setOutputType(ActivationType* outputType);

    std::string toString() const;

private:
    SynapseType* synapseType;
    ActivationType* inputType;
    ActivationType* outputType;
};

#endif // NETWORK_LINK_DEFINITION_H 