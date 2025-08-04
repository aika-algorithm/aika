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
    enum class SynapseSubType {
        CONJUNCTIVE,
        DISJUNCTIVE
    };

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

    SynapseSubType getSubType() const;
    SynapseType* setSubType(SynapseSubType subType);

    NeuronType* getInput() const;
    SynapseType* setInput(NeuronType* input);

    NeuronType* getOutput() const;
    SynapseType* setOutput(NeuronType* outputDef);

    LinkType* getLink() const;
    SynapseType* setLink(LinkType* link);

    bool isIncomingLinkingCandidate(const std::set<int>& bsTypes) const;
    bool isOutgoingLinkingCandidate(const std::set<int>& bsTypes) const;

    int mapTransitionForward(int bsType) const;
    int mapTransitionBackward(int bsType) const;

    std::vector<Transition*> getTransition() const;
    SynapseType* setTransition(const std::vector<Transition*>& transition);

    NetworkDirection* getStoredAt() const;
    SynapseType* setStoredAt(NetworkDirection* storedAt);

    SynapseType* setTrainingAllowed(bool trainingAllowed);

    SynapseType* getInstanceSynapseType() const;
    SynapseType* setInstanceSynapseType(SynapseType* instanceSynapseType);

    std::string toString() const;

private:
    SynapseSubType subType;

    LinkType* link;

    NeuronType* input;
    NeuronType* output;

    std::vector<Transition*> transition;

    NetworkDirection* storedAt;

    bool trainingAllowed;

    SynapseType* instanceSynapseType;
};

#endif // NETWORK_SYNAPSE_DEFINITION_H 