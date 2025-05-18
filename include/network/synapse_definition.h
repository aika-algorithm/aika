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
class NeuronDefinition;
class LinkDefinition;
class NetworkDirection;


#include <set>
#include <vector>

class SynapseDefinition : public Type {
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

    SynapseDefinition(TypeRegistry* registry, const std::string& name);

    std::vector<Relation> getRelations() const;

    Synapse* instantiate();
    Synapse* instantiate(Neuron* input, Neuron* output);

    SynapseSubType getSubType() const;
    SynapseDefinition* setSubType(SynapseSubType subType);

    NeuronDefinition* getInput() const;
    SynapseDefinition* setInput(NeuronDefinition* input);

    NeuronDefinition* getOutput() const;
    SynapseDefinition* setOutput(NeuronDefinition* outputDef);

    LinkDefinition* getLink() const;
    SynapseDefinition* setLink(LinkDefinition* link);

    bool isIncomingLinkingCandidate(const std::set<BSType*>& BSTypes) const;
    bool isOutgoingLinkingCandidate(const std::set<BSType*>& BSTypes) const;

    BSType* mapTransitionForward(BSType* bsType) const;
    BSType* mapTransitionBackward(BSType* bsType) const;

    std::vector<Transition*> getTransition() const;
    SynapseDefinition* setTransition(const std::vector<Transition*>& transition);

    NetworkDirection* getStoredAt() const;
    SynapseDefinition* setStoredAt(NetworkDirection* storedAt);

    SynapseDefinition* setTrainingAllowed(bool trainingAllowed);

    SynapseDefinition* getInstanceSynapseType() const;
    SynapseDefinition* setInstanceSynapseType(SynapseDefinition* instanceSynapseType);

    std::string toString() const;

private:
    SynapseSubType subType;

    LinkDefinition* link;

    NeuronDefinition* input;
    NeuronDefinition* output;

    std::vector<Transition*> transition;

    NetworkDirection* storedAt;

    bool trainingAllowed;

    SynapseDefinition* instanceSynapseType;
};

#endif // NETWORK_SYNAPSE_DEFINITION_H 