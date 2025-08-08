#ifndef NETWORK_SYNAPSE_DEFINITION_H
#define NETWORK_SYNAPSE_DEFINITION_H

#include "fields/type.h"
#include "fields/type_registry.h"
#include "fields/relation.h"

#include "network/activation.h"
#include "network/binding_signal.h"
#include "network/transition.h"

// Forward declarations
class Synapse;
class Neuron;
class NeuronType;
class LinkType;
class NetworkDirection;


#include <set>
#include <vector>

class SynapseType : public Type {
public:

    static const RelationSelf SELF;
    static const RelationOne INPUT;
    static const RelationOne OUTPUT;
    static const RelationMany LINK;
    // Cannot store abstract class Relation in vector, need to use pointers instead
    // static const std::vector<Relation> RELATIONS;

    SynapseType(TypeRegistry* registry, const std::string& name);

    std::vector<Relation> getRelations() const;

    Synapse* instantiate();
    Synapse* instantiate(Neuron* input, Neuron* output);

    NeuronType* getInput() const;
    SynapseType* setInput(NeuronType* input);

    NeuronType* getOutput() const;
    SynapseType* setOutput(NeuronType* outputDef);

    LinkType* getLink() const;
    SynapseType* setLink(LinkType* link);

    int mapTransitionForward(int bsType) const;
    int mapTransitionBackward(int bsType) const;

    std::vector<Transition*> getTransitions() const;
    SynapseType* setTransitions(const std::vector<Transition*>& transitions);

    NetworkDirection* getStoredAt() const;
    SynapseType* setStoredAt(NetworkDirection* storedAt);

    SynapseType* getInstanceSynapseType() const;
    SynapseType* setInstanceSynapseType(SynapseType* instanceSynapseType);

    std::string toString() const;

private:
    SynapseType* instanceSynapseType;

    LinkType* link;

    NeuronType* input;
    NeuronType* output;

    std::vector<Transition*> transitions;

    NetworkDirection* storedAt;
};

#endif // NETWORK_SYNAPSE_DEFINITION_H 